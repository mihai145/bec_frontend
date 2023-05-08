package com.example.bec_client.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.bec_client.activity.*
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.data.model.request.CommentInfoModel
import com.example.restapi.home.data.model.request.CommentLikedModel
import com.example.restapi.home.data.model.response.LikeResponseModel
import com.example.restapi.home.data.model.response.SimpleResponseModel
import com.example.restapi.home.viewmodel.SearchViewModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CommentAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: ArrayList<CardModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.comment_layout, parent, false),
            parent.context,

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

    class ViewHolder(itemView: View, context: Context) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView
        val title: TextView
        val body: TextView
        val likesCount: TextView
        val likeButton: ToggleButton
        val apiInterface: ApiInterface? = ApiClient.getApiClient().create(ApiInterface::class.java)
        val applicationContext : Context
        var id : Long

        init {
            image = itemView.findViewById(R.id.image)
            title = itemView.findViewById(R.id.title)
            body = itemView.findViewById(R.id.body)
            likeButton = itemView.findViewById(R.id.likeButton)
            likesCount = itemView.findViewById(R.id.likesCount)
            applicationContext = context
            id = 0
        }

        fun wasLikedComment(commentId: Long, userId: Long): LiveData<Int> {
            val data = MutableLiveData<Int>()
            val requestModel = CommentLikedModel(commentId, userId)
            apiInterface?.wasLikedComment(MainActivity.cachedCredentials?.idToken.toString(), requestModel)
                ?.enqueue(object : Callback<LikeResponseModel> {
                    override fun onFailure(call: Call<LikeResponseModel>, t: Throwable) {
                        data.value = null
                    }

                    override fun onResponse(
                        call: Call<LikeResponseModel>,
                        response: Response<LikeResponseModel>
                    ) {
                        val res = response.body()
                        Log.d("OnResponse", res?.results.toString() + " " + res?.ok.toString())
                        if (response.code() == 202 && res != null && res.ok == true) {
                            data.value = res.results!!
                        } else {
                            data.value = null
                        }
                    }
                })
            return data
        }

        fun getLikesComment(commentId: Long): LiveData<Int> {
            val data = MutableLiveData<Int>()
            val requestModel = CommentInfoModel(commentId)
            apiInterface?.getLikesComment(MainActivity.cachedCredentials?.idToken.toString(), requestModel)
                ?.enqueue(object : Callback<LikeResponseModel> {
                    override fun onFailure(call: Call<LikeResponseModel>, t: Throwable) {
                        data.value = null
                    }

                    override fun onResponse(
                        call: Call<LikeResponseModel>,
                        response: Response<LikeResponseModel>
                    ) {
                        val res = response.body()
                        if (res != null) {
                            Log.d("New number likes", res.results.toString())
                        } else {
                            Log.d("New number likes", "NULL")
                        }
                        if (response.code() == 202 && res != null && res.ok == true) {
                            data.value = res.results!!
                        } else {
                            data.value = null
                        }
                    }
                })

            return data
        }

        fun bind(card: CardModel) {
            id = card.id!!
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
            val userId = card.userId
            var wasLiked :  LiveData<Int>? = null
            var likes : LiveData<Int>? = null
            if(userId != null) {
                wasLiked = wasLikedComment(id, userId)
                if(wasLiked != null && wasLiked != null) {
                    likeButton.isChecked = true
                }

                likes = getLikesComment(id)
                if(likes != null) {
                    likesCount.text = "$likes Likes"
                }
            }

//            wasLiked?.observe(viewLifecycleOwner, Observer {
//                Log.d("Was Liked", wasLiked.toString())
//                if (it != null) {
//                    if (it == 1)
//                        likeButton.isChecked = true
//                    Log.d("Debug Liked", it.toString())
//                }
//            })

            if (card.imagePath != null) {
                Glide.with(itemView.context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(card.imagePath)
                    .into(image)
            }

            title.text = card.title
            body.text = card.body

            likeButton.setOnClickListener {
                val requestModel = CommentLikedModel(id, MainActivity.userId?.toLong() ?: -1L)
                if(likeButton.isChecked) {
                    apiInterface?.likeComment(MainActivity.cachedCredentials?.idToken.toString(), requestModel)
                        ?.enqueue(object : Callback<SimpleResponseModel> {
                            override fun onResponse(
                                call: Call<SimpleResponseModel>,
                                response: Response<SimpleResponseModel>
                            ) {
                                val res = response.body()
                                if (response.code() == 202 && res != null && res.ok) {
                                    Toast.makeText(applicationContext, "Comment was liked", Toast.LENGTH_SHORT)
                                        .show()
                                    val likes = getLikesComment(id).value
                                    Log.d("New number likes", likes.toString())
                                    if(likes != null) {
                                        likesCount.text = "$likes Likes"
                                    }
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Please try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<SimpleResponseModel>, t: Throwable) {
                                Toast.makeText(
                                    applicationContext,
                                    "Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                } else {
                    apiInterface?.deleteLikeComment(MainActivity.cachedCredentials?.idToken.toString(), requestModel)
                        ?.enqueue(object : Callback<SimpleResponseModel> {
                            override fun onResponse(
                                call: Call<SimpleResponseModel>,
                                response: Response<SimpleResponseModel>
                            ) {
                                val res = response.body()
                                if (response.code() == 202 && res != null && res.ok) {
                                    Toast.makeText(applicationContext, "Like was deleted", Toast.LENGTH_SHORT)
                                        .show()
                                    val likes = getLikesComment(id).value
                                    if(likes != null) {
                                        likesCount.text = "$likes Likes"
                                    }
                                } else {
                                    Toast.makeText(
                                        applicationContext,
                                        "Please try again",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            override fun onFailure(call: Call<SimpleResponseModel>, t: Throwable) {
                                Toast.makeText(
                                    applicationContext,
                                    "Please try again",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        })
                }
            }

            itemView.setOnClickListener {
                var intent: Intent

                when (card.type) {
                    5 -> intent = Intent(itemView.context, CommentFormActivity::class.java)
                    else -> {
                        throw Exception("BAD CARD TYPE")
                    }
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