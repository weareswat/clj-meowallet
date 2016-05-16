(ns clj-meowallet.core-test
  (:require [clojure.test :refer :all]
            [clj-meowallet.core :as core]))

(deftest url-builder-test
  (testing "sandbox url"
    (is (= core/sandbox-url
           (core/url-builder)))))

(deftest authentify-test
  (let [credentials {:token "qweqweqwk"}
        header {"Authorization: WalletPT " (:token credentials)}
        result (core/authentify credentials {})]
    (is (= header (:headers result)))))

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
