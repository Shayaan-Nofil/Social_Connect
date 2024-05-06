import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ShayaanNofil.i210450.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class searchrecycle_adapter(private val items: MutableList<User>): RecyclerView.Adapter<searchrecycle_adapter.ViewHolder>() {
    private lateinit var onClickListener: searchrecycle_adapter.OnClickListener

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val mentorimg: ImageView = itemView.findViewById(R.id.mentor_img)
        val mentorname: TextView = itemView.findViewById(R.id.mentor_name)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): searchrecycle_adapter.ViewHolder {
        var onClickListener: OnClickListener? = null

        val view = LayoutInflater.from(parent.context).inflate(R.layout.searchpage_mentor_recycle, parent, false)
        return searchrecycle_adapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: searchrecycle_adapter.ViewHolder, position: Int) {
        val mentor = items[position]

        holder.mentorname.text = mentor.name

        var rqoptions: RequestOptions = RequestOptions()
        rqoptions= rqoptions.transform(CenterCrop(), RoundedCorners(40))
        Glide.with(holder.itemView)
            .load(mentor.profilepic)
            .apply(rqoptions)
            .into(holder.mentorimg)

        holder.itemView.setOnClickListener {
            onClickListener.onClick(position, mentor)
        }
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: User)
    }

}