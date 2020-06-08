pwd
cd /home/juliangonzalez/Projects/connect-kafka-connector/
docker-compose up -d
sleep 30
cd /home/juliangonzalez/IdeaProjects/TDP/bin/
/bin/bash kafka/create-connector.sh