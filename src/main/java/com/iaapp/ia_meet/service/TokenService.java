package com.iaapp.ia_meet.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
// import com.zego.zegoserverassistant.ZegoServerAssistant; (désactivé pour build)

@Service
public class TokenService {

    private static final long APP_ID = 1043316041L; 
    private static final String APP_SECRET = "cd906282d7b6075789d36c310b58e7c0";

    public String generateToken(String roomId, String userId) {

        long effectiveTimeInSeconds = 3600; // 1 heure
        int payload = 0;

        // Simulation de token sécurisé pour le build
        // En production, réactiver Zego après avoir installé le JAR manuellement
        return "token_ia_meet_" + UUID.randomUUID().toString().substring(0, 16);
    }
}