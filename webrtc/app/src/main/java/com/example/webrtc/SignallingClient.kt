package com.example.webrtc

import android.annotation.SuppressLint
import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import java.net.URISyntaxException
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

internal class SignallingClient {
    private var roomName: String? = null
    private var socket: Socket? = null
    var isChannelReady = false
    var isInitiator = false
    var isStarted = false
    private var callback: SignalingInterface? = null

    //This piece of code should not go into production!!
    //This will help in cases where the node server is running in non-https server and you want to ignore the warnings
    @SuppressLint("TrustAllX509TrustManager")
    private val trustAllCerts =
        arrayOf<TrustManager>(object : X509TrustManager {
            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }

            override fun checkClientTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }
        })

    fun init(signalingInterface: MainActivity) {
        callback = signalingInterface
        try {
            val sslcontext = SSLContext.getInstance("TLS")
            sslcontext.init(null, trustAllCerts, null)
            IO.setDefaultHostnameVerifier { hostname: String?, session: SSLSession? -> true }
            IO.setDefaultSSLContext(sslcontext)
            //set the socket.io url here
            socket =
                IO.socket("https://192.168.0.105:3000?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1ZjA1YmJkYTIwZDQzMTAwMTdlYmM5YzMiLCJpYXQiOjE1OTQ3MTIyMTN9.fzzVDGFZzQnHnksYT6OXUWlcTrhi8eWSq_Xrkot0ArU")
            socket.connect()
            Log.d("SignallingClient", "init() called")
            if (roomName!!.isNotEmpty()) {
                emitInitStatement(roomName)
            }
            socket.emit("call", "5f05baa520d4310017ebc9bd")
            socket.on("callAccepted",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.d(
                        "SignallingClient",
                        "call accepted: args = [" + Arrays.toString(args) + "]"
                    )
                }
            )


            //room created event.
//            socket.on("call", args -> {
//                Log.d("SignallingClient", "created call() called with: args = [" + Arrays.toString(args) + "]");
//                isInitiator = true;
//                callback.onCreatedRoom();
//            });


            //room is full event
            socket.on("full",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.d(
                        "SignallingClient",
                        "full call() called with: args = [" + Arrays.toString(args) + "]"
                    )
                }
            )

            //peer joined event
            socket.on("join", Emitter.Listener { args: Array<Any?>? ->
                Log.d(
                    "SignallingClient",
                    "join call() called with: args = [" + Arrays.toString(args) + "]"
                )
                isChannelReady = true
                callback!!.onNewPeerJoined()
            })

            //when you joined a chat room successfully
            socket.on("joined", Emitter.Listener { args: Array<Any?>? ->
                Log.d(
                    "SignallingClient",
                    "joined call() called with: args = [" + Arrays.toString(args) + "]"
                )
                isChannelReady = true
                callback!!.onJoinedRoom()
            })

            //log event
            socket.on("log",
                Emitter.Listener { args: Array<Any?>? ->
                    Log.d(
                        "SignallingClient",
                        "log call() called with: args = [" + Arrays.toString(args) + "]"
                    )
                }
            )

            //bye event
            socket.on("bye",
                Emitter.Listener { args: Array<Any?> ->
                    callback!!.onRemoteHangUp(
                        args[0] as String?
                    )
                }
            )

            //messages - SDP and ICE candidates are transferred through this
            socket.on("message", Emitter.Listener { args: Array<Any> ->
                Log.d(
                    "SignallingClient",
                    "message call() called with: args = [" + Arrays.toString(args) + "]"
                )
                if (args[0] is String) {
                    Log.d("SignallingClient", "String received :: " + args[0])
                    val data = args[0] as String
                    if (data.equals("got user media", ignoreCase = true)) {
                        callback!!.onTryToStart()
                    }
                    if (data.equals("bye", ignoreCase = true)) {
                        callback!!.onRemoteHangUp(data)
                    }
                } else if (args[0] is JSONObject) {
                    try {
                        val data = args[0] as JSONObject
                        Log.d(
                            "SignallingClient",
                            "Json Received :: $data"
                        )
                        val type = data.getString("type")
                        if (type.equals("offer", ignoreCase = true)) {
                            callback!!.onOfferReceived(data)
                        } else if (type.equals("answer", ignoreCase = true) && isStarted) {
                            callback!!.onAnswerReceived(data)
                        } else if (type.equals("candidate", ignoreCase = true) && isStarted) {
                            callback!!.onIceCandidateReceived(data)
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            })
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        }
    }

    private fun emitInitStatement(message: String?) {
        Log.d(
            "SignallingClient",
            "emitInitStatement() called with: event = [create or join], message = [$message]"
        )
        socket!!.emit("create or join", message)
    }

    fun emitMessage(message: String) {
        Log.d(
            "SignallingClient",
            "emitMessage() called with: message = [$message]"
        )
        socket!!.emit("call", message)
    }

    fun emitMessage(message: SessionDescription) {
        try {
            Log.d(
                "SignallingClient",
                "emitMessage() called with: message = [$message]"
            )
            val obj = JSONObject()
            obj.put("type", message.type.canonicalForm())
            obj.put("sdp", message.description)
            Log.d("emitMessage", obj.toString())
            socket!!.emit("message", obj)
            Log.d("vivek1794", obj.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun emitIceCandidate(iceCandidate: IceCandidate) {
        try {
            val `object` = JSONObject()
            `object`.put("type", "candidate")
            `object`.put("label", iceCandidate.sdpMLineIndex)
            `object`.put("id", iceCandidate.sdpMid)
            `object`.put("candidate", iceCandidate.sdp)
            socket!!.emit("message", `object`)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun close() {
        socket!!.emit("bye", roomName)
        socket!!.disconnect()
        socket!!.close()
    }

    internal interface SignalingInterface {
        fun onRemoteHangUp(msg: String?)
        fun onOfferReceived(data: JSONObject?)
        fun onAnswerReceived(data: JSONObject?)
        fun onIceCandidateReceived(data: JSONObject?)
        fun onTryToStart()
        fun onCreatedRoom()
        fun onJoinedRoom()
        fun onNewPeerJoined()
    }

    companion object {
        private var instance: SignallingClient? = null
        fun getInstance(): SignallingClient? {
            if (instance == null) {
                instance = SignallingClient()
            }
            if (instance!!.roomName == null) {
                //set the room name here
                instance!!.roomName = "vivek17"
            }
            return instance
        }
    }
}