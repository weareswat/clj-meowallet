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

(defn prepare-data
  [credentials data]
  (assoc data :host (host)
              :path mb-ref-url
              :headers (headers credentials)
              :body data))

(defn generate-mb-ref
  [credentials data]
  (-> (prepare-data credentials data)
      (request-utils/http-post)))
