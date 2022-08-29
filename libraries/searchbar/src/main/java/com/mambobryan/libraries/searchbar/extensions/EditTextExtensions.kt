package com.mambobryan.libraries.searchbar.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Typeface
import android.text.InputFilter
import android.util.TypedValue
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StyleRes

fun EditText.onSearchAction(filter: Boolean = true, onSearchClicked: () -> Unit) {
    setOnEditorActionListener(
        TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (filter)
                    onSearchClicked()

                return@OnEditorActionListener true
            }
            false
    })
}

/**
 * Better solution from TextView.setTextAppearance(context,resId) for EditText
 * @see TextView.setTextAppearance
 *
 * This function sets many attributes to EditText. These styles are as follows;
 * android.R.attr.layout_width
 * android.R.attr.layout_height
 * android.R.attr.focusable
 * android.R.attr.focusableInTouchMode
 * android.R.attr.enabled
 * android.R.attr.hint
 * android.R.attr.imeOptions
 * android.R.attr.maxLength
 * android.R.attr.textSize
 * android.R.attr.inputType
 * android.R.attr.textColorHint
 * android.R.attr.textColor
 * android.R.attr.textAllCaps
 * android.R.attr.padding
 * android.R.attr.paddingTop
 * android.R.attr.paddingBottom
 * android.R.attr.paddingRight
 * android.R.attr.paddingLeft
 * android.R.attr.textColorHighlight
 * android.R.attr.text
 * android.R.attr.textStyle
 * android.R.attr.fontFamily
 *
 * @param context Context parameter used for get typed array with context.obtainStyledAttributes
 * @param resId ResId parameter used for get the style attributes from resource
 *
 * @author mertceyhan
 */
fun EditText.setStyle(context: Context, @StyleRes resId: Int) {
    if (resId == 0)
        return

    val attributes = EditTextAttributes.getAttributesList()
    val typedArray = context.obtainStyledAttributes(resId, attributes)
    readStyle(typedArray, attributes)
}

private fun EditText.readStyle(typedArray: TypedArray, attributes: IntArray) {
    val editTextAttributes = EditTextAttributes()
    val typedArraySize = typedArray.length().minus(1)

    (0..typedArraySize).forEach { index ->
        when (attributes[index]) {
            EditTextAttributes.attrLayoutWidth -> {
                editTextAttributes.layoutWidth = typedArray.getLayoutDimension(index, -1)
            }
            EditTextAttributes.attrLayoutHeight -> {
                editTextAttributes.layoutHeight = typedArray.getLayoutDimension(index, -1)
            }
            EditTextAttributes.attrFocusable -> {
                editTextAttributes.focusable = typedArray.getBoolean(index, true)
            }
            EditTextAttributes.attrFocusableInTouchMode -> {
                editTextAttributes.focusableInTouchMode = typedArray.getBoolean(index, true)
            }
            EditTextAttributes.attrEnabled -> {
                editTextAttributes.enabled = typedArray.getBoolean(index, true)
            }
            EditTextAttributes.attrHint -> {
                editTextAttributes.hint = typedArray.getString(index)
            }
            EditTextAttributes.attrImeOptions -> {
                editTextAttributes.imeOptions = typedArray.getInt(index, -1)
            }
            EditTextAttributes.attrMaxLength -> {
                editTextAttributes.maxLength = typedArray.getInt(index, -1)
            }
            EditTextAttributes.attrTextSize -> {
                editTextAttributes.textSize = typedArray.getDimensionPixelSize(index, -1)
            }
            EditTextAttributes.attrInputType -> {
                editTextAttributes.inputType = typedArray.getInt(index, EditorInfo.TYPE_TEXT_FLAG_CAP_SENTENCES)
            }
            EditTextAttributes.attrTextColorHint -> {
                editTextAttributes.textColorHint = typedArray.getColorStateList(index)
            }
            EditTextAttributes.attrTextColor -> {
                editTextAttributes.textColor = typedArray.getColorStateList(index)
            }
            EditTextAttributes.attrTextColorLink -> {
                editTextAttributes.textColorLink = typedArray.getColorStateList(index)

            }
            EditTextAttributes.attrTextColorHighlight -> {
                editTextAttributes.textColorHighlight = typedArray.getColor(index, -1)
            }
            EditTextAttributes.attrTextStyle -> {
                editTextAttributes.textStyle = typedArray.getInt(index, Typeface.NORMAL)
            }
            EditTextAttributes.attrFontFamily -> {
                editTextAttributes.fontFamily = typedArray.getString(index) ?: ""
            }
            EditTextAttributes.attrAllCaps -> {
                editTextAttributes.textAllCaps = typedArray.getBoolean(index, false)
            }
            EditTextAttributes.attrPadding -> {
                editTextAttributes.padding = typedArray.getLayoutDimension(index, -1)
            }
            EditTextAttributes.attrPaddingTop -> {
                editTextAttributes.paddingTop = typedArray.getLayoutDimension(index, 0)

            }
            EditTextAttributes.attrPaddingBottom -> {
                editTextAttributes.paddingBottom = typedArray.getLayoutDimension(index, 0)

            }
            EditTextAttributes.attrPaddingLeft -> {
                editTextAttributes.paddingLeft = typedArray.getLayoutDimension(index, 0)

            }
            EditTextAttributes.attrPaddingRight -> {
                editTextAttributes.paddingRight = typedArray.getLayoutDimension(index, 0)

            }
        }
    }

    applyStyle(editTextAttributes)
    typedArray.recycle()
}

private fun EditText.applyStyle(editTextAttributes: EditTextAttributes) {
    val editTextFilters: ArrayList<InputFilter> = ArrayList()

    if (editTextAttributes.layoutWidth != -1) {
        layoutParams.width = editTextAttributes.layoutWidth
    }
    if (editTextAttributes.layoutHeight != -1) {
        layoutParams.height = editTextAttributes.layoutHeight
    }
    if (editTextAttributes.focusable.not()) {
        isFocusable = editTextAttributes.focusable
    }
    if (editTextAttributes.enabled.not()) {
        isEnabled = editTextAttributes.enabled
    }
    if (editTextAttributes.imeOptions != -1) {
        imeOptions = editTextAttributes.imeOptions
    }
    if (editTextAttributes.maxLength != -1) {
        editTextFilters.add(InputFilter.LengthFilter(editTextAttributes.maxLength))
    }
    if (editTextAttributes.textSize != -1) {
        setTextSize(TypedValue.COMPLEX_UNIT_PX, editTextAttributes.textSize.toFloat())
    }
    if (editTextAttributes.inputType != -1) {
        inputType = editTextAttributes.inputType
    }
    if (editTextAttributes.textAllCaps) {
        editTextFilters.add(InputFilter.AllCaps())
    }
    if (editTextAttributes.textColorHighlight != -1) {
        highlightColor = editTextAttributes.textColorHighlight
    }
    if (editTextAttributes.padding != -1) {
        setPaddingRelative(
            editTextAttributes.padding,
            editTextAttributes.padding,
            editTextAttributes.padding,
            editTextAttributes.padding
        )
    } else {
        setPaddingRelative(
            editTextAttributes.paddingLeft,
            editTextAttributes.paddingTop,
            editTextAttributes.paddingRight,
            editTextAttributes.paddingBottom
        )
    }
    if (editTextAttributes.textColorHint != null) {
        setHintTextColor(editTextAttributes.textColorHint)
    }
    if (editTextAttributes.textColor != null) {
        setTextColor(editTextAttributes.textColor)
    }
    if (editTextAttributes.textColorLink != null) {
        setLinkTextColor(editTextAttributes.textColorLink)
    }
    if (editTextAttributes.focusableInTouchMode.not()) {
        isFocusableInTouchMode = editTextAttributes.focusableInTouchMode
    }
    if (editTextAttributes.hint.isNullOrBlank().not()) {
        hint = editTextAttributes.hint
    }
    if (editTextFilters.isNullOrEmpty().not()) {
        filters = editTextFilters.toTypedArray()
    }
    val customTypeface = Typeface.create(editTextAttributes.fontFamily, editTextAttributes.textStyle)
    typeface = customTypeface
}

private class EditTextAttributes {

    var padding = -1
    var textSize = -1
    var maxLength = -1
    var inputType = -1
    var imeOptions = -1
    var layoutWidth = -1
    var layoutHeight = -1
    var textColorHighlight = -1
    var paddingTop = 0
    var paddingLeft = 0
    var paddingRight = 0
    var paddingBottom = 0
    var textStyle = Typeface.NORMAL
    var fontFamily = ""
    var enabled = true
    var focusable = true
    var textAllCaps = false
    var focusableInTouchMode = true
    var hint: String? = null
    var textColorHint: ColorStateList? = null
    var textColor: ColorStateList? = null
    var textColorLink: ColorStateList? = null

    companion object {
        const val attrLayoutWidth = android.R.attr.layout_width
        const val attrLayoutHeight = android.R.attr.layout_height
        const val attrFocusable = android.R.attr.focusable
        const val attrFocusableInTouchMode = android.R.attr.focusableInTouchMode
        const val attrEnabled = android.R.attr.enabled
        const val attrHint = android.R.attr.hint
        const val attrImeOptions = android.R.attr.imeOptions
        const val attrMaxLength = android.R.attr.maxLength
        const val attrTextSize = android.R.attr.textSize
        const val attrInputType = android.R.attr.inputType
        const val attrTextColorHint = android.R.attr.textColorHint
        const val attrTextColor = android.R.attr.textColor
        const val attrAllCaps = android.R.attr.textAllCaps
        const val attrPadding = android.R.attr.padding
        const val attrPaddingTop = android.R.attr.paddingTop
        const val attrPaddingBottom = android.R.attr.paddingBottom
        const val attrPaddingRight = android.R.attr.paddingRight
        const val attrPaddingLeft = android.R.attr.paddingLeft
        const val attrTextColorHighlight = android.R.attr.textColorHighlight
        const val attrTextColorLink = android.R.attr.text
        const val attrTextStyle = android.R.attr.textStyle
        const val attrFontFamily = android.R.attr.fontFamily

        fun getAttributesList(): IntArray = intArrayOf(
            attrLayoutWidth,
            attrLayoutHeight,
            attrFocusable,
            attrFocusableInTouchMode,
            attrEnabled,
            attrPadding,
            attrHint,
            attrPaddingTop,
            attrPaddingBottom,
            attrPaddingRight,
            attrPaddingLeft,
            attrImeOptions,
            attrMaxLength,
            attrTextSize,
            attrTextStyle,
            attrFontFamily,
            attrInputType,
            attrTextColorHint,
            attrTextColorHighlight,
            attrTextColor,
            attrTextColorLink,
            attrAllCaps
        ).apply { sort() }
    }
}