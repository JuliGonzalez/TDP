package kafka.sink_connector
import java.util

import kafka.config.personal.PersonalConfig
import kafka.sink_connector.{BaseWriter, GenericSinkConnector}
import org.slf4j.LoggerFactory


class PersonalSinkTask extends GenerickSinkTask{
  log = LoggerFactory.getLogger(classOf[HANASinkTask])
  private val tableCache = scala.collection.mutable.Map[String, PersonalSinkCollectorsRecord]()
  var hanaClient: HANAJdbcClient = _

  override def start(props: util.Map[String, String]): Unit = {
    log.info("Starting Kafka-Connect task")
    config = HANAParameters.getConfig(props)
    hanaClient = new HANAJdbcClient(config.asInstanceOf[PersonalConfig])
    initWriter(config.asInstanceOf[PersonalConfig])
  }

  override def initWriter(config: BaseConfig): BaseWriter = {
    log.info("init HANA Writer for writing the records")
    writer = new HANAWriter(config.asInstanceOf[HANAConfig], hanaClient, tableCache)
    writer
  }


}