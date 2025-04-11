package com.practicum.playlistmaker.newplaylist.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreationplaylistBinding
import com.practicum.playlistmaker.newplaylist.ui.view_model.CreationPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.File
import java.io.FileOutputStream

class CreationPlaylistFragment: Fragment() {

    private lateinit var binding: FragmentCreationplaylistBinding

    private lateinit var nameTextWatcher: TextWatcher
    private lateinit var descriptionTextWatcher: TextWatcher

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private val viewModel by viewModel<CreationPlaylistViewModel> {
        parametersOf(requireArguments().getLong(ARGS_PLAYLIST_ID))
    }

    private var isPhotoSelected = false
    private var isNameEntered = false
    private var isDescriptionEntered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreationplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setTextWatchers()
        setDialog()

        viewModel.observeState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.ivCover.setImageURI(uri)
                isPhotoSelected = true
                saveImageToPrivateStore(uri)
            } else {
                Toast.makeText(requireContext(), "Фото не выбрано", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnBack.setOnClickListener {
            viewModel.closeFragment()
        }

        binding.btnCreatePlaylist.setOnClickListener {
            viewModel.savePlaylist()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.closeFragment()
            }
        })
    }

    private fun saveImageToPrivateStore(uri: Uri) {
        val filePath = File(requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist_album")

        if (!filePath.exists()) {
            filePath.mkdirs()
        }

        val file = File(filePath, "cover_${System.currentTimeMillis()}.jpg")

        val inputStream = requireActivity().contentResolver.openInputStream(uri)

        val outputStream = FileOutputStream(file)

        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        viewModel.setPathToPhoto(Uri.fromFile(file))
    }

    private fun setTextWatchers() {
        nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    viewModel.setNameValue(s.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        }

        descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    viewModel.setDescriptionValue(s.toString())
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        }

        binding.name.addTextChangedListener(nameTextWatcher)
        binding.description.addTextChangedListener(descriptionTextWatcher)
    }

    private fun setDialog() {
        confirmDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.app_title_dialog))
            .setMessage(getString(R.string.app_message_dialog))
            .setNeutralButton(getString(R.string.app_cancel_dialog)) { _, _ -> }
            .setPositiveButton(getString(R.string.app_finish_dialog)) { _, _ ->
                findNavController().popBackStack()
            }

    }

    private fun renderState(state: PlaylistUiState) {
        when(state) {
            PlaylistUiState.NewPlaylistMode -> binding.btnCreatePlaylist.isEnabled = false
            is PlaylistUiState.EditPlaylistMode -> {
                binding.btnCreatePlaylist.isEnabled = true
                binding.ivCover.setImageURI(Uri.parse(state.playlist.pathToCover))
                binding.name.setText(state.playlist.name)
                binding.description.setText(state.playlist.description)
            }
            is PlaylistUiState.SaveButtonEnabled -> {
                binding.btnCreatePlaylist.isEnabled = state.isEnabled
            }
            is PlaylistUiState.CloseWithConfirmation -> {
                if (state.isShowDialog) {
                    confirmDialog.show()
                } else {
                    findNavController().popBackStack()
                }
            }
            PlaylistUiState.SavingPlaylist -> findNavController().popBackStack()
        }
    }

    companion object {

        private const val ARGS_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)

    }

}