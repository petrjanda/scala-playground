package com.ngneers.processors

import java.nio.charset.CodingErrorAction

import akka.stream.Supervision
import akka.stream.scaladsl.{Sink, Source}
import com.ngneers.Processor
import com.ngneers.flows.SinkFlow
import com.softwaremill.react.kafka.ReactiveKafka
import kafka.common.FailedToSendMessageException
import kafka.producer.KafkaProducer
import kafka.serializer.StringEncoder

import scala.io.Codec

class File2KafkaProcessor(path:String, topic:String)
                         (implicit kafka:ReactiveKafka) extends Processor {

  override val decider: Supervision.Decider = ex => ex match {
    case ex: FailedToSendMessageException => Supervision.Resume

    case _ => {
      shutdown(Some(ex))

      Supervision.Stop
    }
  }

  implicit val codec = Codec("UTF-8")
  codec.onMalformedInput(CodingErrorAction.REPLACE)
  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

  val file = io.Source.fromFile(path)
  val lines = file.getLines()
  val encoder = new StringEncoder()
  val producer = new KafkaProducer(topic, kafka.host)

  def source =
    Source(() => lines)
      .map(i => { print("."); i })
      .map(i => { producer.producer.send(producer.kafkaMesssage(encoder.toBytes(i), null)) })


  override def shutdown(ex:Option[Throwable] = None): Unit = {
    super.shutdown(ex)

    producer.close()
    file.close()
  }
}
