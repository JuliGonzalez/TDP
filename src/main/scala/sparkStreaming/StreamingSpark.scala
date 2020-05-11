package sparkStreaming

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
import org.slf4j.Logger

object StreamingSpark {

  def exampleStreaming(): Unit = {
    val conf = new SparkConf().setMaster("local[*]").setAppName("EjemploSparkKafka")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")
    val ssc = new StreamingContext(sc, Seconds(2))
    val topics = "test-tomysql" // lista de Topic de Kafka
    val brokers = "localhost:9092" // broker de Kafka
    val groupId = "Kafka-SparkConsumer" // Identificador del grupo.
    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, Object](
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> brokers,
      ConsumerConfig.GROUP_ID_CONFIG -> groupId,
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer],
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer])
    val messages = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topicsSet, kafkaParams))
    val lines = messages.map(_.value)
    val words = lines.flatMap(_.split(" "))
    val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
    println("================================================================")
    println("================================================================")
    wordCounts.print()

    // Start the computation
    ssc.start()
    ssc.awaitTermination()
  }
    def main(args: Array[String]): Unit = {


      exampleStreaming()
    }
}
