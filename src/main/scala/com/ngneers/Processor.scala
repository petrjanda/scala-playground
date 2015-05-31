package com.ngneers

import akka.actor.Actor
import akka.stream.scaladsl.{Sink, Source}
import akka.stream.{ActorFlowMaterializer, ActorFlowMaterializerSettings, Supervision}

import scala.util.{Failure, Success}

object Processor {
  case object Run
  case class Shutdown(ex:Option[Throwable] = None)
}

abstract class Processor extends Actor {
  import com.ngneers.Processor._

  def source:Source[_, Unit]

  val name = "processor"

  val decider: Supervision.Decider = ex => {
    shutdown(Some(ex))

    Supervision.Stop
  }

  implicit lazy val system = context.system

  implicit lazy val mat = ActorFlowMaterializer(
    ActorFlowMaterializerSettings(system)
      .withSupervisionStrategy(decider)
  )

  override def postStop(): Unit = shutdown()

  def receive = {
    case Run => run()
    case Shutdown(ex) => shutdown(ex)
  }

  def run(): Unit = {
    println("run ...")
    source.to(Sink.onComplete {
      case Success(_) => shutdown()
      case Failure(ex) => shutdown(Some(ex))
    }).run()
  }

  def shutdown(ex:Option[Throwable] = None): Unit = {}
}
