package com.iaapp.ia_meet.controller;

import com.iaapp.ia_meet.service.TranslationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Base64;

@RestController
@RequestMapping("/api/translate")
public class TranslationController {

    private final TranslationService translationService;

    public TranslationController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @PostMapping("/audio")
    public ResponseEntity<Map<String, String>> translateAudio(@RequestBody Map<String, String> payload) throws Exception {
        String audioBase64 = payload.get("audio");
        String sourceLang = payload.get("sourceLang");
        String targetLang = payload.get("targetLang");

        byte[] audioBytes = Base64.getDecoder().decode(audioBase64);
        String text = translationService.speechToText(audioBytes, sourceLang);
        
        if (text.isEmpty()) {
            return ResponseEntity.ok(Map.of("original", "", "translated", "", "audio", ""));
        }

        String translated = translationService.translateText(text, targetLang);
        String translatedAudio = translationService.textToSpeech(translated, targetLang);

        return ResponseEntity.ok(Map.of(
                "original", text,
                "translated", translated,
                "audio", translatedAudio
        ));
    }
}