# HugSQL Adapter ClickHouse Native JDBC

[![Clojars Project](http://clojars.org/us.2da/hugsql-adapter-clickhouse-native-jdbc/latest-version.svg)](http://clojars.org/skinney/hugsql-adapter-postgres-async)

An adapter between [HugSQL](https://www.hugsql.org/) and the [ClickHouse Native JDBC](https://github.com/housepower/ClickHouse-Native-JDBC) driver.

## Usage

Follow the [HugSQL documentation](https://www.hugsql.org/#adapter-other) for using third-party adapters.

Add the following to your `project.clj`:

Then you have to install the adapter:

```clojure
(ns core
  (:require [hugsql.core :as hugsql]
            [hugsql.adapter.clickhouse-native-jdbc :as clickhouse]))

(hugsql/def-db-fns "fns.sql")
(hugsql/set-adapter! (clickhouse/hugsql-adapter-))
```
## Tests

The tests assume you have a local instance of ClickHouse running on port 9000.

```
%> lein test
```

## License

Copyright © 2019 2DA Analytics