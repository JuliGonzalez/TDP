package kafka

import java.util
import akka.actor.ActorSystem

import kafka.sink_connector._
import org.apache.kafka.connect.sink.SinkTaskContext

object SinkData extends App{
  val sink: GenericSinkConnector = null
  val props = new util.HashMap[String, String]()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "sink_connector_test")


  //sink.initialize(SinkTaskContext)
  //sink.start(props)
}
