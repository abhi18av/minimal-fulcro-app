(ns app.parser
  (:require
    [app.resolvers]
    [com.wsscode.pathom.core :as p]
    [com.wsscode.pathom.connect :as pc]
    [taoensso.timbre :as log]))

;; NOTE connect the clojure-cli repl via spacemacs and this works fine then. This is the http-kit / backend server repl

(def resolvers [app.resolvers/resolvers])

(def pathom-parser
  (p/parser {::p/env     {::p/reader                 [p/map-reader
                                                      pc/reader2
                                                      pc/ident-reader
                                                      pc/index-reader]
                          ::pc/mutation-join-globals [:tempids]}
             ::p/mutate  pc/mutate
             ::p/plugins [(pc/connect-plugin {::pc/register resolvers})
                          p/error-handler-plugin]}))

(defn api-parser [query]
  (log/info "Process" query)
  (pathom-parser {} query))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(comment
;; NOTE execute these in the clj -A:dev repl or clojure-cli repl
(app.parser/api-parser [{[:list/id :friends] [:list/id]}])

(app.parser/api-parser [{[:person/id 1] [:person/name]}])

(app.parser/api-parser [{[:list/id :friends] [:list/id {:list/people [:person/name]}]}])
)
