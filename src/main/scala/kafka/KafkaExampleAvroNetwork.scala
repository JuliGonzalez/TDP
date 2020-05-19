package kafka

import java.util.{TimerTask, Timer}

import com.github.nscala_time.time.Imports._

import scala.util.{Failure, Random, Success, Try}

import scala.io.Source

object KafkaExampleAvroNetwork extends App {

  val t = new Timer()
  t.schedule(new TimerTask() {
    @Override
    def run() {
      println("posting")
      val producer = new KProducer[Router, NetworkConnection]()
      produceMessages(Random.nextInt(100), producer)
    }
  }, 0, 10000)

  def produceMessages(numberOfMessages: Int, producer: KProducer[Router, NetworkConnection]): Unit = {
    val filename = "/home/juliangonzalez/IdeaProjects/TDP/input/KDDTrain_modified_test_kafka.csv"
    val bufferedSource = Source.fromFile(filename)
    println("opening file: ")
    for (line <- bufferedSource.getLines) {
      val cols = line.split(',').map(_.trim)
      Try(producer.produce("test-tomysql", Router(3), NetworkConnection(cols(0).toDouble, cols(1).toDouble,
        cols(2).toDouble, cols(3).toDouble, cols(4).toDouble, cols(5).toDouble, cols(6).toDouble, cols(7).toDouble,
        cols(8).toDouble, cols(9).toDouble, cols(10).toDouble, cols(11).toDouble, cols(12).toDouble)))
      match {
        case Success(m) =>
          val metadata = m.get()
          println("Success writing to Kafka topic:" + metadata.topic(),
            metadata.offset(),
            metadata.partition(),
            new DateTime(metadata.timestamp()))

        case Failure(f) => println("Failed writing to Kafka", f)
      }
    }
    bufferedSource.close
  }
}