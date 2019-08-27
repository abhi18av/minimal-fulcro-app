(ns app.ui
  (:require
    ;; internal namespaces
    [app.mutations :as api]
    ;; external libraries
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Person Component

(defsc Person [this {:person/keys [name age] :as props} {:keys [onDelete]}]
       {:query         [:person/id :person/name :person/age]
        :ident         (fn [] [:person/id (:person/id props)])
        :initial-state (fn [{:keys [id name age] :as params}] {:person/id id :person/name name :person/age age})}
       (dom/li
         (dom/h5 (str name " (age: " age ")") (dom/button {:onClick #(onDelete name)} "X"))))

;; The keyfn generates a react key for each element based on props.
;; See React documentation on keys.
(def ui-person (comp/factory Person {:keyfn :person/name}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PersonList Component

(defsc PersonList [this {:list/keys [id label people] :as props}]
       {:query [:list/id :list/label {:list/people (comp/get-query Person)}] ; (5)
        :ident (fn [] [:list/id (:list/id props)])
        :initial-state
               (fn [{:keys [id label]}]
                 {:list/id     id
                  :list/label  label
                  :list/people (if (= id :friends)
                                 [(comp/get-initial-state Person {:id 1 :name "Sally" :age 32})
                                  (comp/get-initial-state Person {:id 2 :name "Joe" :age 22})]
                                 [(comp/get-initial-state Person {:id 3 :name "Fred" :age 11})
                                  (comp/get-initial-state Person {:id 4 :name "Bobby" :age 55})])})}
       (let [delete-person (fn [item-id] (comp/transact! this [(api/delete-person {:list id :item item-id})]))] ; (4)
         (dom/div
           (dom/h4 label)
           (dom/ul
             (map (fn [p] (ui-person (comp/computed p {:onDelete delete-person}))) people)))))

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
