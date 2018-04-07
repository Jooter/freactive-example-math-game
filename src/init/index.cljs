(ns init.index
  (:refer-clojure :exclude [atom])
  (:require [freactive.core :refer [atom cursor]]
            [freactive.dom :as dom])
  (:require-macros [freactive.macros :refer [rx]]))

(defn log1 [& args]
  (js/console.log (pr-str args)))

(defn countdown-component [seconds-left]
  (js/setInterval #(swap! seconds-left (fn [a] (if (> a 0) (dec a) 0))) 1000)
  (fn []  
    [:div "Time Remaining: " (rx (str @seconds-left))]
    ))

(defn view []
  (let [cnt (atom 0)
        seconds-left (atom 20)
        a (atom (rand-int 10)) 
        b (atom (rand-int 10))]
    [:div 
     [:h1 "Math Super Hero"]
     ((countdown-component seconds-left))
     [:h1
      (rx (str @a " + " @b " = "))
      [:input {:id "in1"
               :type "text" 
               :style {:border "solid #0000ff"} 
               :placeholder "Answer and Enter"
               :on-keypress
               (fn [e]
                 (let [v (-> e .-target .-value)
                       t (-> e .-target)
                       k (-> e .-key)]
                   (if (<= @seconds-left 0)
                     (aset t "disabled" true))
                   (when (= k "Enter")
                     (if (not= (str (+ @a @b)) v)
                       (aset t "style" "border:solid #ff0000")
                       (do
                         (aset t "style" "border:solid #0000ff")
                         (reset! a (rand-int 10))
                         (reset! b (rand-int 10))
                         (aset t "value" "")
                         (swap! cnt inc) 
                         )))))
               }]
      ]
     [:p "Total correct answers: " (rx (str @cnt))
      ]]))

(set!
 (.-onload js/window)
 (fn []
   (let [root (dom/append-child!
               (.-body js/document)
               [:div#root])]

     (aset js/document "title" "Math Hero")
     (dom/mount! root (view))
     (log1 "page has been mounted")
     (.focus (js/document.getElementById "in1"))
     )))
