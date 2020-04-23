package kafka.sink_connector.personal
import java.sql.Connection

import kafka.client.personal.{PersonalConfigInvalidInputException, PersonalConfigMissingException, PersonalJdbcClient}
import kafka.client.{MetaSchema, MetaAttr}
import kafka.sink_connector.config.BaseConfigConstants
import kafka.sink_connector.config.personal.PersonalConfig
import kafka.schema.KeyValueSchema
import kafka.utils.personal.PersonalJdbcTypeConverter
import kafka.utils.SchemaNotMatchedException
import org.apache.kafka.connect.data.{Field, Schema}
import org.apache.kafka.connect.sink.SinkRecord
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.JavaConversions._


class PersonalSinkRecordsCollector(var tableName: String, client: PersonalJdbcClient,
                                   connection: Connection, config: PersonalConfig) {
  private val log: Logger = LoggerFactory.getLogger(classOf[PersonalSinkTask])
  private var records: Seq[SinkRecord] = Seq[SinkRecord]()
  private var tableMetaData:Seq[MetaAttr] = Seq[MetaAttr]()
  private var metaSchema: MetaSchema = null
  var tableConfigInitialized = false

  private def initTableConfig(nameSpace: Option[String], tableName: String, topic: String) : Boolean = {

    tableConfigInitialized match {
      case false =>
        if (config.topicProperties(topic)("table.type") == BaseConfigConstants.COLLECTION_TABLE_TYPE) {
          if (client.collectionExists(tableName)) {
            tableMetaData = client.getMetaData(tableName, nameSpace)
            metaSchema = new MetaSchema(tableMetaData, null)
            tableConfigInitialized = true
          }
        } else if(client.tableExists(nameSpace, tableName)){
          tableMetaData = client.getMetaData(tableName, nameSpace)
          metaSchema = new MetaSchema(tableMetaData, null)
          tableConfigInitialized = true
        }
      case true =>
    }
    tableConfigInitialized
  }

  private[sink_connector] def add(records: Seq[SinkRecord]): Unit = {
    val recordHead = records.head
    val recordSchema = KeyValueSchema(recordHead.keySchema(), recordHead.valueSchema())

    initTableConfig(getTableName._1,getTableName._2, recordHead.topic()) match
    {
      case true =>
        log.info(s"""Table $tableName exists.Validate the schema and check if schema needs to evolve""")
        var recordFields = Seq[MetaAttr]()

        if (recordSchema.keySchema != null) {
          for (field <- recordSchema.keySchema.fields) {
            val fieldSchema: Schema = field.schema()
            val fieldAttr = MetaAttr(field.name(),
              PersonalJdbcTypeConverter.convertToHANAType(fieldSchema), 1, 0, 0, isSigned = false)
            recordFields = recordFields :+ fieldAttr
          }
        }

        if (recordSchema.valueSchema != null) {
          for (field <- recordSchema.valueSchema.fields) {
            val fieldSchema: Schema = field.schema
            val fieldAttr = MetaAttr(field.name(),
              PersonalJdbcTypeConverter.convertToHANAType(fieldSchema), 1, 0, 0, isSigned = false)
            recordFields = recordFields :+ fieldAttr
          }
        }
        if(config.topicProperties(recordHead.topic())("table.type") != BaseConfigConstants.COLLECTION_TABLE_TYPE
            && !compareSchema(recordFields))
          {
            log.error(
              s"""Table $tableName has a different schema from the record Schema.
                 |Auto Evolution of schema is not supported""".stripMargin)
            throw new SchemaNotMatchedException(
              s"""Table $tableName has a different schema from the Record Schema.
                 |Auto Evolution of schema is not supported
               """.stripMargin)
          }
      case false =>
        if (config.autoCreate) {
          // find table type
          val tableType = if (config.topicProperties(recordHead.topic())
            .get("table.type").get == "column")
            true
          else false

          // find partition type
          val partitionType = config.topicProperties(recordHead.topic())
            .get("table.partition.mode").get

          //find no. of partitions
          val partitionCount = config.topicProperties(recordHead.topic())
            .get("table.partition.count").get

          metaSchema = new MetaSchema(Seq[MetaAttr](), Seq[Field]())

          if (recordSchema.keySchema != null) {
            for (field <- recordSchema.keySchema.fields) {
              val fieldSchema: Schema = field.schema
              val fieldAttr = MetaAttr(field.name(),
                PersonalJdbcTypeConverter.convertToHANAType(fieldSchema), 1, 0, 0, isSigned = false)
              metaSchema.fields = metaSchema.fields :+ fieldAttr
              metaSchema.avroFields = metaSchema.avroFields :+ field
            }
          }

          if (recordSchema.valueSchema != null) {
            for (field <- recordSchema.valueSchema.fields) {
              val fieldSchema: Schema = field.schema
              val fieldAttr = MetaAttr(field.name(),
                PersonalJdbcTypeConverter.convertToHANAType(fieldSchema), 1, 0, 0, isSigned = false)
              metaSchema.fields = metaSchema.fields :+ fieldAttr
              metaSchema.avroFields = metaSchema.avroFields :+ field
            }
          }

          if (config.topicProperties(recordHead.topic()).get("pk.mode").get
            == BaseConfigConstants.RECORD_KEY) {
            val keys = getValidKeys(config.topicProperties(recordHead.topic())
              .get("pk.fields").get.split(",").toList, metaSchema.avroFields)

            client.createTable(getTableName._1, getTableName._2, metaSchema,
              config.batchSize, tableType, keys, partitionType, partitionCount.toInt)
          }
          else if (config.topicProperties(recordHead.topic()).get("pk.mode").get
            == BaseConfigConstants.RECORD_VALUE) {
            val keys = getValidKeys(config.topicProperties(recordHead.topic())
              .get("pk.fields").get.split(",").toList, metaSchema.avroFields)

            client.createTable(getTableName._1, getTableName._2, metaSchema,
              config.batchSize, tableType, keys, partitionType, partitionCount.toInt)
          } else {
            if (config.topicProperties(recordHead.topic())("table.type") == BaseConfigConstants.COLLECTION_TABLE_TYPE) {
              client.createCollection(getTableName._2)
            } else {
              client.createTable(getTableName._1, getTableName._2,
                metaSchema, config.batchSize, tableType, partitionType = partitionType,
                partitionCount = partitionCount.toInt)
            }
          }
          client.getMetaData(getTableName._2,getTableName._1)
        } else {
          throw new PersonalConfigMissingException(s"Table does not exist. Set 'auto.create' parameter to true")
        }
    }
    this.records = records
  }

  private[sink_connector] def flush(): Seq[SinkRecord] = {
    if (config.topicProperties(records.head.topic())("table.type") == BaseConfigConstants.COLLECTION_TABLE_TYPE) {
      client.loadData(getTableName._2, connection, metaSchema, records, config.batchSize)
    } else {
      client.loadData(getTableName._1, getTableName._2, connection, metaSchema, records, config.batchSize)
    }
    val flushedRecords = records
    records = Seq.empty[SinkRecord]
    flushedRecords
  }

  private[sink_connector] def size(): Int = {
    records.size
  }

  private def getTableName: (Option[String], String) = {
    tableName match {
      case BaseConfigConstants.TABLE_NAME_FORMAT(schema, table) =>
        (Some(schema), table)
      case BaseConfigConstants.COLLECTION_NAME_FORMAT(table) =>
        (None, table)
      case _ =>
        throw new PersonalConfigInvalidInputException(s"The table name mentioned in `{topic}.table.name` is invalid." +
          s" Does not follow naming conventions")
    }
  }

  private def getValidKeys(keys: List[String], allFields: Seq[Field]): List[String] = {
    val fields = allFields.map(metaAttr => metaAttr.name())
    keys.filter(key => fields.contains(key))
  }

  private def compareSchema(dbSchema : Seq[MetaAttr]): Boolean = {
    val fieldNames = metaSchema.fields.map(_.name)
    if(metaSchema.fields.size != dbSchema.size)
      false
    else
      {
        for (field <- dbSchema) {
          if (!fieldNames.contains(field.name)){
           return false
          }
        }
      true
      }
  }

}
