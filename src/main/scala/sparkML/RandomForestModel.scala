package sparkML

import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.evaluation.BinaryClassificationEvaluator
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.ml.tuning.{CrossValidator, CrossValidatorModel, ParamGridBuilder}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

object RandomForestModel extends App{

  val spark = SparkSession.builder
    .master("local[*]")
    .appName("RandomForest-Model")
    .getOrCreate()
  import spark.implicits._

  val path: String = "/home/juliangonzalez/IdeaProjects/TDP/input/KDDTrain_modified.csv"
  val path_test: String = "/home/juliangonzalez/IdeaProjects/TDP/input/KDDTrain_modified_test.csv"
  val path_label: String = "/home/juliangonzalez/IdeaProjects/TDP/input/KDDTrain_modified_label.csv"
  val schema = StructType(
    StructField("src_bytes", DoubleType, nullable=true) ::
      StructField("dst_bytes", DoubleType, nullable=true) ::
      StructField("wrong_fragment", DoubleType, nullable=true) ::
      StructField("num_compromised", DoubleType, nullable=true) ::
      StructField("same_srv_rate", DoubleType, nullable=true) ::
      StructField("diff_srv_rate", DoubleType, nullable=true) ::
      StructField("dst_host_count", DoubleType, nullable=true) ::
      StructField("dst_host_same_srv_rate", DoubleType, nullable=true) ::
      StructField("dst_host_serror_rate", DoubleType, nullable=true) ::
      StructField("dst_host_srv_serror_rate", DoubleType, nullable=true) ::
      StructField("service_ecr_i", DoubleType, nullable=true) ::
      StructField("flag_RSTR", DoubleType, nullable=true) ::
      StructField("flag_S0", DoubleType, nullable=true) ::
      StructField("label", DoubleType, nullable=true) ::
    Nil
  )

  val schema_label = StructType(
    StructField("label", DoubleType, nullable = false) ::
    Nil
  )

  val KDD_Df = spark.read.format("csv")
    .option("header", value=true)
    .option("delimiter", ",")
    .option("mode", "DROPMALFORMED")
    .schema(schema)
    .load(path)
    .cache()

  val KDD_Df_label = spark.read.format("csv")
      .option("header", value=true)
      .option("delimiter", ",")
      .option("mode", "DROPMALFORMED")
      .schema(schema_label)
      .load(path_label)
      .cache()

  val KDD_Df_test = spark.read.format("csv")
    .option("header", value=true)
    .option("delimiter", ",")
    .option("mode", "DROPMALFORMED")
    .schema(schema)
    .load(path_test)
    .cache()

  KDD_Df.printSchema()
  KDD_Df.show(10)
  KDD_Df.describe("src_bytes").show()

  val cols = Array("src_bytes", "dst_bytes", "wrong_fragment", "num_compromised", "same_srv_rate", "diff_srv_rate",
  "dst_host_count", "dst_host_same_srv_rate", "dst_host_serror_rate", "dst_host_srv_serror_rate",
  "service_ecr_i", "flag_RSTR", "flag_S0", "label")

  //Vector Assembler to add feature column
  // input columns - cols
  //feature columns - feature
  val assembler = new VectorAssembler()
    .setInputCols(cols)
    .setOutputCol("features")
  val trainingDf = assembler.transform(KDD_Df)
  trainingDf.printSchema()
  trainingDf.show(10)

  val testDf = assembler.transform(KDD_Df_test)
  testDf.printSchema()
  testDf.show(10)

  // random  seed number to allowing repeating results
  val seed  = 5043
  val randomForestClassifier = new RandomForestClassifier()
    .setImpurity("gini")
    .setMaxDepth(3)
    .setNumTrees(20)
    .setFeatureSubsetStrategy("auto")
    .setSeed(seed)

  val randomForestModel = randomForestClassifier.fit(trainingDf)
  println(randomForestModel.toDebugString)

  val predictionDf = randomForestModel.transform(testDf)
  predictionDf.show(10)
  // evaluate model with area under ROC
  val evaluator = new BinaryClassificationEvaluator()
    .setLabelCol("label")
    .setMetricName("areaUnderROC")

  // measure the accuracy
  val accuracy = evaluator.evaluate(predictionDf)
  println("Accuracy:", accuracy)

  // parameters that needs to tune, we tune
  //  1. max buns
  //  2. max depth
  //  3. impurity
  val paramGrid = new ParamGridBuilder()
    .addGrid(randomForestClassifier.maxBins, Array(25, 28, 31))
    .addGrid(randomForestClassifier.maxDepth, Array(4, 6, 8))
    .addGrid(randomForestClassifier.impurity, Array("entropy", "gini"))
    .build()

  // define cross validation stage to search through the parameters
  // K-Fold cross validation with BinaryClassificationEvaluator
  val stages = Array(assembler, indexer, randomForestClassifier)
  val pipeline = new Pipeline().setStages(stages)
  val cv = new CrossValidator()
    .setEstimator(pipeline)
    .setEvaluator(evaluator)
    .setEstimatorParamMaps(paramGrid)
    .setNumFolds(5)

}
