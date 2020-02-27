import org.apache.spark.mllib.clustering.{KMeans, KMeansModel}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.{SparkContext, SparkConf}


object trainModel_kmeans extends App {
  val conf = new SparkConf().setAppName("trainingKmeans").setMaster("local[*]")
  val sc = new SparkContext(conf)

  // val data_path: String = "input/KDDTrain+.txt"
  val data_path: String = "input/kmeans_data.txt"


  val data = sc.textFile(data_path)
  val parsedData = data.map(s => Vectors.dense(s.split(' ').map(_.toDouble))).cache()
  //val parsedData = data.map(s => s.split(',')).cache()
  print(parsedData)

  // set the number of cluster for the K-means
  // val numClusters = 5
  val numClusters = 2
  val numIterations = 20
  val clusters = KMeans.train(parsedData, numClusters, numIterations)

  // evaluate clustering by computing withing set sum of squared errores
  val WSSSE = clusters.computeCost(parsedData)
  println(s"Within Set Sum of Squared Errors = $WSSSE")

  // save and load model
  clusters.save(sc, "models/KMeansModel")
  val sameModel = KMeansModel.load(sc, "models/KMeansModel")

  sc.stop()
}
