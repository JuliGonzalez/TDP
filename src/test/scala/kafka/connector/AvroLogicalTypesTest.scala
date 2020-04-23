package kafka.connector

import java.util
import java.math.BigDecimal
import java.text.SimpleDateFormat

import kafka.config.personal.PersonalParameters
import kafka.sink_connector.personal.PersonalSinkTask
import org.apache.kafka.connect.data._
import org.apache.kafka.connect.sink.{SinkRecord, SinkTaskContext}
import org.scalatest.FunSuite
import org.mockito.Mockito.mock

class AvroLogicalTypesTest extends FunSuite{
  test("put propagates to DB with schema containing data fields") {
    val schema = SchemaBuilder.struct()
      .field("date_field", Date.builder().build())

    val props = new util.HashMap[String, String]()
    props.put("connection.url", "jdbc:mysql://localhost:3306/test_db;" +
      "INIT=CREATE SCHEMA IF NOT EXISTS TEST")
    props.put("connection.user", "root")
    props.put("connection.password", "pass_test")
    props.put("topics", "test")
    props.put("testTopic.table.name", "\"TEST\".\"DATE_TABLE\"")
    props.put("testTopic.table.type", "row")
    props.put("auto.create", "true")
    props.put("testTopic.pk.mode", "record_value")
    props.put("testTopic.pk.fields", "date_field")

    val task = new PersonalSinkTask()

    task.initialize(mock(classOf[SinkTaskContext]))
    task.start(props)

    val config = PersonalParameters.getConfig(props)
    task.hanaClient = new MockJdbcClient(config)
    task.initWriter(config)
    // when using an Avro Schema. use custom parameters for kafka connect like this.
    // {
    //  "name": "date",
    //  "type": {
    //    "type": "int",
    //    "connect.version": 1,
    //    "connect.name": "org.apache.kafka.connect.data.Date"
    //  }
    // }
    val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val expectedDateField = "2013-10-16"
    val date = simpleDateFormat.parse(expectedDateField)

    val struct = new Struct(schema).put("time_field", date)

    task.put(util.Collections.singleton(
      new SinkRecord("testTopic", 1, null, null, schema, struct, 42)
    ))

    val rs = task.hanaClient.executeQuery(schema, "select * from \"TEST\".\"TIME_TABLE\"",
      -1, -1)
    val structs = rs.get
    assert(structs.size === 1)
    val head = structs.head

    val actualDateField = head.get("date_field").toString
    assert(expectedDateField === actualDateField)
  }
}
