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
    Nil
  )

  val KDD_Df = spark.read.format("csv")
    .option("header", value=true)
    .option("delimiter", ",")
    .option("mode", "DROPMALFORMED")
    .schema(schema)
    .load(path)
    .cache()
  KDD_Df.printSchema()
  KDD_Df.show(10)
  KDD_Df.describe("src_bytes").show()
}
