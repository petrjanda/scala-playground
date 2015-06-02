package com.ngneers.processors

import java.util.UUID

import akka.actor.Props
import com.ngneers.Processor
import com.ngneers.flows.SyncFileSource
import flows.EsFlow
import flows.EsFlow._

object File2EsProcessor {
  case class Args(path:String, uri:String, indexName:String, documentType:String)

  def props(args:Args) = Props(new File2EsProcessor(args.path, args.uri, args.indexName, args.documentType))
}

class File2EsProcessor(path:String, uri:String, indexName:String, documentType:String) extends Processor {
  implicit val ec = context.dispatcher

  val host :: port :: Nil = uri.split(":").toList
  val esFlow = new EsFlow(host, port.toInt)
  val indexer = esFlow.indexer(1)

  def source =
    SyncFileSource(path, "UTF-8")
      .map(i => { print("."); i })
      .map(line => IndexData(indexName, Map("line" -> line), UUID.randomUUID().toString))
      .via(indexer)

  override def shutdown(ex:Option[Throwable] = None): Unit = {
    esFlow.client.shutdown()

    system.shutdown()
  }
}
