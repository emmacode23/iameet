let localStream;
let peerConnections = {};
let stompClient = null;
const meetCode = document.getElementById('meet-code')?.value;
const userEmail = document.getElementById('user-email')?.value;
const userName = document.getElementById('user-name')?.value;
const videoGrid = document.getElementById('video-grid');
let myLang ;


const config = {
    iceServers: [{ urls: 'stun:stun.l.google.com:19302' }]
};

async function init() {
    try {
        localStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
        document.getElementById('localVideo').srcObject = localStream;
        connectWebSocket();
        startSpeechRecognition();
    } catch (e) {
        console.error('Erreur accès média:', e);
    }
}

function connectWebSocket() {
    const socket = new SockJS('/ws/meet');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, () => {
        stompClient.subscribe(`/topic/meet/${meetCode}/signal`, (message) => {
            const signal = JSON.parse(message.body);
            if (signal.sender !== userEmail) {
                handleSignal(signal);
            }
        });

        stompClient.subscribe(`/topic/meet/${meetCode}/chat`, (message) => {
            const chatMsg = JSON.parse(message.body);

            displayMessage(chatMsg);
        });

        stompClient.send(`/topic/meet/${meetCode}/signal`, {}, JSON.stringify({
            type: 'join',
            sender: userEmail,
            name: userName
        }));
    });
}

async function handleSignal(signal) {
    const { type, sender, name, sdp, candidate } = signal;

    if (type === 'join') {
        createPeerConnection(sender, name, true);
    } else if (type === 'offer') {
        const pc = createPeerConnection(sender, name, false);
        await pc.setRemoteDescription(new RTCSessionDescription(sdp));
        const answer = await pc.createAnswer();
        await pc.setLocalDescription(answer);
        stompClient.send(`/topic/meet/${meetCode}/signal`, {}, JSON.stringify({
            type: 'answer',
            sender: userEmail,
            sdp: pc.localDescription,
            receiver: sender
        }));
    } else if (type === 'answer' && signal.receiver === userEmail) {
        await peerConnections[sender].setRemoteDescription(new RTCSessionDescription(sdp));
    } else if (type === 'candidate' && signal.receiver === userEmail) {
        await peerConnections[sender].addIceCandidate(new RTCIceCandidate(candidate));
    }
}

function createPeerConnection(peerEmail, peerName, isOffer) {
    const pc = new RTCPeerConnection(config);
    peerConnections[peerEmail] = pc;

    localStream.getTracks().forEach(track => pc.addTrack(track, localStream));

    pc.onicecandidate = (event) => {
        if (event.candidate) {
            stompClient.send(`/topic/meet/${meetCode}/signal`, {}, JSON.stringify({
                type: 'candidate',
                sender: userEmail,
                candidate: event.candidate,
                receiver: peerEmail
            }));
        }
    };

    pc.ontrack = (event) => {
        if (!document.getElementById(`video-${peerEmail}`)) {
            const container = document.createElement('div');
            container.className = 'video-container';
            container.id = `container-${peerEmail}`;
            
            const video = document.createElement('video');
            video.id = `video-${peerEmail}`;
            video.autoplay = true;
            video.playsinline = true;
            video.srcObject = event.streams[0];
            
            const label = document.createElement('div');
            label.className = 'video-label';
            label.innerText = peerName;
            
            container.appendChild(video);
            container.appendChild(label);
            videoGrid.appendChild(container);
        }
    };

    if (isOffer) {
        pc.onnegotiationneeded = async () => {
            const offer = await pc.createOffer();
            await pc.setLocalDescription(offer);
            stompClient.send(`/topic/meet/${meetCode}/signal`, {}, JSON.stringify({
                type: 'offer',
                sender: userEmail,
                name: userName,
                sdp: pc.localDescription,
                receiver: peerEmail
            }));
        };
    }

    return pc;
}

function sendMessage() {
    const input = document.getElementById('message-input');
    const content = input.value.trim();
    if (content && stompClient) {
        const targetLang = myLang === 'fr-FR' ? 'en' : 'fr';
        stompClient.send(`/app/meet/${meetCode}/chat`, {}, JSON.stringify({
            content: content,
            userEmail: userEmail,
            sourceLang: myLang.split('-')[0],
            targetLang: targetLang
        }));
        input.value = '';
    }
}

function displayMessage(msg) {
    const chatMessages = document.getElementById('chat-messages');
    const isMe = msg.user.email === userEmail;
    
    const div = document.createElement('div');
    div.className = `message ${isMe ? 'sent' : 'received'}`;
    
    let html = `<div class="msg-user">${msg.user.name}</div><div>${msg.content}</div>`;
    if (msg.translatedContent && msg.translatedContent !== msg.content) {
        html += `<div class="msg-trans">${msg.translatedContent}</div>`;
        if (!isMe) speakText(msg.translatedContent);
    }
    
    div.innerHTML = html;
    chatMessages.appendChild(div);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function speakText(text) {
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = myLang;
    window.speechSynthesis.speak(utterance);
}

function startSpeechRecognition() {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (!SpeechRecognition) return;

    const recognition = new SpeechRecognition();
    recognition.continuous = true;
    recognition.interimResults = false;
    recognition.lang = myLang;

    recognition.onresult = (event) => {
        const transcript = event.results[event.results.length - 1][0].transcript;
        if (transcript.trim()) {
            const targetLang = myLang === 'fr-FR' ? 'en' : 'fr';
            stompClient.send(`/app/meet/${meetCode}/chat`, {}, JSON.stringify({
                content: transcript,
                userEmail: userEmail,
                sourceLang: myLang.split('-')[0],
                targetLang: targetLang
            }));
        }
    };

    recognition.start();
}

async function  toggleMic() {
try {
    const audioStream=await navigator.mediaDevices.getUserMedia({ audio: true });
    const audioTrack = localStream.getAudioTracks()[0];
    audioTrack.enabled = !audioTrack.enabled;
    // document.getElementById('toggle-mic').classList.toggle('active', !audioTrack.enabled);
        document.getElementById('toggle-mic').srcObject = audioStream;
} catch (e) {
    console.error('Erreur accès micro:', e);
}

}

async function  toggleCam() {
try {
 const videoStream=await navigator.mediaDevices.getUserMedia({ video: true });
    const videoTrack = localStream.getVideoTracks()[0];
    videoTrack.enabled = !videoTrack.enabled;
    // document.getElementById('toggle-cam').classList.toggle('active', !videoTrack.enabled);
        document.getElementById('toggle-cam').srcObject = videoStream;
} catch (e) {
    console.error('Erreur accès caméra:', e);
}
}

async function toggleScreenShare() {
    try {
        const screenStream = await navigator.mediaDevices.getDisplayMedia({ video: true });
        const screenTrack = screenStream.getVideoTracks()[0];
        
        for (let email in peerConnections) {
            const sender = peerConnections[email].getSenders().find(s => s.track.kind === 'video');
            sender.replaceTrack(screenTrack);
        }
        
        document.getElementById('localVideo').srcObject = screenStream;
        
        screenTrack.onended = () => {
            stopScreenShare();
        };
    } catch (e) {
        console.error('Erreur partage écran:', e);
    }
}

function stopScreenShare() {
    const videoTrack = localStream.getVideoTracks()[0];
    for (let email in peerConnections) {
        const sender = peerConnections[email].getSenders().find(s => s.track.kind === 'video');
        sender.replaceTrack(videoTrack);
    }
    document.getElementById('localVideo').srcObject = localStream;
}

function toggleChat() {
    const sidebar = document.getElementById('chat-sidebar');
    sidebar.style.display = sidebar.style.display === 'flex' ? 'none' : 'flex';
}

function updateLang() {
    myLang = document.getElementById('my-lang').value;
     
    
}

function copyLink() {
    const link = document.getElementById('meet-link').innerText;
    navigator.clipboard.writeText(link).then(() => {
        // alert('Lien copié !');
    });
}

function leaveMeet() {
    window.location.href = '/dashboard';
}

if (meetCode) init();
