package org.acme;

import io.quarkus.test.junit.QuarkusIntegrationTest;

// Same checks against the packaged application, started in its own process
@QuarkusIntegrationTest
class EchoEndpointIT extends EchoEndpointTest {
}
