(ns app.mutations
  (:require
    [app.resolvers :refer [list-table]]
    [com.wsscode.pathom.connect :as pc]
    [taoensso.timbre :as log]))

(pc/defmutation delete-person [env {list-id   :list/id
                                    person-id :person/id}]
                ;; optional, this is how you override what symbol it responds to.  Defaults to current ns.
                {::pc/sym `delete-person}
                (log/info "Deleting person" person-id "from list" list-id)
                (swap! list-table update list-id update :list/people (fn [old-list] (filterv #(not= person-id %) old-list))))

(def mutations [delete-person])