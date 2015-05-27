package com.ngneers

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorFlowMaterializer, ActorFlowMaterializerSettings, Supervision}
import kafka.common.FailedToSendMessageException

import scala.util.{Failure, Success}

trait Processor {
  def source:Source[_, Unit]

  val name = "processor"

  val decider: Supervision.Decider = ex => {
    shutdown(Some(ex))

    Supervision.Stop
  }

  implicit lazy val system = ActorSystem(name)

  implicit lazy val mat = ActorFlowMaterializer(
    ActorFlowMaterializerSettings(system)
      .withSupervisionStrategy(decider)
  )

  def run(): Unit =
    source.to(Sink.onComplete {
      case Success(_) => shutdown()
      case Failure(ex) => shutdown(Some(ex))
    }).run()

  def shutdown(ex:Option[Throwable] = None): Unit = {
    system.shutdown()
  }
}
