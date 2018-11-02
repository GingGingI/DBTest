package com.example.ginggingi.dbtest.Adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.listitem.view.*

class RcHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
    val rtxtv = itemView!!.txt
    val rx = itemView!!.vx
    val ry = itemView!!.vy
    val rtype = itemView!!.vtype
}