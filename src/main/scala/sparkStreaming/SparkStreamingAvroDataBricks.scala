package sparkStreaming

import org.apache.spark.sql.avro.from_avro
import org.apache.avro.SchemaBuilder
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}


object SparkStreamingAvroDataBricks extends App{
  val sc = new SparkConf().setMaster("local[*]").setAppName("kafka-spark-avro-network")
  val streamContext = new StreamingContext(sc, Seconds(1))
  val scl = streamContext.sparkContext
  scl.setLogLevel("ERROR")
  val spark = SparkSession.builder().appName("kafka-spark-avro-network").getOrCreate()

  val schemaRegistryAddr = "https://localhost:8081"

  val df = spark.readStream
    .format("kafka")
    .option("kafka.bootstrap.servers", "localhost:9092")
    .option("subscribe", "test-tomysql")
    .option("startingOffsets", "latest")
    .load()

  df.printSchema()

}
