(ns tictactoe.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.core.matrix :as matrix]))

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

(defn win-column? [turn]
  (let [board (:board @app-state)]
    (some true? (map (fn [seq] (every? #(= % turn) seq)) board))))

(defn win-row? [turn]
  (let [board (:board @app-state)
        transposed-board (apply mapv vector board)]
    (some true? (map (fn [seq] (every? #(= % turn) seq)) transposed-board))))

(defn win-diagonal? [turn]
  (let [diagonal-a (matrix/diagonal (:board @app-state))
        diagonal-b (matrix/diagonal (apply vector (reverse (:board @app-state))))]
    (do
      (prn diagonal-a)
      (prn diagonal-b)
      (or
       (every? #(= % turn) diagonal-a)
       (every? #(= % turn) diagonal-b)))))

(defn win? [turn]
  (or (win-row? turn) (win-column? turn) (win-diagonal? turn)))

(defn positions-with-values 
  [board] 
  (flatten 
   (map-indexed 
    (fn [i m] 
      (map-indexed 
       (fn [j n] {:i i :j j :value n}) m)) board)))

(defn empty-positions 
  [board] 
  (filter (fn [x] (= (:value x) 0)) (positions-with-values board)))

(defn new-board
  [board cell player]
  (let 
      [i (:i cell)
       j (:j cell)]
    (assoc-in board [i j] player)))

(defn make-a-minimax-tree
  [tree current-player]
  (let [board (:value tree)
        sub-nodes (vec (map (fn [cell] (new-board board cell current-player)) (empty-positions board)))
        prev-player (if (= current-player 1) 2 1)]
    (assoc 
     tree 
     :nodes 
     (vec (map (fn [node] (make-a-minimax-tree {:value node} prev-player)) sub-nodes)))))

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
                          (js/alert (str turn " wins")))))))}])))])
  
(reagent/render-component [tictactoe]
                          (. js/document (getElementById "app")))


(defn on-js-reload [])
