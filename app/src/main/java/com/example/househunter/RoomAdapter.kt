package com.example.househunter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RoomAdapter(private val rooms: List<Room>) :
    RecyclerView.Adapter<RoomAdapter.RoomViewHolder>() {

    private var onItemClickListener: ((Room) -> Unit)? = null

    fun setOnItemClickListener(listener: (Room) -> Unit) {
        this.onItemClickListener = listener
    }

    inner class RoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val roomImageView: ImageView = itemView.findViewById(R.id.roomImageView)
        val roomTitleTextView: TextView = itemView.findViewById(R.id.roomTitleTextView)
        val monthlyRent: TextView = itemView.findViewById(R.id.monthlyRent)
        val deposit: TextView = itemView.findViewById(R.id.deposit)

        init {
            // 아이템 클릭 시
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(rooms[position])
                }
            }
        }
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
            val imageUrl = it["photo1"]
            imageUrl?.let {
                Glide.with(holder.itemView.context)
                    .load(it)
                    .into(holder.roomImageView)
            }
        }

        holder.roomTitleTextView.text = room.location
        holder.monthlyRent.text = room.monthly_money?.toString() ?: "N/A"
        holder.deposit.text = room.fix_money?.toString() ?: "N/A"

    }

    override fun getItemCount(): Int {
        return rooms.size
    }
}

