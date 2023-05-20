import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.bec_client.MainActivity
import com.example.restapi.home.data.model.NotificationModel
import com.example.restapi.home.viewmodel.SearchViewModel
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class NotificationUpdateListener(
    val owner: MainActivity
) {
    private  var searchViewModel: SearchViewModel
    private var isListening = false
    private val executor = Executors.newSingleThreadScheduledExecutor()
    private var job: Job? = null
    public var userId: Long? = 0

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

    fun stopListening() {
        if (!isListening) {
            Log.d("DEBUG","Listener is not running.")
            return
        }

        isListening = false
        job?.cancel()
        executor.shutdown()
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
                        notification.message?.let { it1 -> owner.sendNotification(it1) };
                    }
            } else {
                Log.d("DEBUG POSTS:", "a crapat")
            }
        })
    }

}