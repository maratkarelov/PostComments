package test.coreteka.presentation.screens

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import test.coreteka.R
import test.coreteka.broadcast.ConnectivityReceiver
import test.coreteka.presentation.core.IConnected

class MainActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {
    private var snackBar: Snackbar? = null

    companion object {
        const val tag: String = "current"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fragment = UsersFragment()
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment, tag)
            .commit()
        registerReceiver(
            ConnectivityReceiver(),
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        if (isConnected) {
            val fragment = supportFragmentManager.findFragmentByTag(tag)
            if (fragment is IConnected) {
                fragment.onNetworkAvailable()
            }
        }
        updateNetworkMessage(isConnected)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun updateNetworkMessage(isConnected: Boolean) {
        if (isConnected) {
            snackBar?.dismiss()
        } else {
            snackBar = Snackbar.make(
                findViewById(R.id.root_layout),
                "You are offline",
                Snackbar.LENGTH_LONG
            ) //Assume "rootLayout" as the root layout of every activity.
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.show()
        }
    }

}