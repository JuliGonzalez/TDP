package kafka

import scala.io.Source

object ReadNetworkFile extends App{

  val filename = "/home/juliangonzalez/IdeaProjects/TDP/input/KDDTrain_modified_test_kafka.csv"
  val bufferedSource = Source.fromFile(filename)
    for (line <- bufferedSource.getLines) {
    val cols = line.split(',').map(_.trim)
    println(cols(2))
  }
  bufferedSource.close


}
