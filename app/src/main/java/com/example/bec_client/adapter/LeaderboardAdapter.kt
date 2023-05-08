package com.example.bec_client.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bec_client.R
import com.example.bec_client.activity.*
import com.example.restapi.home.data.model.CardModel


class LeaderboardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: ArrayList<CardModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lifeCycleOwner = parent.context as LifecycleOwner
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.leaderboard_layout, parent, false),
            parent.context,
            lifeCycleOwner
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                holder.bind(items.get(position))
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addEntry(card: CardModel) {
        val pos = items.size
        items.add(card)
        notifyItemInserted(pos)
    }

    fun submitList(List: List<CardModel>) {
        List.forEach { x -> addEntry(x) }
    }

    fun resetAdapter() {
        items.clear()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View, context: Context, lifeCycleOwner: LifecycleOwner) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView
        val title: TextView
        val likes: TextView
        val applicationContext : Context
        val lifecycleOwner : LifecycleOwner

        init {
            image = itemView.findViewById(R.id.image)
            title = itemView.findViewById(R.id.title)
            likes = itemView.findViewById(R.id.likes)
            applicationContext = context
            lifecycleOwner = lifeCycleOwner
        }

        fun bind(card: CardModel) {
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)


            if (card.imagePath != null) {
                Glide.with(itemView.context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(card.imagePath)
                    .into(image)
            }

            title.text = card.title
            likes.text = card.body

            when (card.place) {
                1L -> image.setImageResource(R.drawable.baseline_filter_1_24)
                2L -> image.setImageResource(R.drawable.baseline_filter_2_24)
                3L -> image.setImageResource(R.drawable.baseline_filter_3_24)
                4L -> image.setImageResource(R.drawable.baseline_filter_4_24)
                5L -> image.setImageResource(R.drawable.baseline_filter_5_24)
            }

            itemView.setOnClickListener {
                var intent: Intent

                when (card.type) {
                    3 -> intent = Intent(itemView.context, UserActivity::class.java)
                    else -> {
                        throw Exception("BAD CARD TYPE")
                    }
                }
                intent.putExtra("id", card.id)
                if (card.type == 3) {
                    intent.putExtra("nickname", card.title)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

}