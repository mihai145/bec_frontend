package com.example.bec_client.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bec_client.R
import com.example.bec_client.activity.*
import com.example.restapi.home.data.model.CardModel


class RecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: ArrayList<CardModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
            )
            1 -> return ViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.user_layout, parent, false)
            )
        }
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return if(items.get(position).type == 3) {
            1
        } else {
            0
        }
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

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView
        val title: TextView
        val body: TextView

        init {
            image = itemView.findViewById(R.id.image)
            title = itemView.findViewById(R.id.title)
            body = itemView.findViewById(R.id.body)
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
            body.text = card.body

            itemView.setOnClickListener {
                var intent: Intent

                when (card.type) {
                    1 -> intent = Intent(itemView.context, MovieActivity::class.java)
                    2 -> intent = Intent(itemView.context, ActorActivity::class.java)
                    3 -> intent = Intent(itemView.context, UserActivity::class.java)
                    4 -> intent = Intent(itemView.context, PostActivity::class.java)
                    5 -> intent = Intent(itemView.context, CommentFormActivity::class.java)
                    else -> {
                        throw Exception("BAD CARD TYPE")
                    }
                }
                intent.putExtra("id", card.id)
                if (card.type == 3) {
                    intent.putExtra("nickname", card.title)
                }
                if (card.type == 5) {
                    intent.putExtra("newOrEdit", 1L)
                    intent.putExtra("content", card.body)
                    intent.putExtra("postId", card.postId)
                    intent.putExtra("userId", card.userId)
                }
                itemView.context.startActivity(intent)
            }
        }
    }

}