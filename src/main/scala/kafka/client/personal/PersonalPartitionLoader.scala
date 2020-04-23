package kafka.client.personal

import kafka.sink_connector.config.personal.PersonalConfig

/**
 * The [[AbstractPersonalPartitionLoader]] which uses [[PersonalJdbcClient]].
 */
object PersonalPartitionLoader extends AbstractPersonalPartitionLoader {

  /** @inheritdoc */
  def getPersonalJdbcClient(personalConfiguration: PersonalConfig): PersonalJdbcClient =
    new PersonalJdbcClient(personalConfiguration)

}
