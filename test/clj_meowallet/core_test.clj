(ns clj-meowallet.core-test
  (:require [environ.core :refer [env]]
            [result.core :as result]
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
        result (core/headers credentials)]
    (is (= headers result))))

(deftest generate-mb-ref-test
  (if-not (env :meo-wallet-api-key)
    (println "Warning: No meo wallet api key on env (ignoring test)")

    (let [credentials {:meo-wallet-api-key (env :meo-wallet-api-key)}
          data {:amount 10
                :expires "2060-05-19T23:12:58+0000"
                :currency "EUR"
                :ext_invoiceid "i00001232"}
        result (<!! (core/generate-mb-ref credentials data))
        body (:body result)]

    (testing "amount"
      (is (= (:amount data)
             (:amount body))))

    (testing "currency"
      (is (= (:currency data)
             (:currency body))))

    (testing "method"
      (is (= "MB"
             (:method body))))

    (testing "type"
      (is (= "PAYMENT"
             (:type body))))

    (testing "success"
      (is (result/succeeded? result)))

    (testing "expires"
      (is (:expires body)))

    (testing "status"
      (is (= "PENDING"
             (:status body))))

    (testing "mb"
      (testing "ref"
        (is (get-in body [:mb :ref])))
      (testing "entity"
        (is (get-in body [:mb :entity])))))))

(deftest generate-mb-ref-with-invalid-api-key-test
  (let [credentials {:meo-wallet-api-key "qweqweqw"}
        data {:amount 10
              :currency "EUR"
              :ext_invoiceid "i00001232"}
      result (<!! (core/generate-mb-ref credentials data))]

  (testing "status"
    (is (= 401
           (:status result))))

  (testing "success"
    (is (result/failed? result)))))

(deftest verify-callback-test
  (if-not (env :meo-wallet-api-key)
    (println "Warning: No meo wallet api key on env (ignoring test)")

    (let [credentials {:meo-wallet-api-key (env :meo-wallet-api-key)}
          data {:amount 10
                :currency "EUR"
                :event "COMPLETED"
                :ext-customerid "00001"
                :ext-email "noreply@sapo.pt"
                :ext-invoiceid "38440200100"
                :method "WALLET"
                :operation-id "qwkjehqkjwhe"
                :operation-status "COMPLETED"
                :user "237"}
        result (<!! (core/verify-callback credentials data))]

    (is (not (result/succeeded? result)))
    (is (= false
           (:body result)))
    )))
