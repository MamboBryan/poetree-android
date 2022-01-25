package com.mambo.core

import com.mambo.data.models.Topic

interface OnTopicClickListener {
    fun onTopicClicked(topic: Topic)
}