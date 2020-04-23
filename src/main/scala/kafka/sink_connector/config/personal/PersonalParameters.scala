package kafka.sink_connector.config.personal

import java.util

import kafka.client.personal.PersonalConfigMissingException
import kafka.sink_connector.config.BaseParameters

import scala.collection.JavaConversions._

object PersonalParameters extends BaseParameters {

  override def getConfig(props: util.Map[String, String]): PersonalConfig = {
    super.getConfig(props)

    if (props.get("connection.url") == null) {
      throw new PersonalConfigMissingException("Mandatory parameter missing: " +
        " Personal DB Jdbc url must be specified in 'connection.url' parameter")
    }

    if (props.get("connection.user") == null) {
      throw new PersonalConfigMissingException("Mandatory parameter missing: " +
        " Personal DB user must be specified in 'connection.user' parameter")
    }

    if (props.get("connection.password") == null) {
      throw new PersonalConfigMissingException("Mandatory parameter missing: " +
        " Personal DB password must be specified in 'connection.password' parameter")
    }

    PersonalConfig(props.toMap)
  }
}