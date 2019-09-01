# JanusGraph TinkerPop Football Java Example

Simple Java Football example using JanusGraph 0.4.0 and Apache TinkerPop 3.4.1.

## Build Environment

* Java 8.0 Update 221
* Apache Maven 3.6.1

## Building and Running

* Datastax Distribution of Apache Cassandra 5.1.16
* Elastic Search 6.6.0
* Apache Tinkerpop 3.4.1

```
mvn clean package
mvn exec:java -Dexec.mainClass="football.janusgraph.example.FootballExample"
```

