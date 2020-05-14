package sparkML

import org.apache.spark.ml.classification.{RandomForestClassificationModel, RandomForestClassifier}
import org.apache.spark.ml.tuning.{CrossValidator, CrossValidatorModel}
import org.apache.spark.sql.SparkSession

object UseRandomForestModel extends App{
  val spark = SparkSession.builder
    .master("local[*]")
    .appName("RandomForest-Model")
    .getOrCreate()

  val rfModel = RandomForestClassificationModel.load("/home/juliangonzalez/IdeaProjects/TDP/models/randomForest")
  //val cvModelLoaded = CrossValidatorModel
    //.load("models/randomForest")
    //.load("/home/juliangonzalez/IdeaProjects/TDP/models/randomForest")

}
