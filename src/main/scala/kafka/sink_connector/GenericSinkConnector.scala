package kafka.sink_connector

import java.util
import java.util.Properties

import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.connect.sink.{SinkConnector, SinkRecord, SinkTask}
import org.slf4j.Logger

object GenericSinkConnector extends SinkTask{
  var log: Logger = _


  val task: SinkTask =  null
  val props = new Properties()

  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "sink_connect")

  override def put(records: util.Collection[SinkRecord]): Unit = {
    if(records.isEmpty) {
      return
    }
    val recordCount: Int = records.size()
    print(recordCount)
  }

  override def start(props: util.Map[String, String]): Unit = ???

  override def flush(offsets: util.Map[TopicPartition, OffsetAndMetadata]): Unit = ???

  override def stop(): Unit = ???

  override def version(): String = ???
}
