package com.global.vtg.appview.home.uploaddocument

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.NonNull
import com.vtg.R


class TestResultSpinnerAdapter(context: Context, items: List<String?>) :
    ArrayAdapter<String?>(context, R.layout.my_spinner_row, items) {
    override fun getDropDownView(
        position: Int,
        convertView: View?,
        @NonNull parent: ViewGroup
    ): View {
        return if (position == 0) {
            initialSelection(true)
        } else getCustomView(position, convertView, parent)
    }

    @NonNull
    override fun getView(position: Int, convertView: View?, @NonNull parent: ViewGroup): View {
        return if (position == 0) {
            initialSelection(false)
        } else getCustomView(position, convertView, parent)
    }

    override fun getCount(): Int {
        return super.getCount() + 1
    }

    private fun initialSelection(dropdown: Boolean): View {
        // Just an example using a simple TextView. Create whatever default view
        // to suit your needs, inflating a separate layout if it's cleaner.

        val view = LayoutInflater.from(context).inflate(R.layout.my_spinner_title,null)
        val textView = (view as TextView).findViewById<TextView>(R.id.tvSpinnerText)
        textView.setText(R.string.select)
        if (dropdown) { // Hidden when the dropdown is opened
            view.height = 0
        }
        return view
    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Distinguish "real" spinner items (that can be reused) from initial selection item
        var itemPosition = position
        val view = LayoutInflater.from(context).inflate(R.layout.my_spinner_row,null)
        val textView = (view as TextView).findViewById<TextView>(R.id.tvSpinnerText)
        itemPosition -= 1 // Adjust for initial selection item
        val item: String? = getItem(itemPosition)
        textView.text = item
        return view
    }
}