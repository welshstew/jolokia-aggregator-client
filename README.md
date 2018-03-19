# Jolokia Aggregator Client

Simple project which will populate an ActiveMQ queue, and periodically check the
jolokia aggregator for the result of the QueueSize of said queue.

When the queue size reaches over 15 a consumer is started to deplete the queue.

When the queue size reaches 0, the consumer is stopped and the queue will start to fill again.

Sample log output as below:

```text
13:29:53.491 [Camel (MyCamel) thread #0 - timer://foo] INFO  queueSizeCheck - my.queue message count: 14
13:29:58.447 [Camel (MyCamel) thread #0 - timer://foo] INFO  queueSizeCheck - my.queue message count: 15
13:30:03.412 [Camel (MyCamel) thread #0 - timer://foo] INFO  queueSizeCheck - my.queue message count: 15
13:30:08.424 [Camel (MyCamel) thread #0 - timer://foo] INFO  queueSizeCheck - my.queue message count: 16
13:30:08.460 [Camel (MyCamel) thread #1 - ControlBus] INFO  o.a.camel.spring.SpringCamelContext - Route: queueConsumer started and consuming from: activemq://queue:my.queue
13:30:08.460 [Camel (MyCamel) thread #1 - ControlBus] INFO  o.a.c.c.c.ControlBusProducer - ControlBus task done [start route queueConsumer] with result -> void
13:30:08.910 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.911 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.911 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.912 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.912 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.913 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.915 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.915 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.916 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.916 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.917 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.918 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.918 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.919 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.919 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:08.920 [Camel (MyCamel) thread #2 - JmsConsumer[my.queue]] INFO  queueConsumer - Consumed hello from my.queue
13:30:13.447 [Camel (MyCamel) thread #0 - timer://foo] INFO  queueSizeCheck - my.queue message count: 0
13:30:13.449 [Camel (MyCamel) thread #3 - ControlBus] INFO  o.a.c.impl.DefaultShutdownStrategy - Starting to graceful shutdown 1 routes (timeout 300 seconds)
13:30:13.932 [Camel (MyCamel) thread #4 - ShutdownTask] INFO  o.a.c.impl.DefaultShutdownStrategy - Route: queueConsumer shutdown complete, was consuming from: activemq://queue:my.queue
13:30:13.933 [Camel (MyCamel) thread #3 - ControlBus] INFO  o.a.c.impl.DefaultShutdownStrategy - Graceful shutdown of 1 routes completed in 0 seconds
13:30:13.937 [Camel (MyCamel) thread #3 - ControlBus] INFO  o.a.camel.spring.SpringCamelContext - Route: queueConsumer is stopped, was consuming from: activemq://queue:my.queue
13:30:13.937 [Camel (MyCamel) thread #3 - ControlBus] INFO  o.a.c.c.c.ControlBusProducer - ControlBus task done [stop route queueConsumer] with result -> void
13:30:18.431 [Camel (MyCamel) thread #0 - timer://foo] INFO  queueSizeCheck - my.queue message count: 1

```

### Application Properties...

Change:

- broker username/password
- broker url (according to your openshift svc name)
- queue name
- rest api url according to your openshift route
- token

```text
#amq default properties - need to be changed to your broker username/password
spring.activemq.broker-url=tcp://broker-amq-tcp:61616
spring.activemq.user=usertfs
spring.activemq.password=XSuAUoBT


#stuff for aggregator and queue name
jolokia.aggregator.queue.name=my.queue
jolokia.aggregator.rest.api=http://jolokia-aggregator-myproject.192.168.42.48.nip.io
jolokia.aggregator.rest.token=2U3Y4FLJsXbJMsiacpTjcGcQjiYa2S6HbG3sGUrVIrE
```



### Building

The example can be built with

    mvn clean fabric8:deploy
