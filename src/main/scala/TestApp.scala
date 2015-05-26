import com.ngneers._
import com.ngneers.processors.{File2KafkaProcessor, Kafka2CassandraProcessor}
import com.softwaremill.react.kafka.ReactiveKafka

import scala.language.postfixOps
import scala.util.Try
import scala.util.control.NonFatal

object TestApp extends App {
  val app :: tail = args.toList

  implicit val kafka = new ReactiveKafka(
    host = "localhost:9092",
    zooKeeperHost = "localhost:2181"
  )

  app match {
    case "index" => {
      runProcessor { new Kafka2CassandraProcessor(tail) }
    }

    case "read" => {
      val topic :: path :: Nil = tail.toList

      runProcessor { new File2KafkaProcessor(path, topic) }
    }
  }

  def runProcessor(processor:Processor): Unit = Try { processor.run() }.recover {
    case NonFatal(ex) => processor.shutdown(Some(ex))
  }
}

