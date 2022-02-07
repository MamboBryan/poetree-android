# Home

## Why?

I was inspired  to create Poetree when my friends we're always sharing their poems via Google keep (cause you know google got your back), I decided to create one for them to use and along the way I realized I can open source it for everyone else. I believe that everyone has something to offer in the poetry space around the globe, because there is no good or bad poetree but simply a poem and the users that like it. Poetree is a simple demonstartion of how we can come together and make something amazing and great.

## How?

Poetree is developed with the following:

* Tech-stack
  * [Kotlin](https://kotlinlang.org/) - a cross-platform, statically typed, general-purpose programming language with type inference.
  * [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - perform background operations.
  * [Flow](https://kotlinlang.org/docs/reference/coroutines/flow.html) - handle the stream of data asynchronously that executes sequentially.
  * [Retrofit](https://github.com/square/retrofit) - A type-safe HTTP client for Android.
  * [Chuck](https://github.com/jgilfelt/chuck) - An in-app HTTP inspector for Android OkHttp clients
  * [Jetpack](https://developer.android.com/jetpack)
    * [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) - a dependency injection library for Android.
    * [Room](https://developer.android.com/topic/libraries/architecture/room) - a persistence library provides an abstraction layer over SQLite.
    * [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - is an observable data holder.
    * [Lifecycle](https://developer.android.com/topic/libraries/architecture/lifecycle) - perform action when lifecycle state changes.
    * [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - store and manage UI-related data in a lifecycle conscious way.

* Architecture
  * MVVM
  * MVI

* Gradle
  * [Gradle Kotlin DSL](https://docs.gradle.org/current/userguide/kotlin_dsl.html) - For reference purposes, here's an [article explaining the migration](https://medium.com/@evanschepsiror/migrating-to-kotlin-dsl-4ee0d6d5c977).

* CI/CD
  * Github Actions
  * Buddy

## What next?

My aim is to keep developing and improving Poetree so that users can enjoy it's unique features.

<br>

# Contributing

I'm really excited that you are interested in contributing to Poetree :tada:. Before submitting your contribution, please make sure to take a moment and read through the following.

* [Home](#home)
  * [Why?](#why)
  * [How?](#how)
  * [What next?](#what-next)
* [Contributing](#contributing)
  * [Quick start](#quick-start)
  * [Setting Up](#setting-up)
    * [Step 1 : Generate a Personal Access Token for GitHub](#step-1--generate-a-personal-access-token-for-github)
    * [Step 2: Store your GitHub — Personal Access Token details](#step-2-store-your-github--personal-access-token-details)
    * [Step 3: Create Firebase Project](#step-3-create-firebase-project)
    * [Step 4: Add google-services.json](#step-4-add-google-servicesjson)
    * [Step 5. Build and make changes](#step-5-build-and-make-changes)
  * [Pull Request Guidelines](#pull-request-guidelines)
    * [Where should I start?](#where-should-i-start)

## Quick start

1. Fork the repository.
2. Clone your fork: `git clone git@github.com:<username>/poetree.git`
3. Create to custom branch: `git checkout -b feature/bug/chore`

## Setting Up

### Step 1 : Generate a Personal Access Token for GitHub

* Inside you GitHub account:
* Settings -> Developer Settings -> Personal Access Tokens -> Generate new token
* Make sure you select the following scopes (“ read:packages”) and Generate a token
* After Generating make sure to copy your new personal access token. You cannot see it again! The only option is to generate a new key.

### Step 2: Store your GitHub — Personal Access Token details

* Create a github.properties file within your root Android project
* In case of a public repository make sure you add this file to .gitignore for keep the token private
* Add properties gpr.usr=GITHUB_USERID and gpr.key=PERSONAL_ACCESS_TOKEN
* Replace GITHUB_USERID with personal / organisation Github User ID and PERSONAL_ACCESS_TOKEN with the token generated in #Step 1

### Step 3: Create Firebase Project

* Go to firebase console [here](https://console.firebase.google.com/)
* Click on **create project** and follow the steps
  * add project name and click continue
  * you can enable google analytics or not and click contine
  * choose analytics location, check the box to accept [Google Analytics Terms](https://marketingplatform.google.com/about/analytics/terms/us/) and click create project

### Step 4: Add google-services.json

* Once step 4 is finished click on the **android icon** on the new product page
* register app with the following details
  * Android Package name : com.mambo.poetree
  * (optional) App nickname : Poetree
  * (optional) Debug signing certificate SHA-1 : [how to get debug siging certificate](https://stackoverflow.com/questions/27609442/how-to-get-the-sha-1-fingerprint-certificate-in-android-studio-for-debug-mode)
  * click **register app**
* click **download google-services.json** and locate the downloaded file
* click **next**
* on android studio, click on the project view and copy the google-services.json file in the ./app folder and also ./modules/core folder
* click **continue to console**

### Step 5. Build and make changes

* Make your changes and push your branch.
* Create a PR against `develop` and describe your changes.
  <br>
  YOU'RE READY TO GO

## Pull Request Guidelines

**In *all* Pull Requests:** provide a detailed description of the problem, as Ill as a demonstration with screen recordings and/or screenshots.

Please make sure the following is done before submitting a PR:

* Submit PRs directly to the `develop` branch.
* Reference the related issue in the PR comment.
* All PRs need to pass the **CI** before merged. If it fails, please try to solve the issue(s) and feel free to ask for any help.

If you add new feature:

* Open a suggestion issue first.
* Provide your reasoning on why you want to add this feature.
* Submit your PR.

If you fix a bug:

* If you are resolving a special issue, add the issue number in the PR
  * `fix: #<issue number> <short message>`
  * e.g. `fix: #1 crash on poem share`.
* Provide a detailed description of the bug in your PR and/or link to the issue.

**Rebase your PR:**

If there are conflicts or you want to update your local branch, please do the following:

1. `git fetch upstream`
2. `git rebase upstream/develop`
3. Please [resolve](https://help.github.com/articles/resolving-merge-conflicts-after-a-git-rebase/) all conflicts and force push your feature branch: `git push -f`

### Where should I start?

A good way to start is to find an [issue](https://github.com/MamboBryan/poetree/issues). The `good first issue` issues are good for newcomers.

Other ways to help:

* Documentation
* Translation
* Improve the UI
* Write tests for Poetree
* Share your thoughts! I want to hear about features you think are missing, any bugs you find, and why you :heart: Poetree.
