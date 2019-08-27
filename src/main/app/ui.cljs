(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Person Component
(defsc Person [this {:person/keys [name age]} {:keys [onDelete]}] ; (2)
       {:query         [:person/name :person/age]
        :initial-state (fn [{:keys [name age] :as params}] {:person/name name :person/age age})}
       (dom/li
         (dom/h5 (str name " (age: " age ")") (dom/button {:onClick #(onDelete name)} "X")))) ; (2)


;; The keyfn generates a react key for each element based on props.
;; See React documentation on keys.
(def ui-person (comp/factory Person {:keyfn :person/name}))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PersonList Component

(defsc PersonList [this {:list/keys [label people]}] ;
       {:query [:list/label {:list/people (comp/get-query Person)}]
        :initial-state
               (fn [{:keys [label]}]
                 {:list/label  label
                  :list/people (if (= label "Friends")
                                 [(comp/get-initial-state Person {:name "Sally" :age 32})
                                  (comp/get-initial-state Person {:name "Joe" :age 22})]
                                 [(comp/get-initial-state Person {:name "Fred" :age 11})
                                  (comp/get-initial-state Person {:name "Bobby" :age 55})])})}
       (let [delete-person (fn [name] (println label "asked to delete" name))] ; (1)
         (dom/div
           (dom/h4 label)
           (dom/ul
             (map (fn [p] (ui-person (comp/computed p {:onDelete delete-person}))) people))))) ; (1)

(def ui-person-list (comp/factory PersonList))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main entry point for ReactJS

(defsc Root [this {:keys [friends enemies]}]
       {:initial-state (fn [params] {:friends (comp/get-initial-state PersonList {:label "Friends"})
                                     :enemies (comp/get-initial-state PersonList {:label "Enemies"})})}
       (dom/div
         (ui-person-list friends)
         (ui-person-list enemies)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; NOTE: CLJS repl in IntelliJ
;; (shadow/repl :main)

;; NOTE: Inspect (initial) Root element state at the repl
;; (com.fulcrologic.fulcro.application/current-state app.application/app)

;; NOTE: Inspect application state at the repl
;; (com.fulcrologic.fulcro.application/current-state app.application/app)

;; NOTE: Inspect the db as a tree
;; (com.fulcrologic.fulcro.algorithms.denormalize/db->tree
;;  [{:friends [:list/label]}] (comp/get-initial-state app.ui/Root {}) {})

