package com.mambo.poetree.navigation

import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mambo.bookmarks.BookmarkActions
import com.mambo.compose.ComposeActions
import com.mambo.data.models.Poem
import com.mambo.data.models.Topic
import com.mambo.data.models.User
import com.mambo.explore.ExploreActions
import com.mambo.features.home.FeedActions
import com.mambo.library.LibraryActions
import com.mambo.poetree.NavigationMainDirections
import com.mambo.poetree.R
import com.mambobryan.features.artist.ArtistActions
import com.mambobryan.features.loading.LoadingActions
import com.mambobryan.features.search.SearchActions
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
) : FeedActions, PoemActions, BookmarkActions, ComposeActions, LoadingActions, LibraryActions,
    ExploreActions, SearchActions, ArtistActions {

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
        controller.navigate(NavigationMainDirections.toPoem(poem))
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
        controller.navigate(NavigationMainDirections.toArtist(artist = user))
    }

    @Module
    @InstallIn(ActivityComponent::class)
    abstract class BookmarksModule {
        @Binds
        abstract fun bookmarksActions(navigator: Navigator): BookmarkActions
    }

    @Module
    @InstallIn(ActivityComponent::class)
    abstract class ComposeModule {
        @Binds
        abstract fun composeActions(navigator: Navigator): ComposeActions
    }

    @Module
    @InstallIn(ActivityComponent::class)
    abstract class LoadingModule {
        @Binds
        abstract fun loadingActions(navigator: Navigator): LoadingActions
    }

    override fun navigateToFeeds() {
        controller.navigate(NavigationMainDirections.toFeeds())
    }

    @Module
    @InstallIn(ActivityComponent::class)
    abstract class LibraryModule {
        @Binds
        abstract fun libraryActions(navigator: Navigator): LibraryActions
    }

    @Module
    @InstallIn(ActivityComponent::class)
    abstract class ExploreModule {
        @Binds
        abstract fun exploreActions(navigator: Navigator): ExploreActions
    }

    override fun navigateToSearch(topic: Topic?) {
        controller.navigate(NavigationMainDirections.toSearch(topic))
    }

    override fun navigateToTopic(topic: Topic?) {
        controller.navigate(NavigationMainDirections.toTopic(topic))
    }

    @Module
    @InstallIn(ActivityComponent::class)
    abstract class SearchModule {
        @Binds
        abstract fun searchActions(navigator: Navigator): SearchActions
    }

    @Module
    @InstallIn(ActivityComponent::class)
    abstract class ArtistModule {
        @Binds
        abstract fun artistActions(navigator: Navigator): ArtistActions
    }

}