package flows

import com.sksamuel.elastic4s.ElasticClient
import akka.stream.scaladsl.Flow
import org.elasticsearch.common.settings.ImmutableSettings

import scala.concurrent.ExecutionContext

object EsFlow {
  case class IndexData(indexName: String, fields: Map[String, Any], id: String)
}

class EsFlow(host:String, port:Int) {
  import EsFlow._
  import com.sksamuel.elastic4s.ElasticDsl._

  val settings = ImmutableSettings.settingsBuilder()
    .put("http.enabled", false)

  println(host)
  println(port)

  val client: ElasticClient = ElasticClient.remote(settings.build(), host, port)

  def indexer(parallelism:Int)(implicit ec: ExecutionContext): Flow[IndexData, String, _] =
    Flow[IndexData].mapAsync(parallelism) { data =>
      client.execute {
        index into data.indexName / "respondent" fields data.fields id data.id
      }.map(_.getIndex)
    }
}
