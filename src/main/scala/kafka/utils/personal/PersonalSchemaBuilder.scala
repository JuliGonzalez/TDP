package kafka.utils.personal

import kafka.client.MetaSchema
import kafka.utils.GenericSchemaBuilder


object PersonalSchemaBuilder extends GenericSchemaBuilder {
  /**
   * Converts a AVRO schema to the HANA Schema.
   *
   * @param schema The schema to convert
   * @return The HANA schema as [[String]]
   */
  def avroToHANASchema(schema: MetaSchema): String =
    super.avroToJdbcSchema(schema)
}
