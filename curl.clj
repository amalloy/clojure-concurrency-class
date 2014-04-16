(ns concurrency
  (:import (java.util.concurrent ArrayBlockingQueue TimeUnit
                                 ThreadPoolExecutor ThreadPoolExecutor$CallerRunsPolicy)))

(defn read-url [url]
  [url (slurp url)])

;; single-threaded, get one and then the next
(defn read-urls1 [urls]
  (into {} (map read-url urls)))

;; use pmap for an easy speedup
(defn read-urls2 [urls]
  (into {} (pmap read-url urls)))

;; start N futures yourself - has different issues than pmap, eg too many threads at once
(defn read-urls3 [urls]
  (let [result (atom {})
        futures (for [url urls]
                  (future (swap! result conj (read-url url))))]
    (dorun futures)
    (dorun (map deref futures))
    @result))

;; use java.util.concurrent to control thread pooling
(defn read-urls4 [urls num-threads]
  (let [result (atom {})
        queue (ArrayBlockingQueue. (* 5 num-threads))
        e (ThreadPoolExecutor. num-threads num-threads 1 TimeUnit/SECONDS queue
                               (ThreadPoolExecutor$CallerRunsPolicy.))
        jobs (for [url urls]
               #(swap! result conj (read-url url)))]
    (doseq [^Runnable f jobs]
      (.execute e f))
    (.shutdown e)
    (.awaitTermination e 30 TimeUnit/SECONDS)
    @result))
