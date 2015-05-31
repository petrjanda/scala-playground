package com.ngneers.flows

import akka.stream.scaladsl.Flow
import kafka.producer.KafkaProducer
import kafka.serializer.StringEncoder

class StreamKafkaProducer(producer:KafkaProducer) {
  val encoder = new StringEncoder()

  def flow: Flow[String, String, _] = Flow[String].map(i => {
    // Get around System.exit(1) handler for any event in the case of producer issues
    // See https://github.com/stealthly/scala-kafka/blob/master/src/main/scala/KafkaProducer.scala#L106-114
    producer.producer.send(
      producer.kafkaMesssage(encoder.toBytes(i), null)
    )

    i
  })

  def close() = producer.close()
}
