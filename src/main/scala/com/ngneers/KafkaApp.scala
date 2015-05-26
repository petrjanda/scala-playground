package com.ngneers

import akka.actor.ActorSystem
import akka.stream.{ActorFlowMaterializerSettings, ActorFlowMaterializer}
import akka.stream.scaladsl.{Sink, Source}

import scala.language.postfixOps
import scala.util.{Success, Failure, Try}
import scala.util.control.NonFatal
import akka.stream.Supervision

trait KafkaApp {
  val decider: Supervision.Decider = ex => ex match {
    case _ => Supervision.Stop
  }

  implicit lazy val system = ActorSystem("ReactiveKafka")

  implicit lazy val mat = ActorFlowMaterializer(
    ActorFlowMaterializerSettings(system)
      .withSupervisionStrategy(decider)
  )

  def runProcessor(processor:Processor): Unit = Try { processor.run() }.recover {
    case NonFatal(ex) => shutdown(Some(ex))
  }

  def shutdown(ex:Option[Throwable] = None): Unit = {
    ex.map(_.printStackTrace())

    system.shutdown()
  }
}
