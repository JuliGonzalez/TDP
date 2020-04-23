package kafka.sink_connector.config.personal

import kafka.client.personal.PersonalConfigMissingException
import kafka.sink_connector.config.{BaseConfig, BaseConfigConstants}

case class PersonalConfig(props: Map[String, String]) extends BaseConfig(props: Map[String, String]){

  /**
   * DB Jdbc url for source & sink
   */
  def connectionUrl = props("connection.url")


  override def topicProperties(topic: String) = {
    val topicPropMap =
      scala.collection.mutable.Map[String, String]() ++ super.topicProperties(topic)

    for ((key, value) <- props) {
      /**
       * table type to be used by sink
       * Default value is COLUMN_TABLE_TYPE.
       */
      if (key == s"$topic.table.type") {
        if (value == BaseConfigConstants.COLUMN_TABLE_TYPE)
          topicPropMap.put("table.type", value)
        else if (value == BaseConfigConstants.ROW_TABLE_TYPE)
          topicPropMap.put("table.type", value)
        else if (value == BaseConfigConstants.COLLECTION_TABLE_TYPE)
          topicPropMap.put("table.type", value)
        else
          throw new PersonalConfigMissingException(
            "Value specified is incorrect for 'table.type' parameter")
      }
    }

    if (topicPropMap.get("table.name").isEmpty && topicPropMap.get("query").isEmpty) {
      throw new PersonalConfigMissingException("A table name must be specified for HANA-Kafka " +
        "connectors to work")
    }

    if (topicPropMap.get("query").isEmpty) {
      topicPropMap.put("query", "")
    }

    if (topicPropMap.get("table.type").isEmpty) {
      topicPropMap.put("table.type", BaseConfigConstants.COLUMN_TABLE_TYPE)
    }

    if (topicPropMap.get("incrementing.column.name").isEmpty &&
      (mode == BaseConfigConstants.MODE_INCREMENTING)) {
      throw new PersonalConfigMissingException(s"With mode as ${BaseConfigConstants.MODE_INCREMENTING}" +
        s" an incrementing column must be specified")
    }

    topicPropMap.toMap
  }
}