(ns tictactoe.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defn new-board [n]
  (vec (repeat n [0 0 0])))

(prn (new-board 3))

(defonce app-state
  (atom {:text "Welcome to Tic Tac Toe"
         :board (new-board 3)}))

(defn tictactoe []
  [:div
   [:h1 (:text @app-state)]
   (into
    [:svg
     {:view-box "0 0 3 3"
      :width 500
      :height 500
      :style {:border "1px solid #cccccc"}}]
    (for [i (range (count (:board @app-state)))
          j (range (count (:board @app-state)))]
      [:rect {:x i
              :y j
              :width 0.9
              :height 0.9
              :fill "green"
              :on-click
              (fn rect-click [e]
                (prn "You clicked me!" i j)
                (prn
                 (swap! app-state update-in [:board i j] inc)))}]))])

(reagent/render-component [tictactoe]
                          (. js/document (getElementById "app")))


(defn on-js-reload [])
