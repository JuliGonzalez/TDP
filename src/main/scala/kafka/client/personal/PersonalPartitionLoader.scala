package kafka.client.personal

import com.sap.kafka.connect.config.hana.HANAConfig

/**
 * The [[AbstractPersonalPartitionLoader]] which uses [[PersonalJdbcClient]].
 */
object PersonalPartitionLoader extends AbstractPersonalPartitionLoader {

  /** @inheritdoc */
  def getHANAJdbcClient(hanaConfiguration: HANAConfig): PersonalJdbcClient =
    new PersonalJdbcClient(hanaConfiguration)

}
