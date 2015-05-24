package com.ngneers

import akka.actor.ActorSystem
import akka.stream.ActorFlowMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.language.postfixOps
import scala.util.{Success, Failure, Try}
import scala.util.control.NonFatal

trait KafkaApp {
  implicit lazy val actorSystem = ActorSystem("ReactiveKafka")
  implicit lazy val materializer = ActorFlowMaterializer()

  def execute(fn: => Source[String, Unit]): Unit = Try {
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
