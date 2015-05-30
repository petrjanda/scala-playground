package com.ngneers

import akka.actor.{Actor, Props}
import com.ngneers.processors.{File2KafkaProcessor, Kafka2CassandraProcessor}
import com.softwaremill.react.kafka.ReactiveKafka

object Runner {
  def props()(implicit kafka:ReactiveKafka) = Props(new Runner())
}

class Runner()(implicit kafka:ReactiveKafka) extends Actor {
  def receive = {
    case args@Kafka2CassandraProcessor.Args(_) => run(Kafka2CassandraProcessor.props(args))
    case args@File2KafkaProcessor.Args(_, _) => run(File2KafkaProcessor.props(args))
  }

  def run(props: Props) =
    context.actorOf(props) ! Processor.Run
}
