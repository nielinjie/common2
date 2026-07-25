package xyz.nietongxue.common.testing

import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer

/**
 *
 * https://www.mock-server.com
 *
 * new MockServerClient("localhost", 1080)
 *     .when(
 *         request()
 *             .withMethod("POST")
 *             .withPath("/login")
 *             .withBody("{username: 'foo', password: 'bar'}")
 *     )
 *     .respond(
 *         response()
 *             .withStatusCode(302)
 *             .withCookie(
 *                 "sessionId", "2By8LOhBmaW5nZXJwcmludCIlMDAzMW"
 *             )
 *             .withHeader(
 *                 "Location", "https://www.mock-server.com"
 *             )
 *     );
 */

class MockServer(val port: Int) {
    var server: ClientAndServer? = null
    fun start(fn: (ClientAndServer) -> Unit) {
        server = startClientAndServer(port)?.apply(fn)
    }

    fun stop() {
        server?.stop()
    }

}