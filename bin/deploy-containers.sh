pwd
cd /home/juliangonzalez/IdeaProjects/TDP
docker-compose up -d
sleep 1
cd /home/juliangonzalez/IdeaProjects/TDP/bin/
/bin/bash kafka/create-connector.sh