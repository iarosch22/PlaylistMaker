package com.practicum.playlistmaker.creationplaylist.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creationplaylist.domain.models.Playlist
import com.practicum.playlistmaker.databinding.FragmentCreationplaylistBinding
import com.practicum.playlistmaker.creationplaylist.ui.view_model.CreationPlaylistViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

        setDialog()

        viewModel.observeState().observe(viewLifecycleOwner) {
            renderState(it)
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                setImageCover(uri)
                isPhotoSelected = true
                viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    saveImageToPrivateStore(uri)
                }

            } else {
                Toast.makeText(requireContext(),
                    getString(R.string.app_no_photo_selected), Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnBack.setOnClickListener {
            Log.d("BACK_TAG", "Back pressed triggered in top")
            viewModel.closeFragment()
        }

        binding.btnCreatePlaylist.setOnClickListener {
            viewModel.savePlaylist()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("BACK_TAG", "Back pressed triggered in bottom")
                viewModel.closeFragment()
            }
        })
    }

    private suspend fun saveImageToPrivateStore(uri: Uri) {
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

        withContext(Dispatchers.Main) {
            viewModel.setPathToPhoto(Uri.fromFile(file))
        }
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

        binding.tvNamePlaylist.addTextChangedListener(nameTextWatcher)
        binding.description.addTextChangedListener(descriptionTextWatcher)
    }

    private fun setEditMode(playlist: Playlist) {
        binding.btnCreatePlaylist.text = getString(R.string.app_btn_save)
        binding.btnCreatePlaylist.isEnabled = true
        setImageCover(playlist.pathToCover.toUri())
        binding.tvNamePlaylist.setText(playlist.name)
        binding.description.setText(playlist.description)
        binding.title.text = getString(R.string.app_edit_playlist_btn)

        setTextWatchers()
    }

    private fun setCreationMode() {
        binding.btnCreatePlaylist.text = getString(R.string.app_btn_add)
        binding.btnCreatePlaylist.isEnabled = false
        binding.title.text = getString(R.string.app_new_playlist_btn)

        setTextWatchers()
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

    private fun renderState(state: CreationPlaylistUiState) {
        when(state) {
            CreationPlaylistUiState.NewCreationPlaylistMode -> {
                setCreationMode()
            }

            is CreationPlaylistUiState.EditCreationPlaylistMode -> {
                setEditMode(state.playlist)
            }
            is CreationPlaylistUiState.SaveButtonEnabled -> {
                binding.btnCreatePlaylist.isEnabled = state.isEnabled
            }
            is CreationPlaylistUiState.CloseWithConfirmation -> {
                if (state.isShowDialog) {
                    confirmDialog.show()
                } else {
                    findNavController().popBackStack()
                }
            }
            is CreationPlaylistUiState.SavingCreationPlaylist -> {
                if (state.isNewPlaylist) {
                    Toast.makeText(requireContext(), "Плейлист ${state.name} создан", Toast.LENGTH_SHORT).show()
                }
                findNavController().popBackStack()
            }
        }
    }

    private fun setImageCover(uri: Uri) {
        val cornersValueDp = 8F
        val cornersValuePx = dpToPx(cornersValueDp, requireContext())

        Glide.with(this)
            .load(uri)
            .placeholder(R.drawable.ic_placeholder_large)
            .transform(
                CenterCrop(),
                RoundedCorners(cornersValuePx)
            )
            .into(binding.ivCover)
    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics).toInt()
    }

    companion object {

        private const val ARGS_PLAYLIST_ID = "playlist_id"

        fun createArgs(playlistId: Long): Bundle =
            bundleOf(ARGS_PLAYLIST_ID to playlistId)

    }

}