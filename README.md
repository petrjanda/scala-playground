This application is proof of concept of Akka Streams powered data processing, which is decomposed into several independent pieces.
These are called `Processors`. In order to decouple the processes, back pressure is not working end-to-end but instead just within the
individual processor itself. Motivation for this is to let individual processors consume data at its own pace, compared with really long
and complex pipelines which might be negatively influenced by any single component anywhere across the stream.

To allow such a processing flexibility system needs to be able to buffer data between processors. In case of fast consumer, data
flows through the system, regulated by back pressure in individual processors. If the producer gets faster the data will be
buffered before it can find its way to the slower processor(s). For that I've chosen Apache Kafka as its extremely fast and uses
efficient file storage and convenient replication, persistency and consistency settings.

Individual storage mechanisms like ElasticSearch or Cassandra are system's storage for materialized views, which each offers different
capabilities to provide high performance query interfaces.

## Getting started

To avoid unnecessary pollution of your system with infrastructure components, I've prepared simple `docker-compose` powered
 setup which can get you up and running.

First you will need docker-machine instance running. For virtualbox powered one run the following command:

    docker-machine create --driver virtualbox dev --virtualbox-memory 4096
    eval "$(docker-machine env dev)"

This would create a new virtualbox instance (based on boot2docker) which will be used to run all the necessary components.

To get the infrastructure up, only think you need to do is:

    boot2docker up
    docker-compose up -d

Which might take a while first time (downloading all the necessary docker images). You can verify everything running using

    docker-compose ps

which should output something like this:

    ╰─$ docker-compose ps                                                                                                                                                                                127 ↵
            Name                      Command               State                                              Ports
    -----------------------------------------------------------------------------------------------------------------------------------------------------------
    test_cassandra_1       cassandra-singlenode             Up      22/tcp, 61621/tcp, 7000/tcp, 7001/tcp, 7199/tcp, 8012/tcp, 9042/tcp, 0.0.0.0:9160->9160/tcp
    test_elasticsearch_1   /docker-entrypoint.sh elas ...   Up      0.0.0.0:9200->9200/tcp, 0.0.0.0:9300->9300/tcp
    test_kafka_1           supervisord -n                   Up      0.0.0.0:2181->2181/tcp, 0.0.0.0:9092->9092/tcp

Once its up and running go ahead, start `sbt` console and run these two commands:

    run index gwiq
    run read gwiq test.csv