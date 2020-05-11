package com.example.android.navigation

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.navigation.databinding.FragmentTitleBinding

class TitleFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentTitleBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_title, container, false )
        binding.playButton.setOnClickListener({titleFragment ->
            titleFragment.findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment())
        })
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.app_name)
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.aboutFragment) {
            return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

}