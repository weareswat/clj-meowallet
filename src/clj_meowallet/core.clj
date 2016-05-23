(ns clj-meowallet.core
  (:require [environ.core :refer [env]]
            [request-utils.core :as request-utils]))

(def sandbox-url "https://services.sandbox.meowallet.pt/api/v2/")
(def production-url "https://services.wallet.pt/api/v2/")

(defn host
  []
  (cond
    (= "true" (env :production)) production-url
    :else sandbox-url))

(defn headers
  [credentials]
  (when-let [token (:meo-wallet-api-key credentials)]
    {"Content-Type" "application/json"
     "Authorization" (str "WalletPT " token)}))

(def mb-ref-url "mb/pay")
(def verify-callback-url "callback/verify")

(defn prepare-data
  [credentials data path]
  (assoc data :host (host)
              :path path
              :headers (headers credentials)
              :body data))

(defn prepare-data-to-generate-mb-ref
  [credentials data]
  (prepare-data credentials data mb-ref-url))

(defn generate-mb-ref
  [credentials data]
  "This fn generates an mb-ref by doing a request to the path given by `mb-ref-url`"
  (-> (prepare-data-to-generate-mb-ref credentials data)
      (request-utils/http-post)))

(defn prepare-data-to-verify-callback
  [credentials data]
  (prepare-data credentials data verify-callback-url))

(defn verify-callback
  "This fn verifies if the callback data is valid or not by doing a 
  request to the path given by `verify-callback-url`"
  [credentials data]
  (-> (prepare-data-to-verify-callback credentials data)
      (request-utils/http-post)))
