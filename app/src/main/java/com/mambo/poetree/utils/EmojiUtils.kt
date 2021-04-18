package com.mambo.poetree.utils

class EmojiUtils {

    companion object {
        fun getEmojiByUnicode(unicode: Int): String {
            return String(Character.toChars(unicode))
        }
    }

}