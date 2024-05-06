//package com.ShayaanNofil.i210450
//
//import android.content.Context
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import de.hdodenhof.circleimageview.CircleImageView
//
//class chat_adapter(private val context: Context, private val chatList: List<Chat>) : RecyclerView.Adapter<chat_adapter.ViewHolder>() {
//
//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
//        val chatName: TextView = itemView.findViewById(R.id.chat_name)
//        val newMessageCount: TextView = itemView.findViewById(R.id.new_message_count)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_page_recycle, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val chat = chatList[position]
//
//        // Bind data to views
//        holder.chatName.text = chat.name
//        holder.newMessageCount.text = "${chat.newMessageCount} New Message"
//
//        // Load profile image using Glide or any other image loading library
//        Glide.with(context)
//            .load(chat.profileImageUrl)
//            .placeholder(R.drawable.john_circle)
//            .into(holder.profileImage)
//    }
//    override fun getItemCount(): Int {
//        return chatList.size
//    }
//}