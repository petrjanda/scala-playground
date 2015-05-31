package com.ngneers.flows

import akka.actor.Props
import akka.stream.Supervision
import com.ngneers.Processor
import com.softwaremill.react.kafka.ReactiveKafka
import kafka.common.FailedToSendMessageException
import kafka.producer.KafkaProducer

object File2KafkaProcessor {
  case class Args(topic:String, path:String)

  def props(args:Args)(implicit kafka:ReactiveKafka) = Props(new File2KafkaProcessor(args.path, args.topic))
}

class File2KafkaProcessor(path:String, topic:String)
                         (implicit kafka:ReactiveKafka) extends Processor {

  override val decider: Supervision.Decider = ex => ex match {
    case ex: FailedToSendMessageException => {
      ex.printStackTrace()

      Supervision.Resume
    }

    case _ => {
      shutdown(Some(ex))

      Supervision.Stop
    }
  }
  
  val streamKafkaProducer = new StreamKafkaProducer(new KafkaProducer(
    topic = topic,
    brokerList = kafka.host,
    synchronously = false,
    batchSize = 10,
    requestRequiredAcks = 1
  ))

  def source =
    SyncFileSource(path, "UTF-8")
      .map(i => { print("."); i })
      .via(streamKafkaProducer.flow)


  override def shutdown(ex:Option[Throwable] = None): Unit = {
    system.shutdown()
  }
}

