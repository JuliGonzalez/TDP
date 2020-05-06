package spark
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010._
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe


object StreamingSparkString extends App {
  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> "localhost:9092",
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> "streaming_string_example",
    "auto.offset.reset" -> "earliest",
    "enable.auto.commit" -> (false: java.lang.Boolean)
  )
  val conf = new SparkConf().setMaster("local[*]").setAppName("EjemploSparkKafka_string")
  val sc = new SparkContext(conf)
  sc.setLogLevel("ERROR")
  val streamingContext = new StreamingContext(sc, Seconds(2))
  val topics = Array("test-tomysql")
  /*val stream = KafkaUtils.createDirectStream[String, String](
    streamingContext,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams)
  )*/
  val kafkaStream = KafkaUtils.createDirectStream[String,String](
    streamingContext,
    PreferConsistent,
    Subscribe[String, String](topics, kafkaParams)
  )

  //stream.map(record => {(record.key, record.value)
  //println(record.key, record.value)})
  println("================================================================")
  println("================================================================")
  println("================================================================")
  println(kafkaStream)
  streamingContext.start()
  streamingContext.awaitTermination()
}
