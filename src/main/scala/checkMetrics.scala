import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.sql.SparkSession
import org.apache.log4j.{Level, Logger}


object checkMetrics extends App{
  Logger.getLogger("logging.checkMetrics").setLevel(Level.ERROR)
  val session =  SparkSession
    .builder()
    .appName("Check_Metrics")
    .master("local[*]")
    .getOrCreate()

  val input_file_path: String = "input/KDDTrain.csv"

  val df = session.read.csv(input_file_path)
  print(df)
}
