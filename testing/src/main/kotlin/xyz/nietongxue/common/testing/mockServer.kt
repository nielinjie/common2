package xyz.nietongxue.common.testing

import org.mockserver.integration.ClientAndServer
import org.mockserver.integration.ClientAndServer.startClientAndServer


class MockServer(val port: Int) {
    var server: ClientAndServer? = null
    fun start(fn: (ClientAndServer) -> Unit) {
        server = startClientAndServer(port)?.apply(fn)
    }

    fun stop() {
        server?.stop()
    }

}