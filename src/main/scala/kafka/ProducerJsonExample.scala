package kafka

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import org.codehaus.jackson.JsonNode
import org.codehaus.jackson.map.ObjectMapper

object ProducerJsonExample extends App {


  import java.util.Properties
  import org.apache.kafka.clients.producer._

  def sendMessage(): Unit = {
    val topic: String = "test-tomysql"
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    //props.put("client.id", "KafkaProducer")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.connect.json.JsonSerializer")

    val kafkaProducer = new KafkaProducer[String, JsonNode](props)

    //val mapper = new ObjectMapper with ScalaObjectMapper
    //mapper.registerModule(DefaultScalaModule)
    //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    //def toJson(value: Any): String = {
      //mapper.writeValueAsString(value)
    //}

    val jsonString =
      s"""{
         |"id":"0001",
         |"name":"Peter",
         |}
         |""".stripMargin

    //val jsonNode: JsonNode = mapper.readTree(jsonString)
    //val rec = new ProducerRecord[String, JsonNode](topic, jsonNode)
    //kafkaProducer.send(rec)

  }
}
