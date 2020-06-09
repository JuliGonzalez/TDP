
mysql_ip=$(docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' mysql_local)
echo $mysql_ip
connect_ip=$(docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' my-connect)
echo $connect_ip
schema_registry_ip=$(docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' my-schema-registry)
echo $schema_registry_ip

topic_kafka_1='network-traffic'
topic_kafka_2='network-traffic-analized'
echo "Topic used: $topic_kafka_1"
echo "Topic used: $topic_kafka_2"

 echo "Waiting for Kafka Connect to start listening on $connect_ip"
        # shellcheck disable=SC1083
        while [ $(curl -s -o /dev/null -w %{http_code} http://$connect_ip:8088/connectors) -ne 200 ] ; do
          echo -e $(date) " Kafka Connect listener HTTP state: " $(curl -s -o /dev/null -w %{http_code} http://$connect_ip:8088/connectors) " (waiting for 200)"
          sleep 5
        done
        nc -vz $connect_ip 8088
        echo "Waiting for Schema Registry to start listening on schema-registry:8081 ⏳"
        while [ $(curl -s -o /dev/null -w %{http_code} http://$schema_registry_ip:8081) -eq 000 ]; do
          echo -e $(date) " Schema Registry listener HTTP state: " $(curl -s -o /dev/null -w %{http_code} http://schema-registry:8081) " (waiting for != 000)"
          sleep 5
        done
        #
        # nc -vz kafka-connect 8083
        # echo -e "\n--\n+> Creating Kafka Connect Mysql sink"

curl -X POST http://$connect_ip:8088/connectors -H "Content-Type: application/json" -d '{
                  "name": "mysql_sink_connector",
                  "config": {
                    "topics": "'$topic_kafka_1'",
                    "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
                    "connection.url": "jdbc:mysql://'$mysql_ip':3306/test_db",
                    "connection.user": "root",
                    "connection.password": "test_pass",
                    "auto.create": "true"
                  }
                  }'

curl -X POST http://$connect_ip:8088/connectors -H "Content-Type: application/json" -d '{
                  "name": "mysql_sink_connector_network_analized",
                  "config": {
                    "topics": "'$topic_kafka_2'",
                    "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
                    "connection.url": "jdbc:mysql://'$mysql_ip':3306/test_db",
                    "connection.user": "root",
                    "connection.password": "test_pass",
                    "auto.create": "true"
                  }
                  }'
echo
