(ns app.ui
  (:require
    ;; internal namespaces
    [app.mutations :as api]
    ;; external libraries
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Person Component
;; TODO figure out where is the mutation broken - it is sensed by the fulcro inspector but doesn't reflect in the UI
(defsc Person [this {:person/keys [id name age] :as props} {:keys [onDelete]}]
  {:query [:person/id :person/name :person/age]
   :ident (fn [] [:person/id (:person/id props)])}
  (dom/li
    (dom/h5 (str name " (age: " age ")") (dom/button {:onClick #(onDelete id)} "X"))))

;; The keyfn generates a react key for each element based on props.
;; See React documentation on keys.
(def ui-person (comp/factory Person {:keyfn :person/id}))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PersonList Component

(defsc PersonList [this {:list/keys [id label people] :as props}]
  {:query [:list/id :list/label {:list/people (comp/get-query Person)}]
   :ident (fn [] [:list/id (:list/id props)])}
  (let [delete-person (fn [person-id] (comp/transact! this [(api/delete-person {:list/id id :person/id person-id})]))] ; (2)
    (dom/div
      (dom/ul
        (map #(ui-person (comp/computed % {:onDelete delete-person})) people)))))


(def ui-person-list (comp/factory PersonList))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main entry point for ReactJS

(defsc Root [this {:keys [friends enemies]}]
  {:query         [{:friends (comp/get-query PersonList)}
                   {:enemies (comp/get-query PersonList)}]
   :initial-state {}}
  (dom/div
    (dom/h3 "Friends")
    (when friends
      (ui-person-list friends))
    (dom/h3 "Enemies")
    (when enemies
      (ui-person-list enemies))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(comment
;; NOTE: CLJS repl in IntelliJ
(shadow/repl :main)

;; NOTE:
(ns app.ui)

;; NOTE: Inspect application state at the repl
(def state (com.fulcrologic.fulcro.application/current-state app.application/app))

(def query (com.fulcrologic.fulcro.components/get-query app.ui/Root))
(com.fulcrologic.fulcro.algorithms.denormalize/db->tree query state state)

) ;; end of comment
