package kafka

object ProducerExample extends App{

  import java.util.Properties
  import org.apache.kafka.clients.producer._

  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val TOPIC = "change-tracker-config"
  for (i <- -1 to 59){
    val record = new ProducerRecord(TOPIC, "key", s"Hello $i")
    producer.send(record)
  }

  val record = new ProducerRecord(TOPIC, "key", "the end "+ new java.util.Date)
  producer.send(record)

  producer.close()
}
