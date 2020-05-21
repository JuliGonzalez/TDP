package kafka

case class Key(deviceId: Int)

case class Router(routerId: Int)

case class DeviceMeasurements(deviceId: Int, temperature: Int, modeData: String, timestamp: Long)

case class NetworkConnection(src_bytes: Double, dst_bytes: Double, wrong_fragment: Double, num_compromised: Double,
                             same_srv_rate: Double, diff_srv_rate: Double, dst_host_count: Double,
                             dst_host_same_srv_rate: Double, dst_host_serror_rate: Double, dst_host_srv_serror_rate: Double,
                             service_ecr_i: Double, flag_RSTR: Double, flag_S0: Double, label: Double)