"hello"
3+4
val test:String = "How is everyone doing tonight"

import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint


val conf = new SparkConf().setAppName("testingNaives").setMaster("local[*]")
val sc = new SparkContext(conf)

val data_path: String = "/home/juliangonzalezdosreis/ScalaProjects/TDP/input/sample_svm_data.txt"
val data = sc.textFile(data_path)

//val data = MLUtils.loadLibSVMFile(sc,data_path)
val parsedData = data.map { line =>
  val parts = line.split(',')
  LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
}

print(data)
print(parsedData)

