package com.mambobryan.libraries.searchbar.ui

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mambobryan.libraries.searchbar.R
import com.mambobryan.libraries.searchbar.model.MultiSearchChangeListener
import com.mambobryan.libraries.searchbar.presenter.MultiSearchPresenter
import kotlinx.android.synthetic.main.search_view.view.*

class MultiSearch @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val presenter by lazy { MultiSearchPresenter(context) }

    init {
        val typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.MultiSearch, defStyleAttr, defStyleAttr
        )
        presenter.createConfiguration(typedArray)
        onInitializeComponent()
    }

    private fun onInitializeComponent() {
        LayoutInflater.from(context).inflate(R.layout.search_view, this, true)
        presenter.configureSearchAction(multiSearchActionIcon)

        multiSearchContainer.presenter = presenter
        multiSearchActionIcon.setOnClickListener {
            if (!presenter.isInSearchMode)
                multiSearchContainer.search()
            else
                multiSearchContainer.completeSearch()
        }
    }

    fun setSearchViewListener(multiSearchChangeListener: MultiSearchChangeListener) {
        multiSearchContainer.multiSearchChangeListener = multiSearchChangeListener
    }
}