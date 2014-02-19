(ns cayenne-data.lookup
  (:require [clojure.data.json :as json]
            [cayenne.ids.doi :as doi-id]
            [org.httpkit.client :as http]
            [clj-http.client :as hutil]
            [conf-er :refer [config configured?]]))

(defn make-doi-lookup-opts [doi format]
  {:query-params {:pid (config :service :query :id)
                  :doi (doi-id/extract-long-doi doi)
                  :format format}})

(defmacro when-success [bindings & body]
  (let [form (bindings 0) 
        tst (bindings 1)]
    `(let [temp# @~tst]
       (when (hutil/success? temp#)
         (let [~form (:body temp#)]
           ~@body)))))

(defn body-if-success [request]
  (let [response @request]
    (when (hutil/success? response)
      (:body response))))

(defn get-unixsd
  "Get UNIXSD XML from CrossRef's DOI system."
  [doi]
  (body-if-success
   @(http/get (str (config :service :query :url) "/"
                   (config :service :query :doi-path))
              (make-doi-lookup-opts doi "unixsd"))))

(defn get-unixref
  "Get UNIXSD XML from OpenURL."
  [doi]
  (body-if-success
   @(http/get (str (config :service :query :url) "/"
                   (config :service :query :doi-path))
              (make-doi-lookup-opts doi "unixref"))))

(defn get-metadata-from-api 
  "Get DOI metadata from the cayenne api."
  [doi]
  (body-if-success
   @(http/get (str (config :service :api :url) "/"
                   (config :service :api :works-path))
                   "/" 
                   (doi-id/extract-long-doi doi))))

(defn get-metadata-from-doi-system
  "Get DOI metadata from CrossRef's DOI system."
  [doi]
  (when-let [xml (get-unixsd (doi-id/extract-long-doi doi))]
    (when-success [metadata @(http/get (str (config :service :api :url) "/"
                                            (config :service :api :convert-path))
                                       {}
                                       xml)]
      (json/read-str metadata))))

(defn get-metadata [doi]
  (or
   (get-metadata-from-api doi)
   (get-metadata-from-doi-system doi)))
