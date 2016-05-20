(ns clj-meowallet.http
  (:require [cheshire.core :as json]
            [clojure.string :as str]
            [result.core :as result]
            [clojure.core.async :refer [chan <!! >!! close! go <! timeout]]
            [manifold.deferred :as d]
            [aleph.http :as http]))

(defn- retry?
  "Verifies that the given error response is the final one, or that
  we should try it again."
  [data response]
  (and (instance? java.util.concurrent.TimeoutException response)
       (not= 0 (int (:retries data)))))

(def final-response? (comp not retry?))

(defn- get-success
  "Checks if the response was successful, based on the status code"
  [response]
  (let [first-char (-> response :status str first)]
    (or (= \2 first-char)
        (= \3 first-char) )))

(defn- prepare-response
  "Handles post-response"
  [data response]
  (try
    (merge {:success (get-success response)
            :status (:status response)
            :requests (inc (:requests data))}
           (json/parse-string (slurp (:body response)) true))
    (catch Exception ex
      {:exception ex})))

(defn parse-body
  [body]
  (if (or (map? body) (seq? body))
    (json/generate-string body)
    body))

(defn- prepare-error
  "Handles post-response errors"
  [data response]
  (try
    (cond
      (instance? java.util.concurrent.TimeoutException response)
        {:status 408
         :error (str "Error getting " (:url data))
         :request-time (-> data :http-opts :request-timeout)
         :requests (inc (:requests data))
         :data {:message "Timed out"}}
      (instance? clojure.lang.ExceptionInfo response)
        (merge {:status (.getMessage response)
                :error (str "Error getting " (:url data))
                :requests (inc (:requests data))
                :request-time (:request-time (.getData response))}
                (json/parse-string (slurp (:body (.getData response))) true))
      (instance? Throwable response)
        {:error (str "Error getting " (:url data))
         :caused-by response}
      :else
        (-> response
            (assoc :error (str "Error getting " (:url data)))
            (assoc :requests (inc (:requests data)))
            (assoc :status (-> response :data :cause))
            (assoc :body-data (slurp (-> response :data :body)))))
    (catch Exception ex
      {:error (str "Error getting " (:url data))
       :exception (.getMessage ex)})))

(defn fetch-response
  "Fetches the response for a given URL"
  [data]
  (try
    (let [result-ch (or (:result-ch data) (chan 1))
          async-stream ((:method-fn data) (:url data) (:http-opts data))]
      (d/on-realized async-stream
                     (fn [x]
                       (if x
                         (>!! result-ch (prepare-response data x))
                         (>!! result-ch (result/failure (prepare-response data x))))
                       (close! result-ch))
                     (fn [x]
                       (if (final-response? data x)
                         (do
                           (>!! result-ch (prepare-error data x))
                           (close! result-ch))
                         (go
                           (<! (timeout (* 300 (+ 1 (int (:requests data))))))
                           (fetch-response (-> data
                                               (assoc :result-ch result-ch)
                                               (update :retries dec)
                                               (update :requests inc)))))))
      result-ch)
    (catch Exception ex
      (go (result/failure ex)))))
