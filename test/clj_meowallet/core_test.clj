(ns clj-meowallet.core-test
  (:require [clojure.test :refer :all]
            [clj-meowallet.core :as core]))

(deftest url-builder-test
  (testing "sandbox url"
    (is (= core/sandbox-url
           (core/url-builder)))))
