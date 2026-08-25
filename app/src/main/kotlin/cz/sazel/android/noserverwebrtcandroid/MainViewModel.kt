package cz.sazel.android.noserverwebrtcandroid

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import cz.sazel.android.noserverwebrtcandroid.console.ConsoleBuffer
import cz.sazel.android.noserverwebrtcandroid.webrtc.ServerlessRTCClient
import cz.sazel.android.noserverwebrtcandroid.webrtc.ServerlessRTCClient.State
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow

/**
 * Owns the WebRTC client and the console content, so both survive configuration changes
 * and the connection is torn down only when the screen is really gone.
 */
class MainViewModel(application: Application) : AndroidViewModel(application), ServerlessRTCClient.IStateChangeListener {

    /** Console the client prints into, rendered by the activity. */
    val console = ConsoleBuffer(application)

    private val _state = MutableStateFlow(State.INITIALIZING)

    /** Current state of the connection. */
    val state = _state.asStateFlow()

    private val initErrors = Channel<String>(Channel.BUFFERED)

    /** Errors that cannot be shown in the console, delivered once. */
    val errors = initErrors.receiveAsFlow()

    private val client = ServerlessRTCClient(console, application, this)

    init {
        try {
            client.init()
        } catch (e: Exception) {
            initErrors.trySend(e.message ?: e.toString())
            e.printStackTrace()
        }
    }

    override fun onStateChanged(state: State) {
        _state.value = state
        if (state == State.INITIALIZING || state == State.CHAT_ENDED) {
            client.waitForOffer()
        }
    }

    fun makeOffer() = client.makeOffer()

    /**
     * Interprets the text entered by the user according to the state the connection is in.
     */
    fun submit(text: String) {
        val message = text.trim()
        when (client.state) {
            State.WAITING_FOR_OFFER -> client.processOffer(message)

            State.WAITING_FOR_ANSWER -> client.processAnswer(message)

            State.CHAT_ESTABLISHED -> if (message.isNotBlank()) {
                client.sendMessage(message)
                console.printf("&gt;$message")
            }

            else -> if (message.isNotBlank()) console.printf(message)
        }
    }

    override fun onCleared() {
        client.destroy()
        super.onCleared()
    }
}
