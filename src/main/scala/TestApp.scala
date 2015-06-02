import java.net.InetSocketAddress

import akka.actor.ActorSystem
import com.ngneers._
import com.ngneers.processors.{File2EsProcessor, File2KafkaProcessor, Kafka2CassandraProcessor}
import com.softwaremill.react.kafka.ReactiveKafka

import scala.language.postfixOps
import sys.process._

object TestApp extends App {
  val app :: tail = args.toList

  implicit val system = ActorSystem("test")
  implicit val kafka = connectKafka()

  val msg = app match {
    case "index" => {
      require(tail.nonEmpty, "App needs to have at least one topic to listen to!")

      Kafka2CassandraProcessor.Args(tail)
    }

    case "read" => {
      val topic :: path :: Nil = tail.toList

      File2KafkaProcessor.Args(topic, path)
    }

    case "read-es" => {
      val path :: uri :: Nil = tail.toList

      File2EsProcessor.Args(path, uri, "foo", "bar")

    }
  }

  system.actorOf(Runner.props(), "runner") ! msg

  private def connectKafka(): ReactiveKafka = {
    val ip = "docker-machine ip" !!

    new ReactiveKafka(
      host = s"${ip.trim}:9092",
      zooKeeperHost = s"${ip.trim}:2181"
    )
  }
}

object Conf {
  var cassandra: CassandraConf = null
}

case class CassandraConf(hosts:Set[InetSocketAddress])