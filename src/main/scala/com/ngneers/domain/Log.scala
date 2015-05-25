package com.ngneers.domain

import java.util.UUID

object Log {
  def apply(str:String): Log = this(UUID.randomUUID(), str)
}

case class Log(id:UUID, log:String)

