package kafka.utils.personal

import kafka.client.MetaAttr
import kafka.utils.GenericJdbcTypeConverter

import org.apache.kafka.connect.data.Schema.Type
import org.apache.kafka.connect.data._

object PersonalJdbcTypeConverter extends GenericJdbcTypeConverter {

  /**
   * Converts a Kafka SinkRow Schema  to the most compatible HANA SQL datatype.
   *
   * @param fieldSchema The Schema for field.
   * @return The converted HANA datatype as [[java.sql.Types]]
   */
  def convertToHANAType(fieldSchema: Schema): Int = super.convertToDBType(fieldSchema)

  /**
    * Convert HANA Table schema to Kafka Schema
    * @param tableName HANA table for which metadata is converted
    * @param datatypes sequence containing metadata for table
    * @return kafka schema
    */
  def convertHANAMetadataToSchema(tableName: String, datatypes: Seq[MetaAttr]): Schema =
    super.convertJdbcMetadataToSchema(tableName, datatypes)
}
