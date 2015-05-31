package com.ngneers

import akka.actor.SupervisorStrategy.{Escalate, Stop}
import akka.actor.{ActorInitializationException, OneForOneStrategy, Actor, Props}
import com.ngneers.flows.File2KafkaProcessor
import com.ngneers.processors.Kafka2CassandraProcessor
import com.softwaremill.react.kafka.ReactiveKafka
import scala.concurrent.duration._
import scala.language.postfixOps

object Runner {
  def props()(implicit kafka:ReactiveKafka) = Props(new Runner())
}

class Runner()(implicit kafka:ReactiveKafka) extends Actor {
  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case ex: ActorInitializationException => {
        context.system.shutdown()

        Stop
      }
      case _: Exception => Escalate
    }

  def receive = {
    case args@Kafka2CassandraProcessor.Args(_) => run(Kafka2CassandraProcessor.props(args))
    case args@File2KafkaProcessor.Args(_, _) => run(File2KafkaProcessor.props(args))
  }

  def run(props: Props) =
    context.actorOf(props) ! Processor.Run
}
