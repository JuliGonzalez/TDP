package kafka.sink_connector

import kafka.sink_connector.config.BaseConfig

trait SinkWriter {
  def initWriter(config: BaseConfig): BaseWriter
}
