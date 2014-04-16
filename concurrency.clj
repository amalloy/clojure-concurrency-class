(let [counter (atom 0)
      num-threads 10
      actions-per-thread 10000000
      threads (doall (for [i (range num-threads)]
                       (future
                         (dotimes [_ actions-per-thread]
                           (swap! counter inc)))))]
  (doseq [thread threads]
    @thread)
  @counter)

;; prints 100000000 every time
