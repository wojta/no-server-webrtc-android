# serverless-webrtc-android
A demo of using WebRTC with no signaling server. But for Android written in Kotlin.
Compatibile and inspired by this project written for JavaScript:
* http://blog.printf.net/articles/2013/05/17/webrtc-without-a-signaling-server/
* http://blog.printf.net/articles/2014/07/01/serverless-webrtc-continued
* https://cjb.github.io/serverless-webrtc/serverless-webrtc.html

# What is WebRTC?
<img src="https://webrtc.org/assets/images/webrtc-logo-vert-retro-255x305.png" width="96">
It's technology for real time peer to peer comunication. Especially useful for transfering audio and video - teleconference apps, but can be used for ordinary data as in this example.
WebRTC is supported in recent Chrome browser, Node.js and also on Android/iOS.

# How it works?
WebRTC requires two data payloads to be transfered between parties, it's called [*SDP*](https://en.wikipedia.org/wiki/Session_Description_Protocol) (sesssion description protocol). One is called *offer* and the second is *answer*.
You can either create offer and send it to other party or wait for offer to be delivered to you.
Usually SDP handshakes are done by special signalling server, but in this case we are not using any, so you'll need to pass SDPs manually by e.g. e-mail.

If it's running IPv4, it's very unlikely that both parties will have public IP address or it will be in the same network.
SDP requires you'll need to pass external IP address there, this is done automatically by process called ICE gathering. It uses two types of external servers - STUN and TURN.
We are using only STUN here, but it should work with TURN as well (and even better).
It can even punch through some NAT mechanisms.

# Usage







