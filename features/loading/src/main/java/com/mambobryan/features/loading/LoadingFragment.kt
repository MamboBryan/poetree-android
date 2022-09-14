package com.mambobryan.features.loading

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.mambo.core.utils.viewBinding
import com.mambobryan.features.loading.databinding.FragmentLoadingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoadingFragment : Fragment(R.layout.fragment_loading) {

    @Inject
    lateinit var loadingActions: LoadingActions

    private val binding by viewBinding(FragmentLoadingBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: remove loading fragment from the destinations
        loadingActions.navigateToFeeds()

    }

}