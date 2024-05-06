package com.ShayaanNofil.i210450

import Posts
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class postrecycle_adapter(private val items: MutableList<Posts>): RecyclerView.Adapter<postrecycle_adapter.ViewHolder>() {

    private lateinit var onClickListener: OnClickListener
    private lateinit var onAcceptClickListener: OnAcceptClickListener

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val postimg: ImageView = itemView.findViewById(R.id.mentor_img)
        val postname: TextView = itemView.findViewById(R.id.mentor_name)
        val postdescription: TextView = itemView.findViewById(R.id.mentor_job)
        val postlikes: TextView = itemView.findViewById(R.id.like_count)
        val likebutton: Button = itemview.findViewById(R.id.like_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_recycle_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = items[position]
        holder.likebutton.setBackgroundResource(R.drawable.grey_heart)

        FirebaseDatabase.getInstance().getReference("Posts").child(post.id).child("Likes").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(String::class.java)
                        if (myclass != null) {
                            if (myclass == Firebase.auth.uid.toString()) {
                                holder.likebutton.setBackgroundResource(R.drawable.red_heart)
                            }
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        holder.likebutton.setOnClickListener {
            onAcceptClickListener.onAcceptClick(position, items[position])
        }
        holder.itemView.setOnClickListener {
            onClickListener.onClick(position, post)
        }

        holder.postname.text = post.sendername
        holder.postdescription.text = post.description
        holder.postlikes.text = post.likes.toString()

        var rqoptions: RequestOptions = RequestOptions()
        rqoptions= rqoptions.transform(CenterCrop(), RoundedCorners(40))
        Glide.with(holder.itemView)
            .load(post.content)
            .apply(rqoptions)
            .into(holder.postimg)

    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }
    fun setOnAcceptClickListener(onAcceptClickListener: OnAcceptClickListener) {
        this.onAcceptClickListener = onAcceptClickListener
    }

    interface OnAcceptClickListener {
        fun onAcceptClick(position: Int, model: Posts)
    }
    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: Posts)
    }

}