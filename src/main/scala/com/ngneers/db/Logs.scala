package com.ngneers.db

import java.net.InetSocketAddress
import java.util.UUID

import com.datastax.driver.core.Row
import com.ngneers.domain.Log
import com.websudos.phantom.CassandraTable
import com.websudos.phantom.connectors.{CassandraProperties, DefaultCassandraManager, KeySpace, SimpleCassandraConnector}
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



object MyCustomManager extends DefaultCassandraManager(Set(new InetSocketAddress("192.168.59.103", 9042)))

trait Connector extends SimpleCassandraConnector {
  override val manager = MyCustomManager
}

class Logs extends CassandraTable[Logs, Log] with Connector {
  implicit val keySpace = KeySpace("kafka")

  object id extends UUIDColumn(this) with PartitionKey[UUID]
  object log extends StringColumn(this)

  def fromRow(row: Row): Log = {
    Log(id(row), log(row))
  }
}