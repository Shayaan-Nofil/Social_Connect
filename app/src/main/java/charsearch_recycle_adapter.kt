import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ShayaanNofil.i210450.R
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import de.hdodenhof.circleimageview.CircleImageView

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
private lateinit var typeofuser: String
class chatsearch_recycle_adapter(private val items: MutableList<Chats>): RecyclerView.Adapter<chatsearch_recycle_adapter.ViewHolder>() {
    private lateinit var onClickListener: chatsearch_recycle_adapter.OnClickListener

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val profleimg: CircleImageView = itemView.findViewById(R.id.profile_image)
        val personname: TextView = itemView.findViewById(R.id.chat_name)
        val missmessagecount: TextView = itemView.findViewById(R.id.new_message_count)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chatsearch_page_recycle, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chat = items[position]
        mAuth = Firebase.auth

        setrecyclerdata(holder, chat)

        val countstring = chat.messagecount.toString() + " New Messages"
        holder.missmessagecount.text = countstring


        holder.itemView.setOnClickListener {
            onClickListener.onClick(position, chat)
        }
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: Chats)
    }

    fun setrecyclerdata(holder: ViewHolder, chat: Chats) {
        mAuth = Firebase.auth
        FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val user = data.getValue(User::class.java)
                    if (user != null) {
                        FirebaseDatabase.getInstance().getReference("User").child(user.id).child("Chats").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snap: DataSnapshot) {
                                if (snap.exists()) {
                                    for (temp in snap.children) {
                                        val chats = temp.getValue(String::class.java)
                                        if (chats != null) {
                                            if (chats == chat.id && user.id != mAuth.uid.toString()) {
                                                holder.personname.text = user.name

                                                Glide.with(holder.itemView)
                                                    .load(user.profilepic)
                                                    .into(holder.profleimg)
                                            }
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}

