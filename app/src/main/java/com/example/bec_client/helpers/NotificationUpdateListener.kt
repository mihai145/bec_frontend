import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bec_client.MainActivity
import com.example.restapi.home.data.model.request.NotificationDelete
import com.example.restapi.home.data.model.response.SimpleResponseModel
import com.example.restapi.home.viewmodel.SearchViewModel
import com.example.restapi.network.ApiInterface
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Response
import java.util.concurrent.Executors

class NotificationUpdateListener(
    val owner: MainActivity
) {
    private  var searchViewModel: SearchViewModel
    private var isListening = false
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var job: Job? = null
    public var userId: Long? = 0
    private final var MAX_NOTIFY = 5

    private var displayedNotif = mutableSetOf<Long?>()
    init  {
        searchViewModel = ViewModelProvider(owner)[SearchViewModel::class.java]
    }

    fun startListening(intervalSeconds: Long) {
        if (isListening) {
            println("Listener is already running.")
            return
        }

        isListening = true
        job = GlobalScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                while (isListening) {
                    try {
                        queryApiForNotifications()
                        delay(intervalSeconds * 1000) // Delay in milliseconds
                    } catch (e: Exception) {
                        Log.d("DEBUG","An error occurred: ${e.message}")

                    }
                }
            }
        }

        Log.d("DEBUG","Listener started.")
    }
    fun deleteNotif(notifId: Int)
    {
        searchViewModel.deleteNotification(notifId)
    }

    fun stopListening() {
        if (!isListening) {
            Log.d("DEBUG","Listener is not running.")
            return
        }

        isListening = false
        job?.cancel()
        executor.shutdown()
        // idk just delete them all if they have already been displaye
        displayedNotif.forEach { notif -> deleteNotif(notif!!.toInt()) }
        displayedNotif.clear()
        Log.d("DEBUG","Listener stopped.")
    }

    private fun queryApiForNotifications(){

        this.userId?.let { searchViewModel.getNotificationUser(it) }
        searchViewModel.notificationUser?.observe(owner, Observer {
            if (it != null) {
                Log.d("Query ans", it.toString())
                val ans = it.results
                    .forEach { notification ->
                        Log.d("DEBUG", "Processing notification: ${notification.message}")
                        if(displayedNotif.size < MAX_NOTIFY && displayedNotif.add(notification.notificationId))
                        {
                            // Element was not in set
                            owner.sendNotification(notification.notificationId!!.toInt(),
                                notification.message!!,
                                notification.postId!!.toInt()
                            )
                        }
                    }
            } else {
                Log.d("DEBUG POSTS:", "a crapat")
            }
        })
    }

}