import org.apache.spark.SparkConf
import org.apache.spark.ml.classification.NaiveBayes
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

import org.apache.spark.sql.SparkSession

object NaiveBayesTrainModel {
  def main(args: Array[String]): Unit = {
    val spConfig = (new SparkConf).setMaster("local").setAppName("SparkApp")
    val spark = SparkSession.
      .builder()
      .appName("SparkNaiveBayes")
      .config(spConfig)
      .getOrCreate()


  spark.stop()
  }


}
