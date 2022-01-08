object BuildModules {

    object Features {
        const val auth = ":features:auth"
        const val home = ":features:home"
        const val explore = ":features:explore"
        const val bookmarks = ":features:bookmarks"
        const val library = ":features:library"
        const val compose = ":features:compose"
        const val comments = ":features:comments"
        const val poem = ":features:poem"
        const val onboarding = ":features:onboarding"
        const val profile = ":features:profile"
        const val account = ":features:account"
        const val updatePassword = ":features:update-password"
        const val search = ":features:search"
        const val artist = ":features:artist"
        const val landing = ":features:landing"
    }

    object Commons {
        const val core = ":modules:core"
        const val local = ":modules:local"
        const val remote = ":modules:remote"
        const val data = ":modules:data"
        const val ui = ":modules:ui"
    }

    object Libraries {
        const val editor = ":libraries:editor"
        const val searchbar = ":libraries:searchbar"
    }

}