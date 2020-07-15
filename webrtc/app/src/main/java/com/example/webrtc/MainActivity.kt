package com.example.webrtc

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONException
import org.json.JSONObject
import org.webrtc.*
import org.webrtc.PeerConnection.IceServer
import org.webrtc.PeerConnection.RTCConfiguration
import org.webrtc.PeerConnectionFactory.InitializationOptions
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener,
    SignallingClient.SignalingInterface {
    var peerConnectionFactory: PeerConnectionFactory? = null
    var audioConstraints: MediaConstraints? = null
    var videoConstraints: MediaConstraints? = null
    var sdpConstraints: MediaConstraints? = null
    var videoSource: VideoSource? = null
    var localVideoTrack: VideoTrack? = null
    var audioSource: AudioSource? = null
    var localAudioTrack: AudioTrack? = null
    var surfaceTextureHelper: SurfaceTextureHelper? = null
    var localVideoView: SurfaceViewRenderer? = null
    var remoteVideoView: SurfaceViewRenderer? = null
    var hangup: Button? = null
    var localPeer: PeerConnection? = null
    var iceServers: List<IceServer>? = null
    var rootEglBase: EglBase? = null
    var gotUserMedia = false
    var peerIceServers: MutableList<IceServer> = ArrayList()
    val ALL_PERMISSIONS_CODE = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !== PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) !== PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO), ALL_PERMISSIONS_CODE)
        } else {
            // all permissions already granted
            start()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ALL_PERMISSIONS_CODE && grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {
            // all permissions granted
            start()
        } else {
            finish()
        }
    }

    private fun initViews() {
        val hangup: Button = findViewById(R.id.end_call)
        localVideoView = findViewById(R.id.local_gl_surface_view)
        remoteVideoView = findViewById(R.id.remote_gl_surface_view)
        hangup.setOnClickListener(this)
    }

    private fun initVideos() {
        val rootEglBase: EglBase = EglBase.create()
        localVideoView!!.init(rootEglBase.eglBaseContext, null)
        remoteVideoView!!.init(rootEglBase.eglBaseContext, null)
        localVideoView!!.setZOrderMediaOverlay(true)
        remoteVideoView!!.setZOrderMediaOverlay(true)
    }

    private fun getIceServers() {
        //get Ice servers using xirsys
//        List<PeerConnection.IceServer> iceServers = null;
//        iceServers.add(new PeerConnection.IceServer("stun:stun1.l.google.com:19302"));
//        iceServers.add(new PeerConnection.IceServer("stun:stun2.l.google.com:19302"));
//        iceServers.add(new PeerConnection.IceServer("stun:stun3.l.google.com:19302"));
//        iceServers.add(new PeerConnection.IceServer("stun:stun4.l.google.com:19302"));
        val peerIceServer =
            IceServer.builder("stun:stun1.l.google.com:19302").createIceServer()
        peerIceServers.add(peerIceServer)
        //        byte[] data = new byte[0];
//        try {
//            data = ("<xirsys_ident>:<xirsys_secret>").getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        String authToken = "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
//        Utils.getInstance().getRetrofitInstance().getIceCandidates(authToken).enqueue(new Callback<TurnServerPojo>() {
//            @Override
//            public void onResponse(@NonNull Call<TurnServerPojo> call, @NonNull Response<TurnServerPojo> response) {
//                TurnServerPojo body = response.body();
//                if (body != null) {
//                    iceServers = body.iceServerList.iceServers;
//                }
//                for (IceServer iceServer : iceServers) {
//                    if (iceServer.credential == null) {
//                        PeerConnection.IceServer peerIceServer = PeerConnection.IceServer.builder(iceServer.url).createIceServer();
//                        peerIceServers.add(peerIceServer);
//                    } else {
//                        PeerConnection.IceServer peerIceServer = PeerConnection.IceServer.builder(iceServer.url)
//                                .setUsername(iceServer.username)
//                                .setPassword(iceServer.credential)
//                                .createIceServer();
//                        peerIceServers.add(peerIceServer);
//                    }
//                }
//                Log.d("onApiResponse", "IceServers\n" + iceServers.toString());
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<TurnServerPojo> call, @NonNull Throwable t) {
//                t.printStackTrace();
//            }
//        });
    }

    fun start() {
        // keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initViews()
        initVideos()
        getIceServers()
        SignallingClient.getInstance()!!.init(this)

        //Initialize PeerConnectionFactory globals.
        val initializationOptions =
            InitializationOptions.builder(this)
                .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        //Create a new PeerConnectionFactory instance - using Hardware encoder and decoder.
        val options = PeerConnectionFactory.Options()
        val defaultVideoEncoderFactory = DefaultVideoEncoderFactory(
            rootEglBase!!.eglBaseContext,  /* enableIntelVp8Encoder */
            true,  /* enableH264HighProfile */
            true
        )
        val defaultVideoDecoderFactory =
            DefaultVideoDecoderFactory(rootEglBase!!.eglBaseContext)
        val peerConnectionFactory: PeerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(defaultVideoEncoderFactory)
            .setVideoDecoderFactory(defaultVideoDecoderFactory)
            .createPeerConnectionFactory()

        //Now create a VideoCapturer instance.
        val videoCapturerAndroid: VideoCapturer? = createCameraCapturer(Camera1Enumerator(false))

        //Create MediaConstraints - Will be useful for specifying video and audio constraints.
        audioConstraints = MediaConstraints()
        videoConstraints = MediaConstraints()

        //Create a VideoSource instance
        if (videoCapturerAndroid != null) {
            surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", rootEglBase!!.eglBaseContext)
            val videoSource: VideoSource = peerConnectionFactory.createVideoSource(videoCapturerAndroid.isScreencast)
            videoCapturerAndroid.initialize(
                surfaceTextureHelper,
                this,
                videoSource.capturerObserver
            )
        }
        val localVideoTrack: VideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource)

        //create an AudioSource instance
        audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource)
        videoCapturerAndroid?.startCapture(1024, 720, 30)
        localVideoView!!.visibility = View.VISIBLE
        // And finally, with our VideoRenderer ready, we
        // can add our renderer to the VideoTrack.
        localVideoTrack.addSink(localVideoView)
        localVideoView!!.setMirror(true)
        remoteVideoView!!.setMirror(true)
        gotUserMedia = true
        if (SignallingClient.getInstance()!!.isInitiator) {
            onTryToStart()
        }
    }

    /**
     * This method will be called directly by the app when it is the initiator and has got the local media
     * or when the remote peer sends a message through socket that it is ready to transmit AV data
     */
    override fun onTryToStart() {
        runOnUiThread {
            if (!SignallingClient.getInstance()!!.isStarted && localVideoTrack != null && SignallingClient.getInstance()!!.isChannelReady) {
                createPeerConnection()
                SignallingClient.getInstance()!!.isStarted = true
                if (SignallingClient.getInstance()!!.isInitiator) {
                    doCall()
                }
            }
        }
    }

    /**
     * Creating the local peerconnection instance
     */
    private fun createPeerConnection() {
        val rtcConfig = RTCConfiguration(peerIceServers)
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE
        rtcConfig.continualGatheringPolicy =
            PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        // Use ECDSA encryption.
        rtcConfig.keyType = PeerConnection.KeyType.ECDSA
        localPeer = peerConnectionFactory!!.createPeerConnection(
            rtcConfig,
            object : CustomPeerConnectionObserver("localPeerCreation") {
                override fun onIceCandidate(iceCandidate: IceCandidate) {
                    super.onIceCandidate(iceCandidate)
                    onIceCandidateReceived(iceCandidate)
                }

                override fun onAddStream(mediaStream: MediaStream) {
                    showToast("Received Remote stream")
                    super.onAddStream(mediaStream)
                    gotRemoteStream(mediaStream)
                }
            })
        addStreamToLocalPeer()
    }

    /**
     * Adding the stream to the localpeer
     */
    private fun addStreamToLocalPeer() {
        //creating local mediastream
        val stream = peerConnectionFactory!!.createLocalMediaStream("102")
        stream.addTrack(localAudioTrack)
        stream.addTrack(localVideoTrack)
        localPeer!!.addStream(stream)
    }

    /**
     * This method is called when the app is the initiator - We generate the offer and send it over through socket
     * to remote peer
     */
    private fun doCall() {
        sdpConstraints = MediaConstraints()
        sdpConstraints!!.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true")
        )
        sdpConstraints!!.mandatory.add(
            MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true")
        )
        localPeer!!.createOffer(object : CustomSdpObserver("localCreateOffer") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                localPeer!!.setLocalDescription(
                    CustomSdpObserver("localSetLocalDesc"),
                    sessionDescription
                )
                Log.d("onCreateSuccess", "SignallingClient emit ")
                SignallingClient.getInstance()!!.emitMessage(sessionDescription)
            }
        }, sdpConstraints)
    }

    /**
     * Received remote peer's media stream. we will get the first video track and render it
     */
    private fun gotRemoteStream(stream: MediaStream) {
        //we have remote video stream. add to the renderer.
        val videoTrack = stream.videoTracks[0]
        runOnUiThread {
            try {
                remoteVideoView!!.visibility = View.VISIBLE
                videoTrack.addSink(remoteVideoView)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Received local ice candidate. Send it to remote peer through signalling for negotiation
     */
    fun onIceCandidateReceived(iceCandidate: IceCandidate?) {
        //we have received ice candidate. We can set it to the other peer.
        SignallingClient.getInstance()!!.emitIceCandidate(iceCandidate!!)
    }

    /**
     * SignallingCallback - called when the room is created - i.e. you are the initiator
     */
    override fun onCreatedRoom() {
        showToast("You created the room $gotUserMedia")
        //        if (gotUserMedia) {
        SignallingClient.getInstance()!!.emitMessage("5f05baa520d4310017ebc9bd")
        //        }
    }

    /**
     * SignallingCallback - called when you join the room - you are a participant
     */
    override fun onJoinedRoom() {
        showToast("You joined the room $gotUserMedia")
        //        if (gotUserMedia) {
        SignallingClient.getInstance()!!.emitMessage("5f05baa520d4310017ebc9bd")
        //        }
    }

    override fun onNewPeerJoined() {
        showToast("Remote Peer Joined")
    }

    override fun onRemoteHangUp(msg: String?) {
        showToast("Remote Peer hungup")
        runOnUiThread { hangup() }
    }

    /**
     * SignallingCallback - Called when remote peer sends offer
     */
    override fun onOfferReceived(data: JSONObject?) {
        showToast("Received Offer")
        runOnUiThread {
            if (!SignallingClient.getInstance()!!.isInitiator && !SignallingClient.getInstance()!!.isStarted) {
                onTryToStart()
            }
            try {
                if (data != null) {
                    localPeer!!.setRemoteDescription(
                        CustomSdpObserver("localSetRemote"),
                        SessionDescription(SessionDescription.Type.OFFER, data.getString("sdp"))
                    )
                }
                doAnswer()
                updateVideoViews(true)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    private fun doAnswer() {
        localPeer!!.createAnswer(object : CustomSdpObserver("localCreateAns") {
            override fun onCreateSuccess(sessionDescription: SessionDescription) {
                super.onCreateSuccess(sessionDescription)
                localPeer!!.setLocalDescription(
                    CustomSdpObserver("localSetLocal"),
                    sessionDescription
                )
                SignallingClient.getInstance()!!.emitMessage(sessionDescription)
            }
        }, MediaConstraints())
    }

    /**
     * SignallingCallback - Called when remote peer sends answer to your offer
     */
    override fun onAnswerReceived(data: JSONObject?) {
        showToast("Received Answer")
        try {
            if (data != null) {
                localPeer!!.setRemoteDescription(
                    CustomSdpObserver("localSetRemote"),
                    SessionDescription(
                        SessionDescription.Type.fromCanonicalForm(
                            data.getString("type").toLowerCase(Locale.ROOT)
                        ), data.getString("sdp")
                    )
                )
            }
            updateVideoViews(true)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * Remote IceCandidate received
     */
    override fun onIceCandidateReceived(data: JSONObject?) {
        try {
            if (data != null) {
                localPeer!!.addIceCandidate(
                    IceCandidate(
                        data.getString("id"),
                        data.getInt("label"),
                        data.getString("candidate")
                    )
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun updateVideoViews(remoteVisible: Boolean) {
        runOnUiThread {
            var params = localVideoView!!.layoutParams
            if (remoteVisible) {
                params.height = dpToPx(100)
                params.width = dpToPx(100)
            } else {
                params = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            localVideoView!!.layoutParams = params
        }
    }

    /**
     * Closing up - normal hangup and app destroye
     */
    override fun onClick(v: View) {
        when (v.id) {
            R.id.end_call -> {
                hangup()
            }
        }
    }

    private fun hangup() {
        try {
            if (localPeer != null) {
                localPeer!!.close()
            }
            localPeer = null
            SignallingClient.getInstance()!!.close()
            updateVideoViews(false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        SignallingClient.getInstance()!!.close()
        super.onDestroy()
        if (surfaceTextureHelper != null) {
            surfaceTextureHelper!!.dispose()
            surfaceTextureHelper = null
        }
    }

    /**
     * Util Methods
     */
    fun dpToPx(dp: Int): Int {
        val displayMetrics = resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    fun showToast(msg: String?) {
        runOnUiThread { Toast.makeText(this@MainActivity, msg, Toast.LENGTH_SHORT).show() }
    }

    private fun createCameraCapturer(enumerator: CameraEnumerator): VideoCapturer? {
        val deviceNames = enumerator.deviceNames

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.")
        for (deviceName in deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(
                    TAG,
                    "Creating front facing camera capturer."
                )
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.")
        for (deviceName in deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.")
                val videoCapturer: VideoCapturer? = enumerator.createCapturer(deviceName, null)
                if (videoCapturer != null) {
                    return videoCapturer
                }
            }
        }
        return null
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
