(ns cayenne-data.core
  (:gen-class)
  (:import [java.net URLEncoder])
  (:require [clojure.string :as string]
            [ring.middleware.logstash :as logstash]
            [compojure.core :refer [defroutes routes context GET HEAD]]
            [compojure.handler :as handler]
            [ring.util.response :as response :refer [redirect]]
            [heartbeat.ring :refer [wrap-heartbeat]]
            [heartbeat.core :refer [def-web-check def-version]]
            [org.httpkit.server :as hs]
            [org.httpkit.client :as hc]
            [environ.core :refer [env]]))

(def data-host "data.crossref.org")

(def id-host "id.crossref.org")

(def redirect-types ["text/html"])

(def data-types 
  ["application/rdf+xml"
   "text/turtle"
   "text/n-triples"
   "text/n3"
   "application/vnd.citationstyles.csl+json"
   "text/x-bibliography"
   "application/x-research-info-systems"
   "application/x-bibtex"
   "application/vnd.crossref.unixref+xml"
   "application/vnd.crossref.unixsd+xml"])

(def-web-check :crossref-api
  (str
   (env :api-internal-url) "/works/" (env :test-doi)))

(def-version (env :version))

(defn make-data-redirect [doi]
  (redirect (str (env :data-url) "/" doi)))

(defn make-api-redirect [path]
  (redirect (str (env :api-url) "/" path)))

(defn apply-links [resp headers]
  (if (:link headers)
    (response/header resp "link" (:link headers))
    resp))

(defn get-doi [accept doi]
  @(hc/get (str (env :api-internal-url)
                "/v1/works/"
                (URLEncoder/encode doi)
                "/transform")
           {:keepalive 30000
            :timeout 10000
            :headers {"Accept" accept}}))

(defn proxy-doi [accept doi]
  (let [{:keys [status error body headers]} (get-doi accept doi)]
    (if error
      (-> (response/response "")
          (response/status 504))
      (-> (response/response body)
          (response/status status)
          (response/content-type (get headers :content-type))
          (apply-links headers)))))

(defn proxy-doi-headers [accept doi]
  (let [{:keys [status error body headers]} (get-doi accept doi)]
    (if error
      (-> (response/response "")
          (response/status 504))
      (-> (response/response "")
          (response/status status)
          (response/content-type (get headers :content-type))
          (apply-links headers)))))

(def all-routes
  (routes
   (GET "/" [] (redirect "http://crosscite.org/cn"))
   (GET "/styles" [] (make-api-redirect "styles"))
   (GET "/locales" [] (make-api-redirect "locales"))
   (HEAD "/*" {{doi :*} :params {accept "accept" host "host"} :headers}
         (cond
          (= data-host host)
          (proxy-doi-headers accept doi)
          (= id-host host)
          (make-data-redirect doi)
          :else
          (proxy-doi-headers accept doi)))
   (GET "/*" {{doi :*} :params {accept "accept" host "host"} :headers}
        (cond 
         (= data-host host)
         (proxy-doi accept doi)
         (= id-host host)
         (make-data-redirect doi)
         :else
         (proxy-doi accept doi)))))

(defn wrap-cors
  [h]
  (fn [request]
    (-> (h request)
        (assoc-in [:headers "Access-Control-Expose-Headers"] "Link")
        (assoc-in [:headers "Access-Control-Allow-Headers"] "*")
        (assoc-in [:headers "Access-Control-Allow-Origin"] "*")
        (assoc-in [:headers "Access-Control-Allow-Headers"]
                  (str "X-Requested-With, Accept, Accept-Encoding, "
                       "Accept-Charset, Accept-Language, Accept-Ranges, Cache-Control")))))

(def conneg
  (-> all-routes
      (logstash/wrap-logstash :host (env :logstash-host)
                              :port (env :logstash-port)
                              :name (env :logstash-name))
      (handler/api)
      (wrap-heartbeat)
      (wrap-cors)))

(defn -main [& args]
  (hs/run-server #'conneg {:port (env :server-port) :thread 64}))
  
