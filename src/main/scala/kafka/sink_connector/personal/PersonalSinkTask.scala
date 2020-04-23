package kafka.sink_connector.personal

import java.util
import kafka.client.personal.PersonalJdbcClient
import kafka.sink_connector.config.personal.{PersonalParameters, PersonalConfig}
import kafka.sink_connector.config.BaseConfig
import kafka.sink_connector.{BaseWriter, GenericSinkTask}
import org.slf4j.LoggerFactory


class PersonalSinkTask extends GenericSinkTask {
  log = LoggerFactory.getLogger(classOf[PersonalSinkTask])
  private val tableCache = scala.collection.mutable.Map[String, PersonalSinkRecordsCollector]()
  var hanaClient: PersonalJdbcClient = _

  override def start(props: util.Map[String, String]): Unit = {
    log.info("Starting Kafka-Connect task")
    config = PersonalParameters.getConfig(props)
    hanaClient = new PersonalJdbcClient(config.asInstanceOf[PersonalConfig])
    initWriter(config.asInstanceOf[PersonalConfig])
  }

  override def initWriter(config: BaseConfig): BaseWriter = {
    log.info("init HANA Writer for writing the records")
    writer = new PersonalWriter(config.asInstanceOf[PersonalConfig], hanaClient, tableCache)
    writer
  }


}