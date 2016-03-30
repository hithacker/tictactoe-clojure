(ns tictactoe.core
  (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

(println "Edits to this text should show up in your developer console.")

;; define your app data so that it doesn't get over-written on reload

(defn new-board [n]
  (vec (repeat n [nil nil nil])))

(defonce app-state
  (atom {:text "Welcome to Tic Tac Toe"
         :board (new-board 3)
         :turn :p1}))

(defn next-turn [turn]
  (if (= turn :p1)
    :p2
    :p1))

(defn win? [turn]
  (let [board (:board @app-state)]
    (if (some true? (map (fn [seq] (every? #(= % turn) seq)) board))
      true
      false)))

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
          j (range 3)]
      (let [cell-value (get-in @app-state [:board i j])]
        [:rect {:x i
                :y j
                :width 0.99
                :height 0.99
                :fill (cond
                        (= :p1 cell-value) "yellow"
                        (= :p2 cell-value) "red"
                        :else "green")
                :on-click
                (fn rect-click [e]
                  (let [turn (:turn @app-state)]
                    (if (nil? cell-value)
                      (do
                        (swap! app-state
                               assoc
                               :turn
                               (next-turn turn))
                        (swap! app-state
                               assoc-in
                               [:board i j]
                               (next-turn turn))
                        (if (win? (next-turn turn))
                          (js/alert (str (turn) " wins")))))))}])))])
  
(reagent/render-component [tictactoe]
                          (. js/document (getElementById "app")))


(defn on-js-reload [])
