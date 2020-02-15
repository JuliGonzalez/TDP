# script to create a kafka topic on standalone mode
# paths can change depending on installations

usr/local/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test

# to check the info of the current created topic
/usr/local/kafka/bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic test