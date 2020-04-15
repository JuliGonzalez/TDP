package kafka.client.personal

import kafka.utils.ConnectorException

class PersonalConnectorException(msg: String) extends ConnectorException(msg)

class PersonalConfigInvalidInputException(msg: String) extends PersonalConnectorException(msg)

class PersonalConfigMissingException(msg: String) extends PersonalConnectorException(msg)

class PersonalJdbcException(msg: String) extends PersonalConnectorException(msg)

class PersonalJdbcConnectionException(msg: String) extends PersonalConnectorException(msg)

class PersonalJdbcBadStateException(msg: String) extends PersonalJdbcException(msg)
