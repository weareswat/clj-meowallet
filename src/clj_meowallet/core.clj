(ns clj-meowallet.core
  (:require [environ.core :refer [env]]
            [aleph.http :as http]))

(def sandbox-url "https://services.sandbox.meowallet.pt/api/v2/")
(def production-url "https://services.wallet.pt/api/v2/")

(defn url-builder
  []
  (cond
    (env :production) production-url
    :else sandbox-url))

(defn authentify
  [credentials http-ops]
  (if-let [token (:token credentials)]
    (assoc http-ops :headers {"Authorization: WalletPT " token})
    http-ops))

(defn add-body
  [data http-ops]
  (if-let [body (:body data)]
    (assoc http-ops :body body)
    http-ops))

(def mb-ref-url "mb/pay")

(defn generate-mb-ref
  [credentials data]

  )
