package sparkML

import org.apache.log4j.{Level, Logger}
import org.apache.spark.{SparkConf, SparkContext}

object checkMetrics extends App{
  Logger.getLogger("logging.checkMetrics").setLevel(Level.OFF)
 /* val session =  SparkSession
    .builder()
    .appName("Check_Metrics")
    .master("local[*]")
    .getOrCreate()
*/
  val input_file_path: String = "input/KDDTrain.csv"
  val input_file_path_txt: String = "input/KDDTrain+.txt"

  val conf = new SparkConf().setAppName("checkingMetrics").setMaster("local[*]")
  val sc = new SparkContext(conf)

  val text = sc.textFile(input_file_path)
  print(text.count())
  val data = text.map(s => s.split(","))
  print(data)
}
