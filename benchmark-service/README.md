# benchmark-service

Service for storing/showing benchmark results

## Prerequisites

* Java 8

* PostgreSQL:

For local development use docker:

```
$ docker run --name benchmark-postgres -e POSTGRES_PASSWORD=postgres -p 15432:5432 -d postgres
```

## Running integration-tests

```
$ mvn integration-test
```

## Running service

```
$ mvn clean package
$ java -jar target/benchmark-service-*-SNAPSHOT.jar

------------------------------------------------------------------

      Copyright 2013-2015, Teradata, Inc. All rights reserved.

               Benchmark-service  (v1.0.0-SNAPSHOT)

------------------------------------------------------------------

11:22:30.170 INFO  com.teradata.benchmark.service.App - Starting App v1.0.0-SNAPSHOT on latitude with PID 8659 (/home/sogorkis/repos/benchmark-service/target/service-1.0.0-SNAPSHOT.jar started by sogorkis in /home/sogorkis/repos/benchmark-service)
...
```

Go to: [http://localhost:8080/](http://localhost:8080/)

## Creating environment

```
$ curl -X POST -H 'Content-Type: application/json' -d '{
    "dashboardType": "grafana",
    "dashboardURL": "http://10.25.17.79:3000/dashboard/db/hdp-cluster",
    "prestoURL": "http://10.25.17.79:8090/"
}' http://localhost:8080/v1/environment/HDP-r3
```