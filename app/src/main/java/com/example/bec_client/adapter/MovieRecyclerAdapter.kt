package com.example.bec_client.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bec_client.R
import com.example.restapi.home.data.model.MovieModel
import kotlin.collections.ArrayList


class MovieRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    private var items: List<MovieModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {

            is ViewHolder -> {
                holder.bind(items.get(position))
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun submitList(List: List<MovieModel>){
        Log.d("DEBUG in adapter",List.toString())
        items = List
        notifyDataSetChanged()
    }

    class ViewHolder
    constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){

        val image = itemView.findViewById<ImageView>(R.id.image)
        val title = itemView.findViewById<TextView>(R.id.title)
        val body = itemView.findViewById<TextView>(R.id.body)

        fun bind(movie: MovieModel){
            Log.d("Binding",movie.toString())
//            TODO("this bad boy broken no idea why")
//            val requestOptions = RequestOptions()
//                .placeholder(R.drawable.ic_launcher_background)
//                .error(R.drawable.ic_launcher_background)
//
//            Glide.with(itemView.context)
//                .applyDefaultRequestOptions(requestOptions)
//                .load(movie.posterPath)
//                .into(image)
            title.setText(movie.title)
            body.setText(movie.overview)
        }

    }

}