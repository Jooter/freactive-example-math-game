(ns main.core
  (:refer-clojure :exclude [atom])
  (:require [freactive.core :refer [atom cursor]]
            [freactive.dom :as dom]
            [chord.client :refer [ws-ch]]
            [cljs.core.async :refer [<! >! put! close!]])
  (:require-macros [cljs.core.async.macros :refer [go]]
                   [freactive.macros :refer [rx]]))
    
(defn log1 [& args]
  (js/console.log (pr-str args)))

(defn countdown-component [seconds-left]
  (js/setInterval #(swap! seconds-left (fn [a] (if (> a 0) (dec a) 0))) 1000)
  (fn []  
    [:div "Time Remaining: " (rx (str @seconds-left))]
    ))

(defn view []
  (let [cnt (atom 0)
        seconds-left (atom 10)
        a (atom (rand-int 10)) 
        b (atom (rand-int 10))]
    [:div 
      [:h1 "Math Super Hero"]
      ((countdown-component seconds-left))
      [:p]
      (rx (str @a " + " @b " = "))
      [:input {:id "in1"
               :type "text" 
               :autofocus "autofocus"
               :style {:width "30%"} 
               :placeholder "Answer and Enter"
               :on-keypress
                (fn [e]
                  (let [v (-> e .-target .-value)
                        t (-> e .-target)
                        k (-> e .-key)]
                     (when (= k "Enter")
                       (when (= (str (+ @a @b)) v)
                         (reset! a (rand-int 10))
                         (reset! b (rand-int 10))
                         (if (> @seconds-left 0)
                           (swap! cnt inc) 
                           (aset t "disabled" true))
                         (aset t "value" "")
                         ))))
              }]
      [:p "Total correct answers: " (rx (str @cnt))
      ]]))

(defonce root (dom/append-child! (.-body js/document) [:div#root]))

(dom/mount! root (view))

(set! (.-onload js/window)
  (fn []
    (log1 "onload")))
