package com.ngneers

import akka.actor.{Actor, Props}
import com.ngneers.processors.{File2KafkaProcessor, Kafka2CassandraProcessor}

class Runner extends Actor {
  def receive = {
    case Kafka2CassandraProcessor.Args(topics) => run(Props[Kafka2CassandraProcessor])
    case File2KafkaProcessor.Args(topic, path) => run(Props[File2KafkaProcessor])
  }

  def run(props: Props) =
    context.actorOf(props) ! Processor.Run
}
