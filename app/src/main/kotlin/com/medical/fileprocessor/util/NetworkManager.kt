package com.medical.fileprocessor.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Observes network connectivity changes and exposes them as a [Flow].
 *
 * Usage:
 * ```kotlin
 * networkManager.isConnected.collect { connected ->
 *     if (!connected) showOfflineBanner()
 * }
 * ```
 */
@Singleton
class NetworkManager @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    /**
     * Returns `true` when the device currently has an active internet connection.
     */
    val isConnected: Boolean
        get() {
            val network = connectivityManager.activeNetwork ?: return false
            val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
            return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

    /**
     * Hot [Flow] that emits `true` when internet becomes available and `false`
     * when connectivity is lost.  Starts with the current connectivity state and
     * emits only on changes ([distinctUntilChanged]).
     */
    val connectivityFlow: Flow<Boolean> = callbackFlow {
        // Emit the current state immediately
        trySend(isConnected)

        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Timber.tag(Constants.TAG_NETWORK).d("🌐 Network available")
                trySend(true)
            }

            override fun onLost(network: Network) {
                Timber.tag(Constants.TAG_NETWORK).d("🚫 Network lost")
                trySend(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                capabilities: NetworkCapabilities,
            ) {
                val connected = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                trySend(connected)
            }
        }

        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, callback)

        awaitClose {
            connectivityManager.unregisterNetworkCallback(callback)
            Timber.tag(Constants.TAG_NETWORK).d("🧹 Network callback unregistered")
        }
    }.distinctUntilChanged()
}
