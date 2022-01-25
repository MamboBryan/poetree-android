package com.mambo.core.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import com.mambo.core.R

object LoadingDialog {

    private var dialog: Dialog? = null

    fun show(context: Context?, cancelable: Boolean, text: String? = "Please Wait") {

        initDialog(context, cancelable)

        try {
            dialog!!.show()
        } catch (e: Exception) {
        }
    }

    private fun initDialog(context: Context?, isCancellable: Boolean) {
        dialog = Dialog(context!!)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.layout_dialog_loading)
//        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog!!.setCancelable(isCancellable)
    }

    fun dismiss() {
        try {
            if (dialog != null) {
                dialog!!.dismiss()
            }
        } catch (e: Exception) {
        }
    }
}