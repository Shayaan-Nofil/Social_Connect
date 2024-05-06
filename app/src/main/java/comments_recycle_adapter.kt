package com.ShayaanNofil.i210450

import Comments
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class comments_recycle_adapter(private val items: MutableList<Comments>): RecyclerView.Adapter<comments_recycle_adapter.ViewHolder>() {

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val sendername: TextView = itemView.findViewById(R.id.mentor_name)
        val content: TextView = itemView.findViewById(R.id.review_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.comments_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = items[position]

        holder.sendername.text = comment.sendername
        holder.content.text = comment.content
    }
}