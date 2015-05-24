import akka.stream.scaladsl._
import com.ngneers.{StreamHelpers, KafkaFlows, KafkaApp}
import com.softwaremill.react.kafka.ReactiveKafka
import kafka.serializer.StringDecoder

import scala.language.postfixOps

object UppercaseApp extends App with KafkaApp with StreamHelpers {
  val topics = args.toList

  execute {
    implicit val kafka = new ReactiveKafka(
      host = "localhost:9092",
      zooKeeperHost = "localhost:2181"
    )

    val publishers = topics.map(topic =>
      kafka.consume(topic, "uppercase", new StringDecoder())
    )

    multiPublisherSource(publishers)
      .map(l => { println(s"sub --> $l"); l })
  }
}

object FeederApp extends App with KafkaApp with StreamHelpers {
  val topic :: path :: Nil = args.toList

  execute {
    implicit val kafka = new ReactiveKafka(
      host = "localhost:9092",
      zooKeeperHost = "localhost:2181"
    )

    val file = io.Source.fromFile(path)
    val lines = file.getLines()

    Source(() => lines)
      .via(KafkaFlows.publisher(topic, "feeder"))
  }
}
