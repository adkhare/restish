RESTish is a REST like client library for Apache Cassandra. It accepts commands and configurations as JSON input and returns results in JSON depending on the parameters.

The project is built on top of Hector, one of the most well maintained Cassandra clients available. It uses Netty as it's server engine to accept and process connections.

I started work on this client because of the lack of clients available in languages such as C++ and PHP. Even though there are some clients about they're not up to date and are often full of bugs or incomplete. My second reasoning was that, PHP support even at thrift level tends to be buggy. Finally, learning a different client API for each language I use Cassandra in can be a daunting task. As a result, I've decided to dedicate my time and efforts to learning how to use hector and building this language independent client on top of it.

Utilising the benefits/advantages of hector, the RESTish project aims to build a feature rich and complete client API through which you're able to manipulate Cassandra. All of this being done through JSON messages (Plans for XML support in the works).

Essentially the API allows you to pass a command along with associated properties/meta data which formulate an underlying query/predicate and executes them via hector's API.

Suggestions/Comments/Code and/or commiters are always welcome.