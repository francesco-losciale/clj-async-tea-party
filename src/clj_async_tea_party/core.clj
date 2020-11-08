(ns clj-async-tea-party.core
  (:require [clojure.core.async :as async]))

(def tea-channel (async/chan))

;; synchronously

;;; getting on a value in `tea-channel` would break the thread (since we are going to use synchronous api)
;;; with buffered channels the thread is stopped only when the buffer gets full
(def tea-channel-with-buffer (async/chan 10))

;;; !! means a blocking call
;;; blocking put
(async/>!! tea-channel-with-buffer :cup-of-tea)
;;; blocking get, that drains the channel
(async/<!! tea-channel-with-buffer)

;;; close channel to new inputs (afterwards you can still get off values)
(async/close! tea-channel-with-buffer)

; asynchronously
;;; everything in a `go` block has its own special pool of threads.
;;; a take from the channel which would normally block, will only pause the execution instead.

(let [tea-channel (async/chan)]
  (async/go (async/>! tea-channel :cup-of-tea-1))
  (async/go (println "Thanks for the " (async/<! tea-channel))))

(def tea-channel (async/chan 10))
(async/go-loop []
  (println "Thanks for the " (async/<! tea-channel))
  (recur))

(async/>!! tea-channel :hot-cup-of-tea)
(async/>!! tea-channel :hot-with-sugar)
(async/>!! tea-channel :hot-with-milk)

