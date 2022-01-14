package com.mambobryan.features.landing

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mambobryan.features.landing.databinding.FragmentLandingBinding
import com.mambobryan.navigation.Destinations
import com.mambobryan.navigation.extensions.getDeeplink
import com.mambobryan.navigation.extensions.getNavOptionsPopUpTo
import com.mambobryan.navigation.extensions.navigate
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class LandingFragment : Fragment(R.layout.fragment_landing) {

    private val binding by viewBinding(FragmentLandingBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val openingPoem =
            "What is it that attracts \n" +
                    "That man can't detract \n" +
                    "His own self from being captured \n" +
                    "By the allure of companions \n" +
                    "To form bonds."

        binding.apply {

            tvPoem.text = openingPoem
            tvPoemAuthor.text = "~  ThePoetreePoet  ~"

            btnAuthLogin.setOnClickListener { navigateToAuthentication() }
        }
    }

    private fun navigateToAuthentication() {
        val deeplink = getDeeplink(Destinations.AUTH)
        val optons = getNavOptionsPopUpTo(findNavController().backStack.last.destination.id)
        navigate(deeplink, optons)
    }

}