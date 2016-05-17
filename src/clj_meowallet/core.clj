(ns clj-meowallet.core
  (:require [environ.core :refer [env]]
            [clj-meowallet.http :as meowallet-http]
            [aleph.http :as http]))

(def sandbox-url "https://services.sandbox.meowallet.pt/api/v2/")
(def production-url "https://services.wallet.pt/api/v2/")

(defn host
  []
  (cond
    (= "true" (env :production)) production-url
    :else sandbox-url))

(defn add-headers
  [credentials http-ops]
  (if-let [token (:meo-wallet-api-key credentials)]
    (assoc http-ops :headers {"Content-Type" "application/json"
                              "Authorization" (str "WalletPT " token)})
    http-ops))

(defn add-body
  [http-ops data]
  (if-let [body (:body data)]
    (assoc http-ops :body (meowallet-http/parse-body body))
    http-ops))

(def mb-ref-url "mb/pay")

(defn prepare-data
  [credentials data path method]
  (let [host (host)
        http-opts (-> (add-headers credentials {})
                      (add-body data))]
    (assoc data :host host
                :requests 0
                :retries (- (or (:retries data) 3) 1)
                :url (str host path)
                :http-opts http-opts
                :request-method method
                :method-fn (cond
                             (= :post method) http/post
                             (= :put method) http/put
                             :else http/get))))

(defn generate-mb-ref
  [credentials data]
  (meowallet-http/fetch-response
    (prepare-data credentials data mb-ref-url :post)))
