package cz.sazel.android.noserverwebrtcandroid.settings

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import cz.sazel.android.noserverwebrtcandroid.MainViewModel
import cz.sazel.android.noserverwebrtcandroid.R
import cz.sazel.android.noserverwebrtcandroid.databinding.DialogTurnSettingsBinding

/**
 * Lets the user enter the TURN server to relay the connection through, prefilled with what is stored.
 */
class TurnSettingsDialogFragment : DialogFragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogTurnSettingsBinding.inflate(layoutInflater)
        viewModel.turnSettings.value.let {
            binding.edTurnHost.setText(it.host)
            binding.edTurnUsername.setText(it.username)
            binding.edTurnPassword.setText(it.password)
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.turn_settings)
            .setView(binding.root)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                viewModel.saveTurnSettings(
                    TurnSettings(
                        host = binding.edTurnHost.text.toString().trim(),
                        username = binding.edTurnUsername.text.toString().trim(),
                        password = binding.edTurnPassword.text.toString(),
                    )
                )
            }
            .setNeutralButton(R.string.turn_clear) { _, _ -> viewModel.saveTurnSettings(TurnSettings.NONE) }
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    companion object {

        const val TAG = "turn_settings"
    }
}
