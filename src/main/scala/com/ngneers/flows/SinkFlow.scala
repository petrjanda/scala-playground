package com.ngneers.flows

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import com.softwaremill.react.kafka.ReactiveKafka
import kafka.serializer.StringEncoder
import org.reactivestreams.Subscriber

import scala.language.postfixOps

object SinkFlow {
  def apply[T](subscriber:Subscriber[T])(implicit system:ActorSystem): Flow[T, T, _] = this(Sink(subscriber))

  def apply[T](snk:Sink[T, Unit])(implicit system:ActorSystem): Flow[T, T, _] = Flow() { implicit b =>
    import akka.stream.scaladsl.FlowGraph.Implicits._

    val bcast = b.add(Broadcast[T](2))
    val sink = b.add(snk)

    bcast.out(0) ~> sink

    (bcast.in, bcast.out(1))
  }
}
