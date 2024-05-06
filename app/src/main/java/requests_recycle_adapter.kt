import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ShayaanNofil.i210450.R
import com.ShayaanNofil.i210450.postrecycle_small_adapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class requests_recycle_adapter(private val items: MutableList<User>): RecyclerView.Adapter<requests_recycle_adapter.ViewHolder>() {
    private lateinit var onAcceptClickListener: OnAcceptClickListener
    private lateinit var onRejectClickListener: OnRejectClickListener
    private lateinit var onItemClickListener: OnItemClickListener

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val mentorimg: ImageView = itemView.findViewById(R.id.mentor_img)
        val mentorname: TextView = itemView.findViewById(R.id.sender_name)
        val acceptButton: Button = itemView.findViewById(R.id.accept_button)
        val rejectButton: Button = itemview.findViewById(R.id.reject_button)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): requests_recycle_adapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.requests_recycle, parent, false)
        return requests_recycle_adapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: requests_recycle_adapter.ViewHolder, position: Int) {
        val session = items[position]

        holder.acceptButton.setOnClickListener {
            onAcceptClickListener.onAcceptClick(position, items[position])
        }

        holder.rejectButton.setOnClickListener {
            onRejectClickListener.onRejectClick(position, items[position])
        }

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(position, items[position])
        }

        holder.mentorname.text = session.name

        var rqoptions: RequestOptions = RequestOptions()
        rqoptions= rqoptions.transform(CenterCrop(), RoundedCorners(40))
        Glide.with(holder.itemView)
            .load(session.profilepic)
            .apply(rqoptions)
            .into(holder.mentorimg)
    }

    interface OnAcceptClickListener {
        fun onAcceptClick(position: Int, model: User)
    }

    interface OnRejectClickListener {
        fun onRejectClick(position: Int, model: User)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, model: User)
    }

    // Functions to bind the click listeners
    fun setOnAcceptClickListener(onAcceptClickListener: OnAcceptClickListener) {
        this.onAcceptClickListener = onAcceptClickListener
    }

    fun setOnRejectClickListener(onRejectClickListener: OnRejectClickListener) {
        this.onRejectClickListener = onRejectClickListener
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }
}