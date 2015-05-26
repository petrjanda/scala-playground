package com.ngneers

import akka.stream.scaladsl._

import scala.language.postfixOps

trait StreamHelpers {
  def mapKeep[T, U](fn: T => U):Flow[T, (T, U), _] = Flow() { implicit b =>

    import akka.stream.scaladsl.FlowGraph.Implicits._

    val bcast = b.add(Broadcast[T](2))
    val zip = b.add(Zip[T, U])

    bcast.out(0) ~> zip.in0
    bcast.out(1) ~> Flow[T].map(fn(_)) ~> zip.in1

    (bcast.in, zip.out)
  }
}
