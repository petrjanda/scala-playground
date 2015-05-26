package com.ngneers

import akka.stream.scaladsl.{Merge, Source}
import org.reactivestreams.Publisher

object MultiPublisherSource {
  def apply[T](publishers: List[Publisher[T]]) = Source[T]() { implicit b =>
    require(publishers.nonEmpty, "MultiPublisherSource needs at least one publisher")

    val merge = b.add(Merge[T](publishers.length))

    publishers.map(Source(_)).zipWithIndex.foreach {
      case (source, i) => b.addEdge(b.add(source), merge.in(i))
    }

    merge.out
  }
}
