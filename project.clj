(defproject weareswat/clj-meowallet "0.3.0"
  :description "FIXME: write description"
  :url "https://github.com/weareswat/clj-meowallet"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [aleph "0.4.1"]
                 [clanhr/result "0.11.0"]
                 [cheshire "5.6.1"]
                 [org.clojure/core.async "0.2.374"]
                 [environ "1.0.3"]]
  :profiles {:dev {:plugins [[com.jakemccrary/lein-test-refresh "0.15.0"]]}})
