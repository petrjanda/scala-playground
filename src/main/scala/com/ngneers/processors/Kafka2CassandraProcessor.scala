package com.ngneers.processors

import com.ngneers.db.Logs
import com.ngneers.domain.Log
import com.ngneers.{MultiPublisherSource, Processor}
import com.softwaremill.react.kafka.ReactiveKafka
import kafka.serializer.StringDecoder

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

class Kafka2CassandraProcessor(topics:List[String])
                         (implicit kafka:ReactiveKafka) extends Processor {
  require(topics.nonEmpty, "App needs to have at least one topic to listen to!")

  Await.result(Logs.setup, 5 seconds)

  val publishers = topics.map(kafka.consume(_, name, new StringDecoder()))

  def source =
    MultiPublisherSource(publishers)
      .map(Log(_))
      .mapAsync(1) { Logs.add(_) }
      .map(i => { print("."); i })
}
