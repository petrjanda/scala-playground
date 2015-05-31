package com.ngneers.flows

import java.io.File

import akka.stream.io.SynchronousFileSource
import akka.stream.scaladsl.Source

object SyncFileSource {
  def apply(path:String, encoding:String): Source[String, _] =
    SynchronousFileSource(new File(path)).map(i => i.decodeString("UTF-8"))
}
