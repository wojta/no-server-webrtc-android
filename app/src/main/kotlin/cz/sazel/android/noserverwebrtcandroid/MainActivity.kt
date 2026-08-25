package cz.sazel.android.noserverwebrtcandroid

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import cz.sazel.android.noserverwebrtcandroid.adapters.ConsoleAdapter
import cz.sazel.android.noserverwebrtcandroid.databinding.ActivityMainBinding
import cz.sazel.android.noserverwebrtcandroid.settings.TurnSettingsDialogFragment
import cz.sazel.android.noserverwebrtcandroid.webrtc.ServerlessRTCClient.State
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding
    private lateinit var consoleAdapter: ConsoleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupConsole()
        setupMenu()

        binding.btSubmit.setOnClickListener { submitEnteredText() }
        binding.edEnterArea.setOnEditorActionListener { _, _, _ ->
            submitEnteredText()
            true
        }

        observeViewModel()
    }

    private fun applyWindowInsets() {
        val basePadding = binding.root.paddingLeft
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.updatePadding(
                left = basePadding + bars.left,
                top = basePadding + bars.top,
                right = basePadding + bars.right,
                bottom = basePadding + bars.bottom,
            )
            insets
        }
    }

    private fun setupConsole() {
        consoleAdapter = ConsoleAdapter(emptyList())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context).apply { stackFromEnd = true }
            adapter = consoleAdapter
        }
    }

    private fun setupMenu() = addMenuProvider(object : MenuProvider {

        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) = menuInflater.inflate(R.menu.menu, menu)

        override fun onPrepareMenu(menu: Menu) {
            menu.findItem(R.id.mnuCreateOffer).isVisible = viewModel.state.value == State.WAITING_FOR_OFFER
        }

        override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
            R.id.mnuCreateOffer -> {
                viewModel.makeOffer()
                true
            }

            R.id.mnuReset -> {
                viewModel.reset()
                binding.edEnterArea.setText("")
                true
            }

            R.id.mnuTurnSettings -> {
                TurnSettingsDialogFragment().show(supportFragmentManager, TurnSettingsDialogFragment.TAG)
                true
            }

            else -> false
        }
    })

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.console.lines.collect(::renderConsole) }
                launch { viewModel.state.collect(::renderState) }
                launch { viewModel.errors.collect { Toast.makeText(this@MainActivity, it, Toast.LENGTH_LONG).show() } }
            }
        }
    }

    private fun submitEnteredText() {
        viewModel.submit(binding.edEnterArea.text.toString())
        binding.edEnterArea.setText("")
    }

    private fun renderConsole(lines: List<String>) {
        val alreadyShown = consoleAdapter.itemCount
        consoleAdapter.items = lines
        if (lines.size > alreadyShown) {
            consoleAdapter.notifyItemRangeInserted(alreadyShown, lines.size - alreadyShown)
            binding.recyclerView.scrollToPosition(lines.lastIndex)
        }
    }

    private fun renderState(state: State) {
        binding.apply {
            edEnterArea.isEnabled = true
            progressBar.visibility = GONE
            when (state) {
                State.WAITING_FOR_OFFER -> edEnterArea.hint = getString(R.string.hint_paste_offer)

                State.WAITING_FOR_ANSWER -> edEnterArea.hint = getString(R.string.hint_paste_answer)

                State.CHAT_ESTABLISHED -> edEnterArea.hint = getString(R.string.enter_message)

                State.WAITING_TO_CONNECT, State.CREATING_OFFER, State.CREATING_ANSWER -> {
                    progressBar.visibility = VISIBLE
                    if (BuildConfig.DEBUG) edEnterArea.hint = state.name
                    edEnterArea.isEnabled = false
                }

                State.INITIALIZING, State.CHAT_ENDED -> Unit
            }
        }
        invalidateOptionsMenu()
    }
}
