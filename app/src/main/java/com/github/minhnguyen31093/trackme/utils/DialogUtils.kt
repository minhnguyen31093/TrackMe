package com.github.minhnguyen31093.trackme.utils

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.ListAdapter
import android.widget.Toast
import com.github.minhnguyen31093.trackme.R


class DialogUtils {
    companion object {
        fun createAlertDialog(context: Context, title: String?, message: String?, positiveString: String?, negativeString: String?, onClickListener: DialogInterface.OnClickListener?): AlertDialog {
            val builder = AlertDialog.Builder(context)
            if (title != null && !title.isEmpty()) {
                builder.setTitle(title)
            }
            if (message != null && !message.isEmpty()) {
                builder.setMessage(message)
            }
            if (positiveString != null && !positiveString.isEmpty()) {
                builder.setPositiveButton(positiveString, onClickListener)
            }
            if (negativeString != null && !negativeString.isEmpty()) {
                builder.setNegativeButton(negativeString, onClickListener)
            }
            return builder.create()
        }

        fun alert(context: Context, message: String, positiveString: String, negativeString: String, onClickListener: DialogInterface.OnClickListener) {
            createAlertDialog(context, "", message, positiveString, negativeString, onClickListener).show()
        }

        fun alert(context: Context?, message: String, positiveString: String, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                createAlertDialog(context, "", message, positiveString, "", onClickListener).show()
            }
        }

        private fun alert(context: Context?, message: String, positiveString: String) {
            if (context != null) {
                createAlertDialog(context, "", message, positiveString, "", null).show()
            }
        }

        fun alert(context: Context?, titleId: Int, messageId: Int, positiveStringId: Int, negativeStringId: Int, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                createAlertDialog(context, getString(context, titleId), getString(context, messageId), getString(context, positiveStringId), getString(context, negativeStringId), onClickListener).show()
            }
        }

        fun alertDialog(context: Context, titleId: Int, messageId: Int, positiveStringId: Int, negativeStringId: Int, onClickListener: DialogInterface.OnClickListener): AlertDialog {
            return createAlertDialog(context, getString(context, titleId), getString(context, messageId), getString(context, positiveStringId), getString(context, negativeStringId), onClickListener)
        }

        fun alert(context: Context?, messageId: Int, positiveStringId: Int, negativeStringId: Int, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                createAlertDialog(context, "", getString(context, messageId), getString(context, positiveStringId), getString(context, negativeStringId), onClickListener).show()
            }
        }

        fun alertDialog(context: Context, messageId: Int, positiveStringId: Int, negativeStringId: Int, onClickListener: DialogInterface.OnClickListener): AlertDialog {
            return createAlertDialog(context, "", getString(context, messageId), getString(context, positiveStringId), getString(context, negativeStringId), onClickListener)
        }

        fun alertDialog(context: Context, messageId: String, positiveStringId: String, negativeStringId: String, onClickListener: DialogInterface.OnClickListener): AlertDialog {
            return createAlertDialog(context, "", messageId, positiveStringId, negativeStringId, onClickListener)
        }

        fun alert(context: Context?, messageId: Int, positiveStringId: Int, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                createAlertDialog(context, "", getString(context, messageId), getString(context, positiveStringId), "", onClickListener).show()
            }
        }

        fun alertForce(context: Context?, messageId: Int, positiveStringId: Int, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                val alertDialog = createAlertDialog(context, "", getString(context, messageId), getString(context, positiveStringId), "", onClickListener)
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }
        }

        fun alertForce(context: Context?, messageId: Int, positiveStringId: Int, negativeStringId: Int, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                val alertDialog = createAlertDialog(context, "", getString(context, messageId), getString(context, positiveStringId), getString(context, negativeStringId), onClickListener)
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }
        }

        fun alertDialog(context: Context, messageId: Int, positiveStringId: Int, onClickListener: DialogInterface.OnClickListener): AlertDialog {
            return createAlertDialog(context, "", getString(context, messageId), getString(context, positiveStringId), "", onClickListener)
        }

        fun alertDialog(context: Context, message: String, positiveString: String, onClickListener: DialogInterface.OnClickListener): AlertDialog {
            return createAlertDialog(context, "", message, positiveString, "", onClickListener)
        }

        fun alert(context: Context?, messageId: Int, positiveStringId: Int) {
            if (context != null) {
                createAlertDialog(context, "", getString(context, messageId), getString(context, positiveStringId), "", null).show()
            }
        }

        fun alertDialog(context: Context, messageId: Int, positiveStringId: Int): AlertDialog {
            return createAlertDialog(context, "", getString(context, messageId), getString(context, positiveStringId), "", null)
        }

        fun alert(context: Context, view: View, positiveStringId: Int, negativeStringId: Int, onClickListener: DialogInterface.OnClickListener) {
            AlertDialog.Builder(context).setView(view).setPositiveButton(positiveStringId, onClickListener).setNegativeButton(negativeStringId, onClickListener).create().show()
        }

        fun alert(context: Context, titleId: Int, view: View) {
            AlertDialog.Builder(context).setTitle(titleId).setView(view).create().show()
        }

        fun alert(context: Context, titleId: Int, adapter: ListAdapter, checkedItem: Int, onClickListener: DialogInterface.OnClickListener) {
            AlertDialog.Builder(context).setTitle(titleId).setSingleChoiceItems(adapter, checkedItem, onClickListener).create().show()
        }

        fun alertYesNo(context: Context?, messageId: Int, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                alert(context, messageId, android.R.string.yes, android.R.string.no, onClickListener)
            }
        }

        fun alertYesNoForce(context: Context?, messageId: Int, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                val alertDialog = alertDialog(context, messageId, android.R.string.yes, android.R.string.no, onClickListener)
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }
        }

        fun alertInfo(context: Context?, message: String) {
            if (context != null) {
                alert(context, message, getString(context, android.R.string.ok))
            }
        }

        fun alertInfo(context: Context?, message: Int) {
            if (context != null) {
                alertInfo(context, getString(context, message))
            }
        }

        fun alertInfoForce(context: Context?, message: Int, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                val alertDialog = alertDialog(context, message, android.R.string.ok, onClickListener)
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }
        }

        fun alertDialogInfoForce(context: Context, message: Int, onClickListener: DialogInterface.OnClickListener): AlertDialog {
            val alertDialog = alertDialog(context, message, android.R.string.ok, onClickListener)
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false)
            return alertDialog
        }

        fun alertError(context: Context?, messageId: Int) {
            if (context != null) {
                alert(context, messageId, R.string.close)
            }
        }

        fun alertErrorForce(context: Context?, messageId: Int) {
            if (context != null) {
                val alertDialog = alertDialog(context, messageId, R.string.close)
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }
        }

        fun alertErrorForce(context: Context?, msg: String, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                val alertDialog = alertDialog(context, msg, getString(context, R.string.close), onClickListener)
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }
        }

        fun alertErrorForce(context: Context?, messageId: Int, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                val alertDialog = alertDialog(context, messageId, R.string.close, onClickListener)
                alertDialog.setCancelable(false)
                alertDialog.setCanceledOnTouchOutside(false)
                alertDialog.show()
            }
        }

        fun alertError(context: Context?, messageId: Int, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                alert(context, messageId, R.string.close, onClickListener)
            }
        }

        fun alertError(context: Context?, msg: String, onClickListener: DialogInterface.OnClickListener) {
            if (context != null) {
                alert(context, msg, getString(context, R.string.close), onClickListener)
            }
        }

        fun alertError(context: Context?, msg: String) {
            if (context != null) {
                alert(context, msg, getString(context, R.string.close))
            }
        }

        fun toast(context: Context, id: Int) {
            toast(context, getString(context, id))
        }

        fun toast(context: Context?, content: String) {
            if (context != null) {
                val toast = Toast.makeText(context, content, Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        fun getString(context: Context?, id: Int): String {
            return if (context != null) {
                context.getString(id)
            } else {
                ""
            }
        }
    }
}