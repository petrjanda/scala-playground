package com.ngneers.db

import java.util.UUID

import com.datastax.driver.core.Row
import com.ngneers.domain.Log
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.connectors.{KeySpace, SimpleCassandraConnector}
import com.websudos.phantom.dsl.{StringColumn, UUIDColumn}
import com.websudos.phantom.keys.PartitionKey

import scala.language.postfixOps

object LogsTable extends LogsTable {
  def add(item:Log) =
    insert
      .value(_.id, item.id)
      .value(_.log, item.log)
      .future

  def setup =
    create.ifNotExists.future
}

class LogsTable extends CassandraTable[LogsTable, Log] with SimpleCassandraConnector {
  implicit val keySpace = KeySpace("test")

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object log extends StringColumn(this)

  def fromRow(row: Row): Log = {
    Log(id(row), log(row))
  }
}