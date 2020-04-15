package kafka.sink_connector

import java.util
import java.util.Properties
import java.sql.{Connection, DriverManager, Timestamp}

import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.connect.sink.{SinkConnector, SinkRecord, SinkTask, SinkTaskContext}
import org.slf4j.Logger

abstract class  GenericSinkConnector extends SinkTask{
  var log: Logger = _
  var db: Connection = _
  var ip_addr: String = "localhost"
  val dbUrl: String = "jdbc:mysql://"+ ip_addr + ":3306/test_db?user=root&password=test_pass"
  db = DriverManager.getConnection(dbUrl)

  override def initialize(context: SinkTaskContext): Unit = super.initialize(context)

  override def put(records: util.Collection[SinkRecord]): Unit = {
    if(records.isEmpty) {
      return
    }
    val recordCount: Int = records.size()
    print(recordCount)
  }

  override def flush(offsets: util.Map[TopicPartition, OffsetAndMetadata]): Unit = ???

  override def stop(): Unit = db.close()

  override def version(): String = getClass.getPackage.getImplementationVersion
}
