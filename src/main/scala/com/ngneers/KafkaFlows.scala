package com.ngneers

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import com.softwaremill.react.kafka.ReactiveKafka
import kafka.serializer.StringEncoder

import scala.language.postfixOps

object KafkaFlows {
  def publisher(topic:String, groupId:String)(implicit kafka:ReactiveKafka, system:ActorSystem) = Flow() { implicit b =>
    import akka.stream.scaladsl.FlowGraph.Implicits._

    val subscriber = kafka.publish(topic, groupId, new StringEncoder())
    val bcast = b.add(Broadcast[String](2))
    val sink = b.add(Sink(subscriber))

    bcast.out(0) ~> sink

    (bcast.in, bcast.out(1))
  }
}
