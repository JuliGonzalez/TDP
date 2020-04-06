package kafka.sink_connector

import akka.actor.ActorSystem
import kafka.sink_connector.WorkerManager.StartWorker

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.language.postfixOps

object Boot extends App{
  def run()(implicit system: ActorSystem, ec: ExecutionContext): Unit = {
    val assembly = new Assembly
    import assembly._

    workerManager ! StartWorker
  }

  override def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem("kafka-connect")
    implicit val executor: ExecutionContextExecutor = actorSystem.dispatcher
    run()
  }
}
