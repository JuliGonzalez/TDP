
mysql_ip=$(docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' mysql_local)
echo $mysql_ip
connect_ip=$(docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' my-connect)
echo $connect_ip

curl -X POST http://$connect_ip:8088/connectors -H "Content-Type: application/json" -d '{
                  "name": "mysql_test_sink_connector_1",
                  "config": {
                    "topics": "test-tomysql",
                    "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
                    "connection.url": "jdbc:mysql://'$mysql_ip':3306/test_db",
                    "connection.user": "root",
                    "connection.password": "test_pass",
                    "auto.create": "true"
                  }
                  }'
echo
