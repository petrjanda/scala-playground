import akka.stream.scaladsl._
import com.ngneers.db.Logs
import com.ngneers.domain.Log
import com.ngneers.{KafkaApp, KafkaFlows, MultiPublisherSource, StreamHelpers}
import com.softwaremill.react.kafka.ReactiveKafka
import kafka.serializer.StringDecoder

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object TestApp extends App with KafkaApp with StreamHelpers {
  val app :: tail = args.toList

  implicit val kafka = new ReactiveKafka(
    host = "localhost:9092",
    zooKeeperHost = "localhost:2181"
  )

  app match {
    case "index" => {
      require(tail.nonEmpty, "App needs to have at least one topic to listen to!")
      println(tail)

      execute {
        Await.result(Logs.setup, 5 seconds)
        
        val publishers = tail.map(kafka.consume(_, app, new StringDecoder()))

        MultiPublisherSource(publishers)
          .map(Log(_))
          .mapAsync(1) { Logs.add(_) }
          .map(i => { print("."); i })
      }
    }

    case "read" => {
      val topic :: path :: Nil = tail.toList

      execute {
        val file = io.Source.fromFile(path)
        val lines = file.getLines()

        Source(() => lines)
          .map(i => { print("."); i })
          .via(KafkaFlows.publisher(topic, app))
      }
    }
  }
}
