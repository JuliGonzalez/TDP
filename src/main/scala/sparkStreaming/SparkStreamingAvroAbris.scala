package sparkStreaming

import java.util.Properties

import org.apache.spark.SparkConf
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.col
// import za.co.absa.abris.examples.utils.ExamplesUtils._

object SparkStreamingAvroAbris extends App{
  val sc = new SparkConf().setMaster("local[*]").setAppName("kafka-spark-avro-network")
  val spark = SparkSession.builder().appName("kafka-spark-avro-network").getOrCreate()

}
