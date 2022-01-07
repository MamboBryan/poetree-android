package com.mambobryan.libraries.searchbar.usecase

import android.animation.ValueAnimator
import android.view.View
import androidx.core.animation.doOnEnd
import com.mambobryan.libraries.searchbar.extensions.afterMeasured
import com.mambobryan.libraries.searchbar.model.MultiSearchChangeListener
import com.mambobryan.libraries.searchbar.presenter.MultiSearchPresenter
import com.mambobryan.libraries.searchbar.usecase.contract.SearchUseCase
import com.mambobryan.libraries.searchbar.util.KeyboardUtility
import kotlinx.android.synthetic.main.search_item.view.*
import kotlinx.android.synthetic.main.search_view_container.view.*

internal class MultiSearchUseCase(
    private val presenter: MultiSearchPresenter,
    private val multiSearchChangeListener: MultiSearchChangeListener?,
    private val multiSearchContainer: View
) : SearchUseCase {

    internal var selectedSearchItemTab: View? = null

    private val searchEnterScrollAnimation by lazy(LazyThreadSafetyMode.NONE) {
        ValueAnimator.ofInt()
            .apply {
                presenter.configureValueAnimator(
                    this
                )
                addUpdateListener {
                    multiSearchContainer.horizontalScrollView
                        .smoothScrollTo(it.animatedValue as Int, 0)
                }

                doOnEnd { _ ->
                    selectedSearchItemTab?.let {
                        it.searchTermEditText.requestFocus()
                        KeyboardUtility.showKeyboard(it.context)
                    }
                }
            }
    }

    private val searchCompleteCollapseAnimator by lazy(LazyThreadSafetyMode.NONE) {
        ValueAnimator.ofInt().apply {
            presenter.configureValueAnimator(
                this
            )
            addUpdateListener { valueAnimator ->
                selectedSearchItemTab?.let {
                    val newViewLayoutParams = it.layoutParams
                    newViewLayoutParams.width = valueAnimator.animatedValue as Int
                    it.layoutParams = newViewLayoutParams
                }
            }
        }
    }

    private val firstSearchTranslateAnimator by lazy(LazyThreadSafetyMode.NONE) {
        ValueAnimator.ofFloat().apply {
            presenter.configureValueAnimator(
                this
            )
            addUpdateListener { valueAnimator ->
                multiSearchContainer.horizontalScrollView
                    .translationX = valueAnimator.animatedValue as Float
            }
            doOnEnd { _ ->
                selectedSearchItemTab?.let {
                    it.searchTermEditText.requestFocus()
                    KeyboardUtility.showKeyboard(it.context)
                }
            }
        }
    }

    private val indicatorAnimator by lazy(LazyThreadSafetyMode.NONE) {
        ValueAnimator.ofFloat().apply {
            presenter.configureValueAnimator(
                this
            )
            addUpdateListener { valueAnimator ->
                multiSearchContainer.viewIndicator.x =
                    valueAnimator.animatedValue as Float
            }
        }
    }

    private fun onSearch(viewWidth: Float, searchViewWidth: Float) {
        selectedSearchItemTab?.afterMeasured {
            val widthWithoutCurrentSearch = widthWithoutCurrentSearch()

            when {
                widthWithoutCurrentSearch == 0 -> {
                    firstSearchTranslateAnimator.setFloatValues(viewWidth, 0f)
                    firstSearchTranslateAnimator.start()
                }
                widthWithoutCurrentSearch < viewWidth -> {
                    val scrollEnterStartValue = 0
                    val scrollEnterEndValue = (
                            multiSearchContainer.layoutItemContainer.measuredWidth - viewWidth
                            ).toInt()
                    searchEnterScrollAnimation.setIntValues(
                        scrollEnterStartValue,
                        scrollEnterEndValue
                    )
                    searchEnterScrollAnimation.start()
                }
                else -> {
                    val scrollEnterStartValue = (widthWithoutCurrentSearch - viewWidth).toInt()
                    val scrollEnterEndValue = (
                            widthWithoutCurrentSearch - viewWidth + searchViewWidth.toInt()
                            ).toInt()
                    searchEnterScrollAnimation.setIntValues(
                        scrollEnterStartValue,
                        scrollEnterEndValue
                    )
                    searchEnterScrollAnimation.start()
                }
            }
        }
    }

    internal fun onSearchCompleted() {
        selectedSearchItemTab?.let {
            if ((it.searchTermEditText.text?.length ?: 0) < 3) {
                removeTab(it)
                return
            }
        }

        selectedSearchItemTab?.let {
            it.searchTermEditText.isFocusable = false
            it.searchTermEditText.isFocusableInTouchMode = false
            it.searchTermEditText.clearFocus()
        }

        selectedSearchItemTab?.let {
            val startWidthValue = it.measuredWidth
            val endWidthValue = it.searchTermEditText.measuredWidth +
                    presenter.sizeRemoveIcon +
                    presenter.defaultPadding

            searchCompleteCollapseAnimator.setIntValues(
                startWidthValue,
                endWidthValue
            )
            searchCompleteCollapseAnimator.start()
            multiSearchChangeListener?.onSearchComplete(
                multiSearchContainer.layoutItemContainer.childCount - 1,
                it.searchTermEditText.text.toString()
            )
        }

        selectedSearchItemTab?.let {
            selectTab(it)
        }
    }

    internal fun onItemClicked(searchItem: View) {
        if (searchItem != selectedSearchItemTab) {
            multiSearchChangeListener?.onItemSelected(
                multiSearchContainer.layoutItemContainer.indexOfChild(searchItem),
                searchItem.searchTermEditText.text.toString()
            )
        }
    }

    private fun widthWithoutCurrentSearch(): Int {
        return when {
            multiSearchContainer.layoutItemContainer.childCount > 1 -> {
                var totalWith = 0
                for (i in 0 until multiSearchContainer.layoutItemContainer.childCount - 1) {
                    totalWith += multiSearchContainer.layoutItemContainer
                        .getChildAt(i).measuredWidth
                }
                totalWith
            }
            else -> 0
        }
    }

    private fun onTabRemoving(newSelectedTabView: View, selectedIndex: Int) {
        multiSearchChangeListener?.onItemSelected(
            selectedIndex,
            newSelectedTabView.searchTermEditText.text.toString()
        )
        changeSelectedTab(newSelectedTabView)
        selectedSearchItemTab = newSelectedTabView
    }

    override fun addTab(viewWidth: Float, searchViewWidth: Float) {
        multiSearchContainer.layoutItemContainer.addView(
            selectedSearchItemTab
        )
        onSearch(viewWidth, searchViewWidth)
    }

    override fun removeTab(parent: View) {
        val removeIndex = multiSearchContainer.layoutItemContainer.indexOfChild(parent)
        val currentChildCount = multiSearchContainer.layoutItemContainer.childCount

        when {
            currentChildCount == 1 -> {
                multiSearchContainer.viewIndicator.visibility = View.INVISIBLE
                multiSearchContainer.layoutItemContainer.removeView(parent)
                presenter.isInSearchMode = false
                selectedSearchItemTab = null
            }
            removeIndex == currentChildCount - 1 -> {
                val index = removeIndex - 1
                val newSelectedTabView = multiSearchContainer
                    .layoutItemContainer.getChildAt(index)
                multiSearchContainer.layoutItemContainer.removeView(parent)
                onTabRemoving(newSelectedTabView, index)
            }
            else -> {
                val index = removeIndex + 1
                val newSelectedTabView = multiSearchContainer
                    .layoutItemContainer.getChildAt(index)
                multiSearchContainer.layoutItemContainer.removeView(parent)
                onTabRemoving(newSelectedTabView, index)
            }
        }

        multiSearchChangeListener?.onSearchItemRemoved(removeIndex)
    }

    override fun selectTab(parent: View) {
        val indicatorCurrentXPosition = multiSearchContainer.viewIndicator.x
        val indicatorTargetXPosition = parent.x
        indicatorAnimator.setFloatValues(
            indicatorCurrentXPosition,
            indicatorTargetXPosition
        )
        indicatorAnimator.start()

        multiSearchContainer.viewIndicator.visibility = View.VISIBLE
        parent.searchTermRemoveIcon.visibility = View.VISIBLE
        parent.searchTermEditText.alpha = 1f
    }

    override fun deselectTab(parent: View) {
        multiSearchContainer.viewIndicator.visibility = View.INVISIBLE
        parent.searchTermRemoveIcon.visibility = View.GONE
        parent.searchTermEditText.alpha = 0.5f
    }

    override fun changeSelectedTab(parent: View) {
        selectedSearchItemTab?.let { deselectTab(it) }
        selectedSearchItemTab = parent
        selectedSearchItemTab?.let { selectTab(it) }
    }
}