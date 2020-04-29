package kafka

case class Key(deviceId: Int)

case class DeviceMeasurements(deviceId: Int, temperature: Int, modeData: String, timestamp: Long)

