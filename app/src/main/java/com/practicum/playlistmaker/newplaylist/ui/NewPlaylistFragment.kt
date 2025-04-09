package com.practicum.playlistmaker.newplaylist.ui

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentNewplaylistBinding
import org.w3c.dom.Text

class NewPlaylistFragment: Fragment() {

    private lateinit var binding: FragmentNewplaylistBinding

    private lateinit var nameTextWatcher: TextWatcher
    private lateinit var descriptionTextWatcher: TextWatcher

    private lateinit var confirmDialog: MaterialAlertDialogBuilder

    private var isPhotoSelected = false
    private var isNameEntered = false
    private var isDescriptionEntered = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewplaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCreatePlaylist.isEnabled = false

        setTextWatchers()
        setDialog()

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.ivCover.setImageURI(uri)
                isPhotoSelected = true
            } else {
                Toast.makeText(requireContext(), "Фото не выбрано", Toast.LENGTH_SHORT).show()
            }
        }

        binding.ivCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnBack.setOnClickListener {
            closeFragment()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                closeFragment()
            }

        })
    }

    private fun setTextWatchers() {
        nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    if (s.isNotEmpty()) {
                        binding.btnCreatePlaylist.isEnabled = true
                        isNameEntered = true
                    } else {
                        binding.btnCreatePlaylist.isEnabled = false
                        isNameEntered = false
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        }

        descriptionTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    isDescriptionEntered = s.isNotEmpty()
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

    private fun closeFragment() {
        if (isPhotoSelected && isNameEntered || isDescriptionEntered) {
            confirmDialog.show()
        } else {
            findNavController().popBackStack()
        }
    }

}