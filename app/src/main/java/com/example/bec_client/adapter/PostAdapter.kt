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
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.bec_client.MainActivity
import com.example.bec_client.R
import com.example.bec_client.activity.*
import com.example.restapi.home.data.model.CardModel
import com.example.restapi.home.data.model.request.PostInfoModel
import com.example.restapi.home.data.model.request.PostLikedModel
import com.example.restapi.home.data.model.response.LikeResponseModel
import com.example.restapi.home.data.model.response.SimpleResponseModel
import com.example.restapi.network.ApiClient
import com.example.restapi.network.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PostAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: ArrayList<CardModel> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val lifeCycleOwner = parent.context as LifecycleOwner
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.post_layout, parent, false),
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
        val body: TextView
        val likesCount: TextView
        val likeButton: ToggleButton
        val apiInterface: ApiInterface? = ApiClient.getApiClient().create(ApiInterface::class.java)
        val applicationContext : Context
        var id : Long
        val lifecycleOwner : LifecycleOwner

        init {
            image = itemView.findViewById(R.id.image)
            title = itemView.findViewById(R.id.title)
            body = itemView.findViewById(R.id.body)
            likeButton = itemView.findViewById(R.id.likeButton)
            likesCount = itemView.findViewById(R.id.likesCount)
            applicationContext = context
            id = 0
            lifecycleOwner = lifeCycleOwner
        }

        fun wasLikedPost(postId: Long, userId: Long): LiveData<Int> {
            val data = MutableLiveData<Int>()
            val requestModel = PostLikedModel(postId, userId)
            apiInterface?.wasLikedPost(MainActivity.cachedCredentials?.idToken.toString(), requestModel)
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

        fun getLikesPost(postId: Long): LiveData<Int> {
            val data = MutableLiveData<Int>()
            val requestModel = PostInfoModel(postId)
            apiInterface?.getLikesPost(MainActivity.cachedCredentials?.idToken.toString(), requestModel)
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
            var wasLiked :  LiveData<Int>? = null
            var likes : LiveData<Int>? = null
            wasLiked = wasLikedPost(id, MainActivity.userId?.toLong() ?: -1L)
            likes = getLikesPost(id)
            likesCount.text = ""


            wasLiked?.observe(lifecycleOwner, Observer {
                Log.d("Was Liked", wasLiked.toString())
                if (it != null) {
                    if (it == 1) {
                        likeButton.isChecked = true
                        likesCount.text = "You + " + likesCount.text
                    } else {
                        likeButton.isChecked = false
                    }
                    Log.d("Debug Liked", it.toString())
                }
            })
            likes?.observe(lifecycleOwner, Observer {
                Log.d("Likes", it.toString())
                if (it != null) {
                    var real = it
                    if(likeButton.isChecked)
                        real -= 1
                    Log.d("likeCount content before:", likesCount.text.toString())
                    likesCount.text = "${likesCount.text}$real Likes"
                    Log.d("Debug Likes", it.toString())
                }
            })


            if (card.imagePath != null) {
                Glide.with(itemView.context)
                    .applyDefaultRequestOptions(requestOptions)
                    .load(card.imagePath)
                    .into(image)
            }

            title.text = card.title
            body.text = card.body

            likeButton.setOnClickListener {
                val requestModel = PostLikedModel(id, MainActivity.userId?.toLong() ?: -1L)
                if(likeButton.isChecked) {
                    apiInterface?.likePost(MainActivity.cachedCredentials?.idToken.toString(), requestModel)
                        ?.enqueue(object : Callback<SimpleResponseModel> {
                            override fun onResponse(
                                call: Call<SimpleResponseModel>,
                                response: Response<SimpleResponseModel>
                            ) {
                                val res = response.body()
                                if (response.code() == 202 && res != null && res.ok) {
                                    Toast.makeText(applicationContext, "Post was liked", Toast.LENGTH_SHORT)
                                        .show()
                                    likesCount.text = "You + " + likesCount.text
                                    Log.d("New number likes", likes.toString())
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
                    apiInterface?.deleteLikePost(MainActivity.cachedCredentials?.idToken.toString(), requestModel)
                        ?.enqueue(object : Callback<SimpleResponseModel> {
                            override fun onResponse(
                                call: Call<SimpleResponseModel>,
                                response: Response<SimpleResponseModel>
                            ) {
                                val res = response.body()
                                if (response.code() == 202 && res != null && res.ok) {
                                    Toast.makeText(applicationContext, "Like was deleted", Toast.LENGTH_SHORT)
                                        .show()
                                    likesCount.text = likesCount.text.drop(6)
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
                    4 -> intent = Intent(itemView.context, PostActivity::class.java)
                    else -> {
                        throw Exception("BAD CARD TYPE")
                    }
                }
                intent.putExtra("id", card.id)
                itemView.context.startActivity(intent)
            }
        }
    }

}