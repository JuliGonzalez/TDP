from kafka import KafkaConsumer
from json import loads

consumer = KafkaConsumer(
            'test',
            auto_offset_reset='latest',
            enable_auto_commit=True,
            group_id='my-group-1',
            value_deserializer=lambda m: loads(m.decode('utf-8')),
            bootstrap_servers=['localhost:9092']
            # bootstrap_servers=['172.17.0.1:32783','172.17.0.1:32782','172.17.0.1:32781']
         )

print('entering here')
print(consumer, type(consumer))
for m in consumer:
    print(type(m))
    print(m.value)
