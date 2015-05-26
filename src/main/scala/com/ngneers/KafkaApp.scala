package com.ngneers

import akka.actor.ActorSystem
import akka.stream.{ActorFlowMaterializerSettings, ActorFlowMaterializer}
import akka.stream.scaladsl.{Sink, Source}

import scala.language.postfixOps
import scala.util.{Success, Failure, Try}
import scala.util.control.NonFatal
import akka.stream.Supervision

trait KafkaApp {
  val decider: Supervision.Decider = exc => exc match {
    case _ => {
      println("error")

      Supervision.Stop
    }
  }

  implicit lazy val actorSystem = ActorSystem("ReactiveKafka")

  implicit lazy val materializer = ActorFlowMaterializer(
//    ActorFlowMaterializerSettings(actorSystem).withSupervisionStrategy(decider)
  )

  def execute(fn: => Source[_, Unit]): Unit = Try {
    fn.to(Sink.onComplete {
      case Success(_) => shutdown()
      case Failure(ex) => {
        ex.printStackTrace()
        shutdown()
      }
    }).run()
  }.recover {
    case NonFatal(e) => {
      e.printStackTrace()
      shutdown()
    }
  }

  def shutdown(): Unit = {
    actorSystem.shutdown()
  }
}
