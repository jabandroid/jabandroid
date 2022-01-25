package com.global.vtg.appview.home.uploaddocument


import android.content.Context

import android.widget.TextView

import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup

import android.widget.ArrayAdapter
import android.widget.Filter
import com.global.vtg.appview.config.Institute
import com.vtg.R
import java.lang.String




/*

class InstituteAutoCompleteAdapter(context: Context?, viewResourceId: Int, items: ArrayList<Institute>) :
    ArrayAdapter<Institute?>(context!!, viewResourceId, items as List<Institute?>) {

    private val items: ArrayList<Institute> = items
    private val viewResourceId: Int = viewResourceId
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            val vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(viewResourceId, null)
        }
        val Institute: Institute = items[position]
        val InstituteNameLabel = v!!.findViewById<View>(R.id.tvInstitute) as TextView
        InstituteNameLabel.text = String.valueOf(Institute.name)
        return v!!
    }

}*/

class InstituteAutoCompleteAdapter(context: Context?, textViewResourceId: Int, Institutes: List<Institute>) :
    ArrayAdapter<Institute?>(context!!, textViewResourceId, Institutes!!) {
    private val layoutInflater: LayoutInflater
    var mInstitutes: MutableList<Institute> = ArrayList()
    private val mFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): kotlin.String {
            return (resultValue as Institute).name!!
        }

        protected override fun performFiltering(constraint: CharSequence?): FilterResults {
            val results = FilterResults()
            if (constraint != null) {
                val suggestions: ArrayList<Institute> = ArrayList<Institute>()
                for (Institute in mInstitutes) {
                    // Note: change the "contains" to "startsWith" if you only want starting matches
                    if (Institute.name!!.lowercase()
                            .contains(constraint.toString().lowercase())
                    ) {
                        suggestions.add(Institute)
                    }
                }
                results.values = suggestions
                results.count = suggestions.size
            }
            return results
        }

        protected override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            clear()
            if (results != null && results.count > 0) {
                // we have filtered results
                addAll(results.values as ArrayList<Institute?>)
            } else {
                // no filter, add entire original list back in
                addAll(mInstitutes)
            }
            notifyDataSetChanged()
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            view = layoutInflater.inflate(R.layout.recycler_view_institute, null)
        }
        val Institute: Institute? = getItem(position)
        val name = view!!.findViewById<View>(R.id.tvInstitute) as TextView
        name.setText(Institute!!.name)
        return view
    }

    override fun getFilter(): Filter {
        return mFilter
    }

    init {
        // copy all the Institutes into a master list
        mInstitutes = ArrayList<Institute>(Institutes.size)
        mInstitutes.addAll(Institutes)
        layoutInflater =
            getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    fun setList(m: MutableList<Institute>){
        mInstitutes=m
        notifyDataSetChanged()

    }
}