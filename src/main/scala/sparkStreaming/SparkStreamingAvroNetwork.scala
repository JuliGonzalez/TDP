package sparkStreaming

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import io.confluent.kafka.serializers.KafkaAvroDeserializer
import org.apache.avro.Schema
import org.apache.avro.generic.GenericRecord
import org.apache.commons.codec.StringDecoder
import org.apache.kafka.common.serialization.Deserializer
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{Encoder, SparkSession}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StringType, StructType}
import org.apache.spark.streaming.kafka010.KafkaUtils
import za.co.absa.abris.avro.functions.from_avro
import za.co.absa.abris.avro.read.confluent.SchemaManager


object SparkStreamingAvroNetwork extends App {
  val sc = new SparkConf().setMaster("local[*]").setAppName("kafka-spark-avro-network")
  val streamContext = new StreamingContext(sc, Seconds(1))
  val scl = streamContext.sparkContext
  scl.setLogLevel("ERROR")
  val spark = SparkSession.builder().appName("kafka-spark-avro-network").getOrCreate()

  val schemaRegUrl = "http://localhost:8081"
  val client = new CachedSchemaRegistryClient(schemaRegUrl, 100)
  val topic = "test-tomysql"

  val schemaRegistryConf = Map(
    SchemaManager.PARAM_SCHEMA_REGISTRY_URL     -> "http://localhost:8081",
    SchemaManager.PARAM_SCHEMA_REGISTRY_TOPIC   -> "test-tomysql",
    SchemaManager.PARAM_VALUE_SCHEMA_NAMING_STRATEGY -> SchemaManager.SchemaStorageNamingStrategies.TOPIC_NAME, // choose a subject name strategy
    SchemaManager.PARAM_VALUE_SCHEMA_ID              -> "latest", // set to "latest" if you want the latest schema version to used
    SchemaManager.PARAM_VALUE_SCHEMA_VERSION         -> "latest" // set to "latest" if you want the latest schema version to used

  )
  //subscribe to kafka
  // val messages = KafkaUtils.createDirectStream[String, Array[Byte], StringDecoder, DefaultDecoder]
  val df = spark.readStream.format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", topic)
    .option("startingOffsets", "latest")
    .load()
  //add confluent kafka avro deserializer, needed to read messages appropriately
  val deserializer = new KafkaAvroDeserializer(client).asInstanceOf[Deserializer[GenericRecord]]

  //needed to convert column select into Array[Bytes]
  import spark.implicits._
  df.printSchema()
  println("column key")
  df.select("key").printSchema()
  println("column value")
  df.select("value").printSchema()
  val schemaConf = "true"
  val results = df.select(from_avro(col("value"), schemaRegistryConf) as 'data).select("data.*")
  println(results)
  /*val results = df.select(col("value").as[Array[Byte]]).map{ rawBytes: Array[Byte] =>
    //read the raw bytes from spark and then use the confluent deserializer to get the record back
    val decoded = deserializer.deserialize(topic, rawBytes)
    print(decoded)
    // Row(decoded.get("src_bytes").toString, decoded.get("dst_bytes").toString)
    //val recordId = decoded.get("modeData").asInstanceOf[org.apache.avro.util.Utf8].toString
    val src_bytes = decoded.get("src_bytes").toString
    val dst_bytes = decoded.get("dst_bytes").toString
    val wrong_fragment = decoded.get("wrong_fragment").toString
    val num_compromised = decoded.get("num_compromised").toString
    // modeData
    val group = new ListBuffer[String]()
    group += src_bytes
    group += dst_bytes
    group += wrong_fragment
    group += num_compromised

    group
  }*/
/*
  }.foreachRDD(rdd =>
  if (rdd != null){
    val netDF = spark.createDataFrame(rdd,
      new StructType().add("src_bytes", StringType)
    .add("dst_bytes", StringType))
  }*/

  results.writeStream
    .outputMode("append")
    .format("console")
    .option("truncate", "false")
    .start()
    .awaitTermination()

}
