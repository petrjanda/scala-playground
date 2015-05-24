package com.ngneers

import akka.stream.scaladsl._
import org.reactivestreams.Publisher

import scala.language.postfixOps

trait StreamHelpers {
  def multiPublisherSource[T](publishers:List[Publisher[T]]) = Source[T]() { implicit b =>
    val merge = b.add(Merge[T](publishers.length))

    publishers.map(Source(_)).zipWithIndex.foreach {
      case (source, i) => b.addEdge(b.add(source), merge.in(i))
    }

    merge.out
  }

  def mapKeep[T, U](fn: T => U):Flow[T, (T, U), _] = Flow() { implicit b =>

    import akka.stream.scaladsl.FlowGraph.Implicits._

    val bcast = b.add(Broadcast[T](2))
    val zip = b.add(Zip[T, U])

    bcast.out(0) ~> zip.in0
    bcast.out(1) ~> Flow[T].map(fn(_)) ~> zip.in1

    (bcast.in, zip.out)
  }
}
