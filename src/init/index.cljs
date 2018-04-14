(ns init.index
  (:refer-clojure :exclude [atom])
  (:require [freactive.core :refer [atom]]
            [freactive.dom :as dom])
  (:require-macros [freactive.macros :refer [rx]]))

(defn log1 [& args]
  (js/console.log (pr-str args)))

(defn countdown-component [seconds-left]
  (js/setInterval #(swap! seconds-left (fn [a] (if (> a 0) (dec a) 0))) 1000)
  (fn []  
    [:div "Time Remaining: " (rx (str @seconds-left))]
    ))

(defn init-values [a b]
  (reset! a (rand-int 10))
  (reset! b (rand-int 10)))

(defn view []
  (let [cnt (atom 0)
        seconds-left (atom 30)
        a (atom 0) 
        b (atom 0)]

    (init-values a b)

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
                         (init-values a b)
                         (aset t "value" "")
                         (swap! cnt inc) 
                         )))))
               }]]

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
