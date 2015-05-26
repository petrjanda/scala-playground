package com.ngneers.processors

import java.nio.charset.CodingErrorAction

import akka.stream.scaladsl.{Sink, Source}
import com.ngneers.Processor
import com.ngneers.flows.SinkFlow
import com.softwaremill.react.kafka.ReactiveKafka
import kafka.common.FailedToSendMessageException
import kafka.serializer.StringEncoder

import scala.io.Codec

class File2KafkaProcessor(path:String, topic:String)
                       (implicit kafka:ReactiveKafka) extends Processor {
  implicit val codec = Codec("UTF-8")
  codec.onMalformedInput(CodingErrorAction.REPLACE)
  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

  val file = io.Source.fromFile(path)
  val lines = file.getLines()
  val subscriber = kafka.publish(topic, name, new StringEncoder())

  def source =
    Source(() => lines)
      .map(i => { print("."); i })
      .via(SinkFlow(subscriber))

  override def shutdown(ex:Option[Throwable] = None): Unit = {
    ex.map(
      _ match {
        case ex:FailedToSendMessageException => print("fuck, kafka is down!")
      }
    )

    super.shutdown(ex)

    file.close()
  }
}
