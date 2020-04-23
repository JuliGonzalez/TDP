package kafka.client.personal

package object personalClient {
  // Variables from the spark context have to be prefixed by
  protected[personalClient] val CONF_PREFIX = "spark.personal."
  protected[personalClient] val PARAMETER_PATH = "tablepath"
  protected[personalClient] val PARAMETER_DBSCHEMA = "dbschema"
  protected[personalClient] val PARAMETER_HOST = "host"
  protected[personalClient] val PARAMETER_INSTANCE_ID = "instance"
  protected[personalClient] val PARAMETER_USER = "user"
  protected[personalClient] val PARAMETER_PASSWORD = "passwd"
  protected[personalClient] val PARAMETER_PARTITION_COLUMN = "partitioningColumn"
  protected[personalClient] val PARAMETER_NUM_PARTITIONS = "numberOfPartitions"
  protected[personalClient] val PARAMETER_MAX_PARTITION_SIZE = "maximumPartitionSize"
  protected[personalClient] val PARAMETER_BATCHSIZE = "batchsize"
  protected[personalClient] val DEFAULT_BATCHSIZE = 1000
  protected[personalClient] val DEFAULT_NUM_PARTITIONS = 1
  protected[personalClient] val PARAMETER_SCHEMA = "schema"
  protected[personalClient] val PARAMETER_LOWER_BOUND = "lowerBound"
  protected[personalClient] val PARAMETER_UPPER_BOUND = "upperBound"
  protected[personalClient] val MAX_PARTITION_SIZE = 100000
  protected[personalClient] val PARAMETER_COLUMN_TABLE = "columnStore"
  protected[personalClient] val DEFAULT_COLUMN_TABLE = false
  protected[personalClient] val PARAMETER_TABLE_PATTERN = "tablePattern"
  protected[personalClient] val DEFAULT_TABLE_PATTERN = "%"
  protected[personalClient] val PARAMETER_VIRTUAL_TABLE = "virtual"
  protected[personalClient] val DEFAULT_VIRTUAL_TABLE = true
  protected[personalClient] val PARAMETER_TENANT_DATABASE = "tenantdatabase"
  protected[personalClient] val PARAMETER_PORT = "port"
  protected[personalClient] val DEFAULT_SINGLE_CONT_HANA_PORT = "15"
  protected[personalClient] val DEFAULT_MULTI_CONT_HANA_PORT = "13"

  protected[personalClient] val CONSTANT_STORAGE_TYPE  = "STORAGE_TYPE"
  protected[personalClient] val CONSTANT_COLUMN_STORE  = "COLUMN STORE"
  protected[personalClient] val CONSTANT_ROW_STORE  = "ROW STORE"
}
