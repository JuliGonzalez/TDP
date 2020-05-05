package spark

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.common.serialization.Deserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Encoder, SparkSession}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object SparkStreamingAvro extends App {

  val sc = new SparkConf().setMaster("local[*]").setAppName("kafka-spark-tester")
  //batch every second
  val streamContext = new StreamingContext(sc, Seconds(1))
  val spark = SparkSession.builder().appName("kafka-spark-tester")
    .getOrCreate()

  //add settings for schema registry url, used to get deser
  val schemaRegUrl = "http://localhost:8081"
  val client = new CachedSchemaRegistryClient(schemaRegUrl, 100)
  val topic = "test-tomysql"
  //subscribe to kafka
  val df = spark
    .readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", topic)
    .option("startingOffsets", "latest")
    .load()

  //add confluent kafka avro deserializer, needed to read messages appropriately
  val deserializer = new KafkaAvroDeserializer(client).asInstanceOf[Deserializer[GenericRecord]]

  //df.take(1).foreach(println)
  //needed to convert column select into Array[Bytes]
  import spark.implicits._

  val results = df.select(col("value").as[Array[Byte]]).map { rawBytes: Array[Byte] =>
    //read the raw bytes from spark and then use the confluent deserializer to get the record back
    val decoded = deserializer.deserialize(topic, rawBytes)
    val recordId = decoded.get("modeData").asInstanceOf[org.apache.avro.util.Utf8].toString
    println(recordId)
    //perform other transformations here!!
    recordId
  }


  results.writeStream
    //.outputMode("append")
    //.format("console")
    .format("csv")        // can be "orc", "json", "csv", etc.
    .option("path", "/home/juliangonzalez/IdeaProjects/TDP/src/main/scala/spark")
    .option("checkpointLocation", "/home/juliangonzalez/IdeaProjects/TDP/src/main/scala/spark")
    .option("truncate", "false")
    .start()
    .awaitTermination()

}