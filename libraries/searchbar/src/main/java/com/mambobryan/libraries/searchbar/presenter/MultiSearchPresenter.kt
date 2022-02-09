package com.mambobryan.libraries.searchbar.presenter

import android.animation.LayoutTransition
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.mambobryan.libraries.searchbar.R
import com.mambobryan.libraries.searchbar.extensions.setStyle
import com.mambobryan.libraries.searchbar.model.MultiSearchConfiguration
import kotlinx.android.synthetic.main.search_item.view.*

internal class MultiSearchPresenter(private val context: Context) {

    private lateinit var multiSearchConfiguration: MultiSearchConfiguration

    internal var isInSearchMode = false
    internal val sizeRemoveIcon = context.resources.getDimensionPixelSize(
        R.dimen.material_padding
    )
    internal val defaultPadding = context.resources.getDimensionPixelSize(
        R.dimen.material_padding
    )

    /**
     * Creates configuration from styles
     *
     * @param typedArray style loaded array
     */
    fun createConfiguration(typedArray: TypedArray) {
        val searchSelectionIndicator = typedArray.getResourceId(
            R.styleable.MultiSearch_searchSelectionIndicator,
            R.drawable.indicator_line
        )
        val searchTextAppearance = typedArray.getResourceId(
            R.styleable.MultiSearch_searchTextAppearance,
            0
        )
        val searchIcon = typedArray.getResourceId(
            R.styleable.MultiSearch_searchIcon,
            R.drawable.ic_round_search_24dp
        )
        val searchRemoveIcon = typedArray.getResourceId(
            R.styleable.MultiSearch_searchRemoveIcon,
            R.drawable.ic_round_clear
        )
        val searchAnimationDuration = typedArray.getInt(
            R.styleable.MultiSearch_searchAnimationDuration,
            500
        )
        typedArray.recycle()

        multiSearchConfiguration = MultiSearchConfiguration(
            searchSelectionIndicator = searchSelectionIndicator,
            searchTextAppearance = searchTextAppearance,
            searchIcon = searchIcon,
            searchRemoveIcon = searchRemoveIcon,
            searchAnimationDuration = searchAnimationDuration
        )
    }

    /**
     * Configure search image view icon
     *
     * @param multiSearchActionIcon view to manipulate
     */
    fun configureSearchAction(
        multiSearchActionIcon: AppCompatImageView?
    ) {
        if (multiSearchActionIcon != null) {
            val searchIcon = ContextCompat.getDrawable(
                context,
                multiSearchConfiguration.searchIcon
            )

            multiSearchActionIcon.setImageDrawable(searchIcon)
        }
    }

    /**
     * Configure search indicator icon
     *
     * @param viewIndicator view to manipulate
     */
    fun configureSelectionIndicator(viewIndicator: View?) {
        if (viewIndicator != null) {
            val indicatorDrawable = ContextCompat.getDrawable(
                context,
                multiSearchConfiguration.searchSelectionIndicator
            )

            viewIndicator.background = indicatorDrawable
        }
    }

    /**
     * Configure search item remove icon
     *
     * @param searchItemAction view to manipulate
     */
    private fun configureSearchItemAction(searchItemAction: AppCompatImageView?) {
        if (searchItemAction != null) {
            val actionDrawable = ContextCompat.getDrawable(
                context,
                multiSearchConfiguration.searchRemoveIcon
            )

            searchItemAction.setImageDrawable(actionDrawable)
        }
    }

    /**
     * Provides transition options
     */
    fun getContainerTransition(): LayoutTransition? {
        return LayoutTransition().apply {
            disableTransitionType(LayoutTransition.APPEARING)
            disableTransitionType(LayoutTransition.CHANGE_APPEARING)
        }
    }

    /**
     * Inflates search item view
     */
    fun createNewSearchItem(viewGroup: ViewGroup, searchItemWidth: Float): View {
        val searchItem = LayoutInflater.from(context).inflate(
            R.layout.search_item,
            viewGroup,
            false
        )

        with (searchItem) {
            configureSearchItemAction(searchItem.searchTermRemoveIcon)
            val editText = searchItem.searchTermEditText
            editText.setStyle(context, multiSearchConfiguration.searchTextAppearance)
            layoutParams = LinearLayout.LayoutParams(searchItemWidth.toInt(), WRAP_CONTENT)
        }

        return searchItem
    }

    /**
     * Configures animators duration and interpolator
     */
    fun configureValueAnimator(valueAnimator: ValueAnimator) {
        valueAnimator.duration = multiSearchConfiguration.searchAnimationDuration.toLong()
        valueAnimator.interpolator = LinearOutSlowInInterpolator()
    }
}