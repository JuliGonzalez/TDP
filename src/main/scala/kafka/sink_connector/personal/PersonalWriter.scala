package kafka.sink_connector.personal

import java.sql.Connection
import java.util

import com.google.common.base.Function
import com.google.common.collect.Multimaps
import kafka.client.personal.PersonalJdbcClient
import kafka.sink_connector.config.personal.PersonalConfig
import kafka.sink_connector.BaseWriter
import org.apache.kafka.connect.sink.SinkRecord
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._


class PersonalWriter(config: PersonalConfig, hanaClient: PersonalJdbcClient,
                     tableCache: scala.collection.mutable.Map[String, PersonalSinkRecordsCollector])
  extends BaseWriter {

  private val log: Logger = LoggerFactory.getLogger(getClass)
  private var connection:Connection = null

  override def initializeConnection(): Unit = {
    if(connection == null || connection.isClosed ) {
      connection = hanaClient.getConnection
    }
    else if(!connection.isValid(120))
    {
      connection.close()
      connection = hanaClient.getConnection
    }
    connection.setAutoCommit(false)
  }


  override def write(records: util.Collection[SinkRecord]): Unit = {
    log.info("write records to HANA")
    log.info("initialize connection to HANA")

    initializeConnection()

    val topicMap = Multimaps.index(records, new Function[SinkRecord, String] {
      override def apply(sinkRecord: SinkRecord) = sinkRecord.topic()
    }).asMap().toMap

    for ((topic, recordsPerTopic) <- topicMap) {
      var table = config.topicProperties(topic).get("table.name").get
      if (table.contains("${topic}")) {
        table = table.replace("${topic}", topic)
      }

      val recordsCollector: Option[PersonalSinkRecordsCollector] = tableCache.get(table)

      recordsCollector match {
        case None =>
          val tableRecordsCollector = new PersonalSinkRecordsCollector(table, hanaClient, connection, config)
          tableCache.put(table, tableRecordsCollector)
          tableRecordsCollector.add(recordsPerTopic.toSeq)
        case Some(tableRecordsCollector) =>
          if (config.autoSchemaUpdateOn) {
            tableRecordsCollector.tableConfigInitialized = false
          }
          tableRecordsCollector.add(recordsPerTopic.toSeq)
      }
    }
    flush(tableCache.toMap)
    log.info("flushing records to Personal successful")
  }

  private def flush(tableCache: Map[String, PersonalSinkRecordsCollector]): Unit = {
    log.info("flush records into HANA")
    for ((table, recordsCollector) <- tableCache) {
        recordsCollector.flush()
    }
    hanaClient.commit(connection)
  }

}
