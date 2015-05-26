package com.ngneers.processors

import java.nio.charset.CodingErrorAction

import akka.stream.scaladsl.Source
import com.ngneers.{KafkaFlows, Processor}
import com.softwaremill.react.kafka.ReactiveKafka

import scala.io.Codec

class ReadFileProcessor(path:String, topic:String, app:String)
                       (implicit kafka:ReactiveKafka) extends Processor[String] {
  implicit val codec = Codec("UTF-8")
  codec.onMalformedInput(CodingErrorAction.REPLACE)
  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

  val file = io.Source.fromFile(path)
  val lines = file.getLines()

  override def shutdown(ex:Option[Throwable] = None): Unit = {
    super.shutdown(ex)

    file.close()
  }

  def source = Source(() => lines)
    .map(i => { print("."); i })
    .via(KafkaFlows.publisher(topic, app))
}
