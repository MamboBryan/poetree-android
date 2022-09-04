package com.mambo.poetree.navigation

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mambo.data.models.Poem
import com.mambo.data.models.User
import com.mambo.features.home.FeedActions
import com.mambo.poetree.NavigationMainDirections
import com.mambo.poetree.R
import com.mambobryan.poetree.poem.PoemActions
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class Navigator @Inject constructor(
    private val controller: NavController
) : FeedActions, PoemActions {

    @Module
    @InstallIn(ActivityComponent::class)
    object NavControllerModule {

        @Provides
        fun providerNavController(activity: FragmentActivity): NavController {
            return NavHostFragment.findNavController(
                activity.supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
            )
        }

    }

    @Module
    @InstallIn(ActivityComponent::class)
    abstract class FeedModule {
        @Binds
        abstract fun feeds(navigator: Navigator): FeedActions
    }

    override fun navigateToPoem(poem: Poem) {
        controller.navigate(NavigationMainDirections.fromFeedsToPoem(poem))
    }

    override fun navigateToProfile() {
        controller.navigate(NavigationMainDirections.fromFeedsToProfile())
    }

    override fun navigateToSettings() {
        controller.navigate(NavigationMainDirections.fromFeedsToSettings())
    }

    override fun navigateToCompose() {
        controller.navigate(NavigationMainDirections.toCompose(poem = null))
    }

    @Module
    @InstallIn(ActivityComponent::class)
    abstract class PoemModule {
        @Binds
        abstract fun poemActions(navigator: Navigator): PoemActions
    }

    override fun navigateToComments(poem: Poem) {
        controller.navigate(NavigationMainDirections.fromPoemToComments(poem = poem))
    }

    override fun navigateToCompose(poem: Poem) {
        controller.navigate(NavigationMainDirections.fromPoemToCompose(poem = poem))
    }

    override fun navigateToArtist(user: User) {
        controller.navigate(NavigationMainDirections.fromPoemToArtist(artist = user))
    }

}