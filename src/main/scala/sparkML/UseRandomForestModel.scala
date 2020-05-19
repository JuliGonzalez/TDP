package sparkML

import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.tuning.{CrossValidator, CrossValidatorModel}
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.types._



object UseRandomForestModel extends App{
  val spark = SparkSession.builder
    .master("local[*]")
    .appName("RandomForest-Model-use")
    .getOrCreate()
  spark.sparkContext.setLogLevel("WARN")
  import spark.implicits._

  //val rfModel = RandomForestClassificationModel.load("/home/juliangonzalez/IdeaProjects/TDP/models/randomForest")
  val cvModelLoaded = CrossValidatorModel
    //.load("models/randomForest")
    .load("/home/juliangonzalez/IdeaProjects/TDP/models/randomForest")

  val df1 = Seq(
    (12983.0,0.0,1.0,1.0,0.0,134.0,86.0,0.04,0.0,0.0,1.0,0.0,0.0),
    (267.0,14515.0,4.0,4.0,0.0,155.0,255.0,0.0,0.0,0.0,0.0,0.0,0.0),
    (327.0,467.0,33.0,47.0,0.04,151.0,255.0,0.0,0.0,0.0,0.0,0.0,0.0),
    (45.0,44.0,505.0,505.0,0.0,255.0,255.0,0.0,1.0,0.0,0.0,0.0,0.0),
    (196.0,1823.0,17.0,17.0,0.0,255.0,255.0,0.0,0.0,0.0,0.0,0.0,0.0),
    (300.0,440.0,7.0,7.0,0.0,255.0,255.0,0.0,0.0,0.0,0.0,0.0,0.0),
    (0.0,0.0,120.0,120.0,0.0,235.0,171.0,0.07,0.0,0.0,0.0,0.0,0.0), //possible attack
    (0.0,0.0,204.0,18.0,0.0,255.0,18.0,0.07,0.0,0.0,0.0,0.0,0.0) //possible attack
  ).toDF("src_bytes", "dst_bytes", "wrong_fragment", "num_compromised", "same_srv_rate", "diff_srv_rate",
    "dst_host_count", "dst_host_same_srv_rate", "dst_host_serror_rate", "dst_host_srv_serror_rate",
    "service_ecr_i", "flag_RSTR", "flag_S0")

  df1.show()

  val df2 = cvModelLoaded.transform(df1)
  df2.show()

  spark.close()

}
