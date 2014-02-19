(ns cayenne-data.convert
  (:require [clojure.data.json :as json]
            [cayenne-data.lookup :as lookup]
            [cayenne.formats.citation :as citation]
            [cayenne.formats.ris :as ris]
            [cayenne.formats.rdf :as rdf]))

(defmulti ->representation 
  "Convert citeproc DOI metadata to the given content-type."
  (fn [content-type metadata] (:base content-type)))

(defmethod ->representation
  "application/vnd.crossref.unixref+xml" [_ metadata]
  (lookup/get-unixref (:DOI metadata)))

(defmethod ->representation
  "application/vnd.crossref.unixsd+xml" [_ metadata]
  (lookup/get-unixsd (:DOI metadata)))

(defmethod ->representation
  "application/rdf+xml" [_ metadata]
  (rdf/->xml metadata))

(defmethod ->representation
  "text/turtle" [_ metadata]
  (rdf/->turtle metadata))

(defmethod ->representation
  "text/n3" [_ metadata]
  (rdf/->n3 metadata))

(defmethod ->representation
  "text/n-triples" [_ metadata]
  (rdf/->n-triples metadata))

(defmethod ->representation
  "application/x-research-info-systems" [_ metadata]
  (ris/->ris metadata))

(defmethod ->representation 
  "application/vnd.citationstyles.csl+json" [_ metadata]
  (json/json-str metadata))

(defmethod ->representation
  "text/x-bibliography" [content-type metadata]
  (let [args (concat
              (when-let [style (get-in content-type [:params :style])]
                [:style style])
              (when-let [lang (get-in content-type [:params :language])]
                [:language lang])
              (when-let [format (get-in content-type [:params :format])]
                [:format format]))]
    (apply citation/->citation metadata args)))

(defmethod ->representation
  "application/x-bibtex" [_ metadata]
  (citation/->citation metadata :style "bibtex"))
