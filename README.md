# quarkus-websockets: endpoint callbacks fail with dispatch-to-worker on main

With `quarkus.websocket.dispatch-to-worker=true`, the callbacks of a
`quarkus-websockets` server endpoint never run on Quarkus main. The client
connects, but the server never sends a reply.

The project has only the `quarkus-websockets` extension, one
`@ServerEndpoint` that replies from `@OnOpen` and `@OnMessage`, and a test
that connects to it. `application.properties` sets
`quarkus.websocket.dispatch-to-worker=true`.

    mvn verify                             # main (999-SNAPSHOT): fails
    mvn verify -Dquarkus.version=3.33.0    # passes

On main, `EchoEndpointTest` fails with
`expected: <welcome> but was: <null>`, and the server logs:

    ERROR [io.undertow.websockets.request] (vert.x-eventloop-thread-0)
    UT026013: Unhandled error in annotated endpoint org.acme.EchoEndpoint@...:
    java.lang.NullPointerException: Cannot invoke
    "java.util.concurrent.Executor.execute(java.lang.Runnable)" because
    "executor" is null
        at io.undertow.websockets.ServerWebSocketContainer.invokeEndpointMethod(ServerWebSocketContainer.java:515)
        at io.undertow.websockets.annotated.AnnotatedEndpoint.invokeMethod(AnnotatedEndpoint.java:151)
        at io.undertow.websockets.annotated.AnnotatedEndpoint.onOpen(AnnotatedEndpoint.java:101)

Without the property (the default, `false`) the test passes on main too,
because the callbacks then run on the event loop and do not need the worker
executor.

`EchoEndpointIT` runs the same checks against the packaged application
(`@QuarkusIntegrationTest`), which fails the same way on main.

Tested with the `maven-repo-main-2026-10-06` build from
quarkusio/quarkus-ecosystem-ci.
