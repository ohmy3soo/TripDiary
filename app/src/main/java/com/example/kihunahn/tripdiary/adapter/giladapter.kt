package com.example.kihunahn.tripdiary.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.kihunahn.tripdiary.R
import com.example.kihunahn.tripdiary.inform.gilinform

class  giladpater(private var activity: Activity, private var items: ArrayList<gilinform>) : BaseAdapter(){

    private class ViewHolder(row: View?) {
        var txtStr: TextView? = null

        init {
            this.txtStr = row?.findViewById<TextView>(R.id.txtStr)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View?
        val viewHolder: ViewHolder
        if (convertView == null) {
            val inflater = activity?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.gil_list, null)
            viewHolder = ViewHolder(view)
            view?.tag = viewHolder
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder
        }

        var gilDto = items[position]
        viewHolder.txtStr?.text = gilDto.str

        return view as View
    }

    override fun getItem(i: Int): Any {
        return items[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getCount(): Int {
        return items.size
    }
}