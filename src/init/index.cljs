(ns init.index
  (:refer-clojure :exclude [atom])
  (:require [freactive.core :refer [atom]]
            [freactive.dom :as dom])
  (:require-macros [freactive.macros :refer [rx]]))

(defn log1 [& args]
  (js/console.log (pr-str args)))

(defn init-values [a b]
  (let [r (range 1 5)]
    (reset! a (rand-nth r))
    (reset! b (rand-nth r))))

(defn countdown-component [secs]
  (let [inv (js/setInterval
             #(reset! secs (dec @secs))
             1000)]

    [:div "Time Remaining: "
     (rx (do (when (<= @secs 0)
               (js/clearInterval inv))

             (str @secs)))]))

(defn fingers [a b]
  (let [f (fn [j] (cond (= j 0) []
                        (<= j 5) [j]
                        :else [5 (- j 5)]))

        arr [(f @a) (f @b)]]

    [:div
     (for [i (flatten arr)]
       [:img {:alt "" :src (str "f" i ".gif")}])
     ]))

(defn view []
  (let [cnt (atom 0)
        seconds-left (atom 30)
        a (atom 0) 
        b (atom 0)]

    (init-values a b)

    [:div 
     [:h1 "Math Super Hero"]
     (rx (fingers a b))
     [:h1
      ; (rx (str @a " + " @b " = "))
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

     (countdown-component seconds-left)
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
