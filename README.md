# quarkus-websockets: endpoint callbacks fail with dispatch-to-worker on main

With `quarkus.websocket.dispatch-to-worker=true`, the callbacks of a
`quarkus-websockets` server endpoint never run on Quarkus main. The client
connects, but the server never sends a reply.

The project has only the `quarkus-websockets` extension and two endpoints
that send `welcome` when a client connects and echo every message:

- `EchoEndpoint`, annotated with `@ServerEndpoint("/echo")`
- `ProgrammaticEchoEndpoint`, an `Endpoint` subclass at
  `/programmatic-echo`, registered by `EchoApplicationConfig`

`application.properties` sets `quarkus.websocket.dispatch-to-worker=true`.

The `999-SNAPSHOT` of Quarkus main is not in a Maven repository. Install
the latest daily snapshot into `~/.m2/repository` first (this needs the
GitHub CLI, `gh`):

    curl -sL https://raw.githubusercontent.com/quarkusio/quarkus-ecosystem-ci/main/setup-quarkus | bash

Then run the tests. `-nsu` keeps Maven from looking for the snapshot in
remote repositories:

    mvn -nsu clean verify                           # main (999-SNAPSHOT): fails
    mvn -nsu clean verify -Dquarkus.version=3.33.0  # passes

On main, both tests in `EchoEndpointTest` fail:

- `annotatedEndpointCallbacksRun`: `expected: <welcome> but was: <null>`.
  The annotated `@OnOpen` never runs.
- `programmaticEndpointCallbacksRun`: `welcome` arrives, because the
  programmatic `onOpen` sends it directly, but then
  `expected: <echo: hello> but was: <null>`. The incoming message never
  reaches the message handler.

The server logs for the annotated endpoint:

    ERROR [io.undertow.websockets.request] (vert.x-eventloop-thread-0)
    UT026013: Unhandled error in annotated endpoint org.acme.EchoEndpoint@...:
    java.lang.NullPointerException: Cannot invoke
    "java.util.concurrent.Executor.execute(java.lang.Runnable)" because
    "executor" is null
        at io.undertow.websockets.ServerWebSocketContainer.invokeEndpointMethod(ServerWebSocketContainer.java:515)
        at io.undertow.websockets.annotated.AnnotatedEndpoint.invokeMethod(AnnotatedEndpoint.java:151)
        at io.undertow.websockets.annotated.AnnotatedEndpoint.onOpen(AnnotatedEndpoint.java:101)

and for the programmatic endpoint:

    An exception was thrown by
    io.undertow.websockets.UndertowSession$1.operationComplete():
    java.lang.NullPointerException: Cannot invoke
    "java.util.concurrent.Executor.execute(java.lang.Runnable)" because
    "executor" is null

Without the property (the default, `false`) both tests pass on main too,
because the callbacks then run on the event loop and do not need the worker
executor.

`EchoEndpointIT` runs the same checks against the packaged application
(`@QuarkusIntegrationTest`), which fails the same way on main.

Tested with the `maven-repo-main-2026-10-06` build from
quarkusio/quarkus-ecosystem-ci.
