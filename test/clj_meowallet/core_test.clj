(ns clj-meowallet.core-test
  (:require [environ.core :refer [env]]
            [clojure.test :refer :all]
            [clojure.core.async :refer [<!!]]
            [clj-meowallet.core :as core]))

(deftest build-url-test
  (testing "sandbox url"
    (is (= core/sandbox-url
           (core/host)))))

(deftest add-headers-test
  (let [credentials {:meo-wallet-api-key "qweqweqwk"}
        headers {"Content-Type" "application/json"
                 "Authorization" (str "WalletPT " (:meo-wallet-api-key credentials))}
        result (core/add-headers credentials {})]
    (is (= headers (:headers result)))))

(deftest add-body-test
  (let [data {:body {:payment {:client {:name "John Santos"
                                        :email "johnsantos@mail.com"
                                        :address {:country "pt"
                                                  :address "Av. Fontes Pereira de Melo"
                                                  :city "Lisboa"}}
                               :amount 229
                               :currency "EUR"
                               :items [{:ref 123
                                        :name "InvoiceXpress plan"
                                        :descr "New subscription to invoicexpress"
                                        :qt 1}]
                               :ext_invoiceid "C12423324"}}}
        result (core/add-body data {})]
    (is (= (:body data)
           (:body result)))))

(deftest prepare-data-test
  (let [credentials {:meo-wallet-api-key "qweqweqwk"}
        data {:body {:payment {:client {:name "John Santos"
                                        :email "johnsantos@mail.com"
                                        :address {:country "pt"
                                                  :address "Av. Fontes Pereira de Melo"
                                                  :city "Lisboa"}}
                               :amount 229
                               :currency "EUR"
                               :items [{:ref 123
                                        :name "InvoiceXpress plan"
                                        :descr "New subscription to invoicexpress"
                                        :qt 1}]
                               :ext_invoiceid "C12423324"}}}
        path core/mb-ref-url
        method :post
        result (core/prepare-data credentials data path method)]

      (testing "host"
        (is (= (core/host)
               (:host result))))

      (testing "retries"
        (is (= 2
               (:retries result))))

      (testing "url"
        (is (= (str (core/host) path)
               (:url result))))

      (testing "request-method"
        (is (= method
               (:request-method result))))

      (testing "http-ops"
        (is (not (nil? (:http-opts result)))))

      (testing "method-fn"
        (is (not (nil? (:method-fn result)))))))

(deftest generate-mb-ref-test
  (if-not (env :meo_wallet_api_key)
    (println "Warning: No meo wallet api key on env (ignoring test)")

    (let [credentials {:meo-wallet-api-key (env :meo_wallet_api_key)}
          body {:amount 10
              :currency "EUR"
              :expires "2016-05-18T15:59:58+0000"
              :ext_invoiceid "i00001232"}
        data {:body body}
        result (<!! (core/generate-mb-ref credentials data))]

    (testing "amount"
      (is (= (:amount body)
             (:amount result))))

    (testing "currency"
      (is (= (:currency body)
             (:currency result))))

    (testing "method"
      (is (= "MB"
             (:method result))))

    (testing "type"
      (is (= "PAYMENT"
             (:type result))))

    (testing "expires"
      (is (= (:expires body)
             (:expires result))))

    (testing "status"
      (is (= "PENDING"
             (:status result))))

    (testing "mb"
      (testing "ref"
        (is (get-in result [:mb :ref])))
      (testing "entity"
        (is (get-in result [:mb :entity])))))))
