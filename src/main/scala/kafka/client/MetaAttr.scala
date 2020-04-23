package kafka.client

import org.apache.kafka.connect.data.Field

// It overrides a JDBC class - keep it starting with lowercase
case class MetaAttr(
    name: String,
    dataType: Int,
    isNullable: Int,
    precision: Int,
    scale: Int,
    isSigned: Boolean)

case class MetaSchema(var fields: Seq[MetaAttr], var avroFields: Seq[Field])
