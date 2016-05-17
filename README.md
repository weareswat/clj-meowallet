# Clj MEO WALLET [![Build Status](https://travis-ci.org/weareswat/clj-meowallet.svg?branch=master)](https://travis-ci.org/weareswat/clj-meowallet)
[![Clojars Project](http://clojars.org/weareswat/clj-meowallet/latest-version.svg)](http://clojars.org/clj-mailgun)

A Clojure Wrapper to MEO WALLET API.

Installation
-----

```clj-meowallet``` is available as a Maven artifact from [Clojars](https://clojars.org/weareswat/clj-meowallet)

With Leiningen/Boot:

```clojure
[clj-meowallet "0.0.1"]
```

#### Tests

* `lein test` - runs the test suite *
* `script/autotest` -listen for file changes and is always running tests

* To Run the integration test you should provide a valid Meo Wallet API KEY with access to mb references api.
  `MEO_WALLET_API_KEY=<YOUR_MEO_WALLET_API_KEY> lein test`

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
