import akka.actor.{Actor, ActorSystem, Props}
import com.ngneers._
import com.ngneers.processors.{File2KafkaProcessor, Kafka2CassandraProcessor}
import com.softwaremill.react.kafka.ReactiveKafka

import scala.language.postfixOps

object TestApp extends App {
  val app :: tail = args.toList

  implicit val system = ActorSystem("test")
  implicit val kafka = new ReactiveKafka(
    host = "192.168.59.103:9092",
    zooKeeperHost = "192.168.59.103:2181"
  )

  val msg = app match {
    case "index" => {
      require(tail.nonEmpty, "App needs to have at least one topic to listen to!")

      Kafka2CassandraProcessor.Args(tail)
    }

    case "read" => {
      val topic :: path :: Nil = tail.toList

      File2KafkaProcessor.Args(topic, path)
    }
  }

  system.actorOf(Props[Runner]) ! msg
}

