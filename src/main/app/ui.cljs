(ns app.ui
  (:require
    [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
    [com.fulcrologic.fulcro.dom :as dom]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Person Component
(defsc Person [this {:person/keys [name age]}]
       {:initial-state (fn [{:keys [name age] :as params}]
                         {:person/name name :person/age age})}
       (dom/li
         (dom/h5 (str name "(age: " age ")"))))


;; The keyfn generates a react key for each element based on props.
;; See React documentation on keys.
(def ui-person (comp/factory Person {:keyfn :person/name}))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; PersonList Component

(defsc PersonList [this {:list/keys [label people]}]
       {:initial-state (fn [{:keys [label]}]
                         {:list/label  label
                          :list/people (if (= label "Friends")
                                         ;; friend
                                         [(comp/get-initial-state Person {:name "Sally" :age 32})
                                          (comp/get-initial-state Person {:name "Joe" :age 22})]
                                         ;; enemies
                                         [(comp/get-initial-state Person {:name "Fred" :age 11})
                                          (comp/get-initial-state Person {:name "Bobby" :age 55})])})}
       (dom/div
         (dom/h4 label)
         (dom/ul
           (map ui-person people))))

(def ui-person-list (comp/factory PersonList))



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Main entry point for ReactJS

(defsc Root [this {:keys [friends enemies]}]
       {:initial-state (fn [params] {:friends (comp/get-initial-state PersonList {:label "Friends"})
                                     :enemies (comp/get-initial-state PersonList {:label "Enemies"})}) }
       (dom/div
         (ui-person-list friends)
         (ui-person-list enemies)))