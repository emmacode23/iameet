package com.iaapp.ia_meet.service;

import com.google.cloud.speech.v1.*;
import com.google.cloud.texttospeech.v1.*;
import com.google.cloud.translate.*;
import com.google.protobuf.ByteString;
import org.springframework.stereotype.Service;
import java.util.Base64;

@Service
public class TranslationService {

    public String speechToText(byte[] audioBytes, String sourceLang) throws Exception {
        try (SpeechClient speechClient = SpeechClient.create()) {
            RecognitionConfig config = RecognitionConfig.newBuilder()
                    .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                    .setLanguageCode(sourceLang)
                    .setSampleRateHertz(16000)
                    .build();
            RecognitionAudio audio = RecognitionAudio.newBuilder()
                    .setContent(ByteString.copyFrom(audioBytes))
                    .build();
            RecognizeResponse response = speechClient.recognize(config, audio);
            return response.getResultsList().stream()
                    .flatMap(r -> r.getAlternativesList().stream())
                    .map(SpeechRecognitionAlternative::getTranscript)
                    .reduce("", (a,b) -> a + " " + b).trim();
        }
    }

    public String translateText(String text, String targetLang) {
        if (text == null || text.isEmpty()) return "";
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translation translation = translate.translate(text, Translate.TranslateOption.targetLanguage(targetLang));
        return translation.getTranslatedText();
    }

    public String textToSpeech(String text, String targetLang) throws Exception {
        try (TextToSpeechClient ttsClient = TextToSpeechClient.create()) {
            SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();
            VoiceSelectionParams voice = VoiceSelectionParams.newBuilder()
                    .setLanguageCode(targetLang)
                    .setSsmlGender(SsmlVoiceGender.NEUTRAL)
                    .build();
            AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16).build();
            SynthesizeSpeechResponse response = ttsClient.synthesizeSpeech(input, voice, audioConfig);
            return Base64.getEncoder().encodeToString(response.getAudioContent().toByteArray());
        }
    }

    public String translateAudio(byte[] audioBytes, String sourceLang, String targetLang) throws Exception {
        String text = speechToText(audioBytes, sourceLang);
        if (text.isEmpty()) return "";
        String translated = translateText(text, targetLang);
        return textToSpeech(translated, targetLang);
    }
}