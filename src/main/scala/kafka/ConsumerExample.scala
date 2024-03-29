package kafka

import java.util
import scala.collection.JavaConverters._

object ConsumerExample extends App{

  import java.util.Properties
  import org.apache.kafka.clients.consumer.KafkaConsumer

  val TOPIC = "test-tomysql"

  val props = new Properties()

  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
  props.put("group.id", "something")

  val consumer = new KafkaConsumer[String, String](props)

  consumer.subscribe(util.Collections.singletonList(TOPIC))

  while(true){
    val records = consumer.poll(100)
    for (record<-records.asScala){
      println(record)
    }
  }

}
