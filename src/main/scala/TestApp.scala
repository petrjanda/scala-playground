import java.net.InetSocketAddress

import akka.actor.ActorSystem
import com.ngneers._
import com.ngneers.processors.{File2KafkaProcessor, Kafka2CassandraProcessor}
import com.softwaremill.react.kafka.ReactiveKafka

import scala.language.postfixOps
import sys.process._

object TestApp extends App {
  val app :: tail = args.toList

  val ip = "boot2docker ip" !!

  Conf.cassandra = CassandraConf(Set(new InetSocketAddress(ip, 9042)))

  implicit val system = ActorSystem("test")
  implicit val kafka = new ReactiveKafka(
    host = s"${ip.trim}:9092",
    zooKeeperHost = s"${ip.trim}:2181"
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

  system.actorOf(Runner.props(), "runner") ! msg
}

object Conf {
  var cassandra: CassandraConf = null
}

case class CassandraConf(hosts:Set[InetSocketAddress])