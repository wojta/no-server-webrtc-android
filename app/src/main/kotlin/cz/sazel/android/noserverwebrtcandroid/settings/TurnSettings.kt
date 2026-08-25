package cz.sazel.android.noserverwebrtcandroid.settings

import org.webrtc.PeerConnection

/**
 * TURN server the user entered, used to relay the connection when a direct one cannot be established.
 */
data class TurnSettings(val host: String, val username: String, val password: String) {

    /** Empty host means the user did not set up any TURN server, so only the public STUN servers are used. */
    val isConfigured = host.isNotBlank()

    /**
     * The scheme is optional in what the user types, so both `turn:1.2.3.4:3478` and `1.2.3.4` are accepted.
     */
    private val url = if (host.startsWith(TURN_SCHEME) || host.startsWith(TURNS_SCHEME)) host else "$TURN_SCHEME$host"

    fun toIceServer(): PeerConnection.IceServer =
        PeerConnection.IceServer.builder(url).setUsername(username).setPassword(password).createIceServer()

    companion object {

        private const val TURN_SCHEME = "turn:"
        private const val TURNS_SCHEME = "turns:"

        val NONE = TurnSettings(host = "", username = "", password = "")
    }
}
