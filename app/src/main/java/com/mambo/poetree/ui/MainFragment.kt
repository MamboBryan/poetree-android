package com.mambo.poetree.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mambo.poetree.R
import com.mambo.poetree.data.model.Poem
import com.mambo.poetree.data.model.User
import com.mambo.poetree.databinding.FragmentMainBinding
import com.mambo.poetree.ui.adapter.PoemAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val adapter = PoemAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {

            rvMain.setHasFixedSize(true)
            rvMain.adapter = adapter

        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(
                R.id.action_mainFragment_to_createFragment
            )
        }

        createPoems()
    }

    private fun createPoems() {

        val poems = ArrayList<Poem>()

        poems.add(
            Poem(
                "1", "The monsoon wings", "Blah blah blah blah", Date(),
                User("1", "MamboBryan", "some string")
            )
        )
        poems.add(
            Poem(
                "1", "The monsoon wings", "Blah blah blah blah", Date(),
                User("1", "MamboBryan", "some string")
            )
        )
        poems.add(
            Poem(
                "1", "The monsoon wings", "Blah blah blah blah", Date(),
                User("1", "MamboBryan", "some string")
            )
        )
        poems.add(
            Poem(
                "1", "The monsoon wings", "Blah blah blah blah", Date(),
                User("1", "MamboBryan", "some string")
            )
        )
        poems.add(
            Poem(
                "1", "The monsoon wings", "Blah blah blah blah", Date(),
                User("1", "MamboBryan", "some string")
            )
        )

        adapter.insert(poems)
    }

}