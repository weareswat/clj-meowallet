(ns clj-meowallet.core-test
  (:require [clojure.test :refer :all]
            [clj-meowallet.core :as core]))

(deftest build-url-test
  (testing "sandbox url"
    (is (= core/sandbox-url
           (core/host)))))

(deftest add-headers-test
  (let [credentials {:token "qweqweqwk"}
        headers {"Content-Type:" "application/json"
                 "Authorization: WalletPT " (:token credentials)}
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
  (let [credentials {:token "qweqweqwk"}
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
