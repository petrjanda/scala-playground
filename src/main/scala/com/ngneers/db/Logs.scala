package com.ngneers.db

import java.util.UUID

import com.datastax.driver.core.Row
import com.ngneers.domain.Log
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.connectors.{KeySpace, SimpleCassandraConnector}
import com.websudos.phantom.dsl.{StringColumn, UUIDColumn}
import com.websudos.phantom.keys.PartitionKey

import scala.language.postfixOps

object Logs extends Logs {
  def add(item:Log) =
    insert
      .value(_.id, item.id)
      .value(_.log, item.log)
      .future

  def setup =
    create.ifNotExists.future
}

class Logs extends CassandraTable[Logs, Log] with SimpleCassandraConnector {
  implicit val keySpace = KeySpace("kafka")

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object log extends StringColumn(this)

  def fromRow(row: Row): Log = {
    Log(id(row), log(row))
  }
}