package com.ngneers.processors

//import com.ngneers.db.Logs
//import com.ngneers.domain.Log

import akka.actor.Props
import com.ngneers.{MultiPublisherSource, Processor}
import com.softwaremill.react.kafka.ReactiveKafka
import kafka.serializer.StringDecoder
import org.reactivestreams.Publisher

import scala.language.postfixOps

object Kafka2CassandraProcessor {
  case class Args(topics:List[String])

  def props(args:Args)(implicit kafka:ReactiveKafka) = Props(new Kafka2CassandraProcessor(args.topics))
}

class Kafka2CassandraProcessor(topics:List[String])
                         (implicit kafka:ReactiveKafka) extends Processor {
//  Await.result(Logs.setup, 5 seconds)

  var publishers: List[Publisher[String]] = _

  override def preStart(): Unit =
    publishers = topics.map(
      kafka.consume(_, name, new StringDecoder())
    )

  def source =
    MultiPublisherSource(publishers)
//      .map(Log(_))
//      .mapAsync(1) { Logs.add(_) }
      .map(i => { print(i); i })
}
