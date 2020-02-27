import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkContext, SparkConf}


object trainModel_naiveBayes extends App{
  val conf = new SparkConf().setAppName("trainmodel_naiveBayes").setMaster("local[*]")
  val sc = new SparkContext(conf)

  val data_path: String = "input/sample_svm_data.txt"

  val data = MLUtils.loadLibSVMFile(sc, data_path)
  val Array(training, test) = data.randomSplit(Array(0.6, 0.4))

  val model = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")

  val predictionAndLabel = test.map(p => (model.predict(p.features), p.label))
  val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()

  model.save(sc, "models/NaiveBayes")

  val sameModel = NaiveBayesModel.load(sc, "models/NaiveBayes")

  sc.stop()

}
