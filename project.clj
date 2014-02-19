(defproject crossref/cayenne-data "0.1.0"
  :description "FIXME: write description"
  :url "http://github.com/CrossRef/cayenne-data"
  :main cayenne-data.core
  :plugins [[codox "0.6.4"]
            [lein-daemon "0.5.4"]]
  :jvm-opts ["-Dconfig=config.edn"]
  :daemon {:caeynne-data {:ns cayenne-data.core
                          :pidfile "cayenne-data.pid"}}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [compojure "1.1.6"]
                 [crossref/heartbeat "0.1.1"]
                 [http-kit "2.1.10"]
                 [conf-er "1.0.1"]])
