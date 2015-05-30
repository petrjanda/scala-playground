package com.ngneers.flows

import akka.stream.scaladsl._
import org.reactivestreams.Subscriber

import scala.language.postfixOps

object SinkFlow {
  def apply[T](subscriber:Subscriber[T]): Flow[T, T, _] =
    this(Sink(subscriber))

  def apply[T](sink:Sink[T, _]): Flow[T, T, _] = Flow() { implicit b =>
    import akka.stream.scaladsl.FlowGraph.Implicits._

    val bcast = b.add(Broadcast[T](2))

    bcast.out(0) ~> b.add(sink)

    (bcast.in, bcast.out(1))
  }
}
