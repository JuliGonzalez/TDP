package kafka.sink_connector

import akka.actor.Actor
import org.apache.kafka.connect.runtime.Connect
import org.apache.kafka.trogdor.rest.StopWorkerRequest


class WorkerManager(connect: Connect) extends Actor {

  import WorkerManager._

  override def receive: Receive = {
    case StartWorker => connect.start()
    case StopWorker => connect.stop()
  }
}

object WorkerManager {
  case object StartWorker
  case object StopWorker
}
