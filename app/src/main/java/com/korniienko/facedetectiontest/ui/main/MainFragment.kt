package com.korniienko.facedetectiontest.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.korniienko.facedetectiontest.R
import com.korniienko.facedetectiontest.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.btnAddPerson.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.action_navigation_main_to_navigation_add_person)
        }

        binding.btnRecognizeFace.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment_activity_main).navigate(R.id.action_navigation_main_to_navigation_recognize_face)
        }
        return root
    }

}
