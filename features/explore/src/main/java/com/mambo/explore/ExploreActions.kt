package com.mambo.explore

import com.mambo.data.models.Topic

interface ExploreActions {

    fun navigateToSearch(topic: Topic? = null)

    fun navigateToTopic(topic: Topic? = null)

}