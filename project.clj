(defproject crossref/cayenne-data "0.1.0"
  :description "DOI data proxy"
  :url "http://github.com/CrossRef/cayenne-data"
  :main cayenne-data.core
  :jvm-opts ["-Dconfig=config.edn"]
  :daemon {:cayenne-data {:ns cayenne-data.core
                          :pidfile "cayenne-data.pid"}}
  :profiles {:uberjar {:aot :all}}
  :plugins [[codox "0.6.4"]
            [lein-daemon "0.5.4"]
            [lein-environ "1.0.0"]]
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [environ "1.0.0"]
                 [kjw/ring-logstash "0.1.3"]
                 [compojure "1.1.6"]
                 [crossref/heartbeat "0.1.1"]
                 [http-kit "2.1.16"]]
  :env {:server-port 3000
        :logstash-host "127.0.0.1"
        :logstash-port 4444
        :logstash-name "cayenne-data"
        :api-url "http://api.crossref.org"
        :api-internal-url "http://localhost:3000"
        :data-url "http://data.crossref.org"
        :id-url "http://id.crossref.org"
        :test-doi "10.5555/12345678"})
             
