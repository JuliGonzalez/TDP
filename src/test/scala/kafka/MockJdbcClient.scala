package kafka

import kafka.client.personal.PersonalJdbcClient
import kafka.sink_connector.config.BaseConfig
import kafka.sink_connector.config.personal.PersonalConfig

class MockJdbcClient(configuration: BaseConfig)
  extends PersonalJdbcClient(configuration.asInstanceOf[PersonalConfig]) {
  override val driver: String = "org.h2.Driver" //TODO: override this driver string
}