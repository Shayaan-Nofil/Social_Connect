package com.ShayaanNofil.i210450

import Posts
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class postrecycle_small_adapter(private val items: MutableList<Posts>): RecyclerView.Adapter<postrecycle_small_adapter.ViewHolder>() {

    private lateinit var onClickListener: postrecycle_small_adapter.OnClickListener

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val postimg: ImageView = itemView.findViewById(R.id.mentor_img)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var onClickListener: OnClickListener? = null

        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_recycle_small_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = items[position]

        var rqoptions: RequestOptions = RequestOptions()
        rqoptions= rqoptions.transform(CenterCrop(), RoundedCorners(40))
        Glide.with(holder.itemView)
            .load(post.content)
            .apply(rqoptions)
            .into(holder.postimg)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(position, post)
        }
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: Posts)
    }

}