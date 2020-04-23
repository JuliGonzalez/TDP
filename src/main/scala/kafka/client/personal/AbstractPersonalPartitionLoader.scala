package kafka.client.personal


import java.sql.Connection
import java.util.function.Consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.google.common.base.Preconditions

import kafka.client.MetaSchema
import kafka.sink_connector.config.personal.PersonalConfig
import kafka.utils.WithCloseables
import kafka.utils.personal.PersonalJdbcTypeConverter

import org.apache.kafka.connect.data.{Field, Struct}
import org.apache.kafka.connect.sink.SinkRecord
import org.slf4j.{Logger, LoggerFactory}

trait AbstractPersonalPartitionLoader {

  private val log: Logger = LoggerFactory.getLogger(classOf[AbstractPersonalPartitionLoader])

  /**
   * Provides a [[PersonalJdbcClient]] implementation used for the partitions loading.
   *
   * @param hanaConfiguration The HANA connection configuration
   * @return a [[PersonalJdbcClient]] implementation
   */
  def getHANAJdbcClient(hanaConfiguration: PersonalConfig): PersonalJdbcClient

  /**
   * Loads a partition of a DataFrame to the HANA backend. This is done in
   * a single database transaction in order to avoid repeatedly inserting
   * data as much as possible.
   *
   * @param connection The HANA JDBC Client Connection
   * @param tableName The name of the table to load
   * @param iterator Iterator over the dataset to load
   * @param metaSchema The SinkRecord schema
   * @param batchSize The batch size
   */
  private[personal] def loadPartition(connection: Connection,
                                    tableName: String,
                                    iterator: Iterator[SinkRecord],
                                    metaSchema: MetaSchema,
                                    batchSize: Int): Unit = {
      WithCloseables(connection
        .prepareStatement(prepareInsertIntoStmt(tableName, metaSchema))) { stmt =>
        val fieldsValuesConverters = PersonalJdbcTypeConverter.getSinkRowDatatypesSetters(metaSchema.fields,
          stmt)
        for (batchRows <- iterator.grouped(batchSize)) {
          for (row <- batchRows) {
            var dataFromKey: Struct = null
            var dataFromValue: Struct = null

            if (row.key() != null && row.key().isInstanceOf[Struct]) {
              dataFromKey = row.key().asInstanceOf[Struct]
            }

            if (row.value() != null && row.value().isInstanceOf[Struct]) {
              dataFromValue = row.value().asInstanceOf[Struct]
            }

            metaSchema.fields.zipWithIndex.foreach{
              case (field, i) =>

                try {
                  Preconditions.checkArgument(dataFromKey != null && dataFromKey.get(field.name) != null)
                  fieldsValuesConverters(i)(dataFromKey.get(field.name))
                } catch {
                  case e: Exception =>
                    try {
                      Preconditions.checkArgument(dataFromValue != null && dataFromValue.get(field.name) != null)
                      fieldsValuesConverters(i)(dataFromValue.get(field.name))
                    } catch {
                      case e: Exception =>
                        fieldsValuesConverters(i)(null)
                    }
                }
               //Handle Null Values
              /*case (null, i) =>
                stmt.setNull(i + 1, JdbcTypeConverter
                  .convertToHANAType(metaSchema.fields(i).dataType))*/
            }
            stmt.addBatch()
          }
          stmt.executeBatch()
          stmt.clearParameters()
        }
      }

      // conn.commit()
      //committed = true
    }
    /*, doClose = { conn => {
      if (!committed) {
        // Partial commit could have happened....rollback
        conn.rollback()
        conn.close()
      } else {
        /**
         * The stage cannot fail now as failure will re-trigger the inserts.
         * Do not propagate exceptions further.
         */
        log.info("Record inserted")
        try {
          conn.close()
        } catch {
          case e: Exception => log.warn("Transaction succeeded, but closing failed", e)
        }
      }
    }
    })*/

  /**
    * Loads a partition of a DataFrame to the HANA backend. This is done in
    * a single database transaction in order to avoid repeatedly inserting
    * data as much as possible.
    *
    * @param connection The HANA JDBC Client Connection
    * @param collectionName The name of the table to load
    * @param iterator Iterator over the dataset to load
    * @param metaSchema The SinkRecord schema
    * @param batchSize The batch size
    */
  private[personal] def loadPartitionForJsonStore(connection: Connection,
                                              collectionName: String,
                                              iterator: Iterator[SinkRecord],
                                              metaSchema: MetaSchema,
                                              batchSize: Int): Unit = {
    for (batchRows <- iterator.grouped(batchSize)) {
      for (row <- batchRows) {
        WithCloseables(connection
          .prepareStatement(prepareJsonStatement(connection, collectionName, row, metaSchema, batchSize))) { stmt =>
          stmt.execute()
        }
      }
    }
  }

  private[personal] def prepareJsonStatement(connection: Connection,
                                          collectionName: String,
                                          row: SinkRecord,
                                          metaSchema: MetaSchema,
                                          batchSize: Int): String = {
    var dataFromKey: Struct = null
    var dataFromValue: Struct = null

    if (row.key() != null && row.key().isInstanceOf[Struct]) {
      dataFromKey = row.key().asInstanceOf[Struct]
    }

    if (row.value() != null && row.value().isInstanceOf[Struct]) {
      dataFromValue = row.value().asInstanceOf[Struct]
    }

    val sinkRecordMap = collection.mutable.Map[String, AnyRef]()
    try {
      Preconditions.checkArgument(dataFromKey != null)

      val fields = dataFromKey.schema().fields()
      fields.forEach(new Consumer[Field] {
        override def accept(field: Field): Unit = {
          sinkRecordMap.update(field.name(), dataFromKey.get(field.name()))
        }
      })
    } catch {
      case e: Exception =>
        try {
          Preconditions.checkArgument(dataFromValue != null)

          val fields = dataFromValue.schema().fields()
          fields.forEach(new Consumer[Field] {
            override def accept(field: Field): Unit = {
              sinkRecordMap.update(field.name(), dataFromValue.get(field.name()))
            }
          })
        } catch {
          case e: Exception =>
            //do nothing
        }
    }

/*    metaSchema.fields.zipWithIndex.foreach {
      case (field, i) =>
        try {
          Preconditions.checkArgument(dataFromKey != null && dataFromKey.get(field.name) != null)
          sinkRecordMap.update(field.name, dataFromKey.get(field.name))
        } catch {
          case e: Exception =>
            try {
              Preconditions.checkArgument(dataFromValue != null && dataFromValue.get(field.name) != null)
              sinkRecordMap.update(field.name, dataFromValue.get(field.name))
            } catch {
              case e: Exception =>
                //do nothing
            }
        }
    }*/
    prepareInsertIntoCollStmt(collectionName, sinkRecordMap.toMap[String, AnyRef])
  }


  /**
   * Prepares an INSERT INTO statement for the give parameters.
   *
   * @param fullTableName The fully-qualified name of the table
   * @param metaSchema The Metadata schema
   * @return The prepared INSERT INTO statement as a [[String]]
   */
  private[personal] def prepareInsertIntoStmt(fullTableName: String, metaSchema: MetaSchema): String = {
    val fields = metaSchema.fields
    val columnNames = fields.map(field => s""""${field.name}"""").mkString(", ")
    val placeHolders = fields.map(field => s"""?""").mkString(", ")
    s"""INSERT INTO $fullTableName ($columnNames) VALUES ($placeHolders)"""
  }

  /**
    * Prepares an INSERT INTO statement for the give parameters.
    *
    * @param collectionName The fully-qualified name of the collection
    * @param metaSchema The Metadata schema
    * @return The prepared INSERT INTO statement as a [[String]]
    */
  private[personal] def prepareInsertIntoCollStmt(collectionName: String, recordMap: Map[String, AnyRef]): String = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    log.info(s"""INSERT INTO $collectionName VALUES ('${mapper.writeValueAsString(recordMap)}')""")
    s"""INSERT INTO $collectionName VALUES ('${mapper.writeValueAsString(recordMap)}')"""
  }

}
