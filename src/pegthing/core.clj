(ns pegthing.core
  (:gen-class))

;; TODO explain why it's nice to start at 0
;; used to produce range of peg positions on first row
(defn tri*
  "Generates lazy sequence of triangular numbers"
  ([] (tri* 0 0))
  ([sum n]
     (let [new-sum (+ sum n)]
       (cons new-sum (lazy-seq (tri* new-sum (inc n)))))))

(def tri (tri*))

(defn triangular?
  [n]
  (= n (last (take-while #(>= n %) tri))))

(defn add-connection
  [new-connection connections]
  (if (nil? connections)
    [new-connection]
    (conj connections new-connection)))

(defn join-positions
  [board p1 p2 axis]
  (reduce (fn [board [p1 p2]]
            (update-in board [p1 axis] (partial add-connection p2)))
          board
          [[p1 p2]
           [p2 p1]]))

(defn neighbors
  [row-num pos]
  (conj (if-not (triangular? pos) [[:a (inc pos)]])
        [:b (+ row-num pos)]
        [:c (+ row-num pos 1)]))

(defn add-peg
  [board row-num pos]
  (let [board (assoc-in board [pos :pegged] true)]
    (reduce (fn [board [axis neighbor-pos]]
              (join-positions board pos neighbor-pos axis))
            board
            (neighbors row-num pos))))

(defn peg-positions
  [row-num]
  (map inc (range (nth tri (dec row-num))
                  (nth tri row-num))))

(defn add-row
  [board row-num]
  (reduce (fn [board pos] (add-peg board row-num pos))
          board
          (peg-positions row-num)))

(defn add-rows
  [board rows]
  (reduce add-row board (range 1 (inc rows))))

(defn new-board
  [rows]
  (let [board {}]
    (add-rows board rows)))


;; printing the board
(def letters (map (comp str char) (range 97 123)))
(def pos-chars 3)

(defn row-padding
  [row-string rows]
  (let [max-row-chars (* rows pos-chars)
        pad-length (/ (- max-row-chars (count row-string)) 2)]
    (apply str (take pad-length (repeat " ")))))

(defn render-pos
  [board pos]
  (str (nth letters (dec pos))
       (if (get-in board [pos :pegged]) "0" "-")))

(defn render-row
  [board rows row-num]
  (clojure.string/join " " (map (partial render-pos board) (peg-positions row-num))))

(defn print-board
  [board rows]
  (doseq [row-num (range 1 (inc rows))]
    (let [row-string (render-row board rows row-num)
          padding (row-padding row-string rows)]
      (println padding row-string))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
