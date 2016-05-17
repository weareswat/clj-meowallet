# Clj MEO WALLET [![Build Status](https://travis-ci.org/weareswat/clj-meowallet.svg?branch=master)](https://travis-ci.org/weareswat/clj-meowallet)
[![Clojars Project](https://clojars.org/weareswat/clj-meowallet/latest-version.svg)](https://clojars.org/weareswat/clj-meowallet)

A Clojure Wrapper to MEO WALLET API.

Installation
-----

```clj-meowallet``` is available as a Maven artifact from [Clojars](https://clojars.org/weareswat/clj-meowallet)

With Leiningen/Boot:

```clojure
[clj-meowallet "0.1.0"]
```

### Operations

For now we just have to create new mb ref operation, but in the near future we will support more operations. Every operation has an async interface, so it returns a channel.

* `POST to /api/v2/mb/pay`

```clojure

(ns my-app.core
  (:require [clj-meowallet.core :as meowallet]))

(defn example-of-request-an-mb-ref
  []
  (let [credentials {:meo-wallet-api-key <YOUR_MEO_WALLET_API_KEY>}
        data {:body {:amount 10
              		   :currency "EUR"
                     :expires "2016-05-18T15:59:58+0000"
                     :ext_invoiceid "i00001232"}}]
    (meowallet/generate-mb-ref credentials data)))
```

The `meowallet/generate-mb-ref` should return data in the following format:

```
{:amount 10
 :fee -0.62
 :date 2016-05-17T15:36:25+0000
 :method MB
 :amount_net 9.38
 :requests 1
 :channel WEBSITE
 :type PAYMENT
 :mb {:ref 243323013
      :entity 90426}
 :expires 2016-05-18T15:59:58+0000
 :currency EUR
 :refundable false
 :ext_invoiceid i00001232
 :status PENDING
 :id 33de099a-49f1-42a7-913f-761f2e83b673
 :items []
 :merchant {:id 688892900
            :name <YOUR_ACCOUNT_NAME>
            :email <YOUR_ACCOUNT_EMAIL>}}

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
