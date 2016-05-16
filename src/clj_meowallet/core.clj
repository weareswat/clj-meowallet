(ns clj-meowallet.core
  (:require [environ.core :refer [env]]))

(def sandbox-url "https://services.sandbox.meowallet.pt/api/v2/")
(def production-url "https://services.wallet.pt/api/v2/")

(defn url-builder
  []
  (cond
    (env :production) production-url
    :else sandbox-url))

(defn authentify
  [data http-ops]
  (if-let [token (:token data)]
    (assoc http-ops :headers {"Authorization: WalletPT " token})
    http-ops))
