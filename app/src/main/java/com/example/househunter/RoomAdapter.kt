package com.example.househunter
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RoomAdapter(private val rooms: List<Room>) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomImageView: ImageView = itemView.findViewById(R.id.roomImageView)
        val roomTitleTextView: TextView = itemView.findViewById(R.id.roomTitleTextView)
        val monthlyRent: TextView = itemView.findViewById(R.id.monthlyRent)
        val deposit: TextView = itemView.findViewById(R.id.deposit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.ai_detail, parent, false)
        return RoomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val room = rooms[position]

        // 데이터를 뷰에 바인딩
        room.photos?.let {
            val imageUrl = it["photo1"] // HashMap에서 이미지 URL을 가져옵니다. 이 예제에서는 photo1만 사용합니다.
            imageUrl?.let {
                Glide.with(holder.itemView.context)
                    .load(it)
                    .into(holder.roomImageView)
            }
        }

        holder.roomTitleTextView.text = room.location
        holder.monthlyRent.text = room.monthly_money?.toString() ?: "N/A"
        holder.deposit.text = room.fix_money?.toString() ?: "N/A"

        // 리사이클러뷰 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener {
            // 클릭한 아이템의 정보를 가지고 있는 Room 객체를 이용하여 다른 액티비티로 이동
            val intent = Intent(holder.itemView.context, RoomDetailActivity::class.java)
            intent.putExtra("roomID", room.roomID)
            intent.putExtra("locate", room.locate)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return rooms.size
    }
}

