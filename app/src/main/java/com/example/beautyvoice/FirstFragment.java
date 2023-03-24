package com.example.beautyvoice;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.beautyvoice.databinding.FragmentFirstBinding;

import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.*;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String speechKey = "Please change to your key";
        String speechRegion = "Please change to your Region";

        SpeechConfig speechConfig = SpeechConfig.fromSubscription(speechKey, speechRegion);

        speechConfig.setSpeechSynthesisVoiceName("en-US-JennyNeural");

        SpeechSynthesizer speechSynthesizer = new SpeechSynthesizer(speechConfig);

        String text = "BeautyVoice ready, what can I do for you?";

        SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();

        if (speechSynthesisResult.getReason() == ResultReason.SynthesizingAudioCompleted) {
            int resID = getResources().getIdentifier("sound", "raw", speechSynthesisResult);
            MediaPlayer mediaPlayer = MediaPlayer.create(this, resID);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                    MediaRecorder recorder;
                    String outputFile;

                    recorder = new MediaRecorder();
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/record.3gp";
                    recorder.setOutputFile(outputFile);

                    recorder.start();

                    recorder.stop();
                    recorder.release();

                    AudioConfig audioConfig = AudioConfig.fromWavFileInput(outputFile);
                    SpeechRecognizer speechRecognizer = new SpeechRecognizer(speechConfig, audioConfig);
                    Future<SpeechRecognitionResult> task = speechRecognizer.recognizeOnceAsync();

                    speechRecognizer.recognized.addEventListener((s, e) -> {
                        if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                            System.out.println("RECOGNIZED: Text=" + e.getResult().getText());
                            // referrence:https://apicoding.com/chatgpt-java-api/
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("https://api.openai.com/v1/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            String text = retrofit.generateText();
                            SpeechSynthesisResult speechSynthesisResult = speechSynthesizer.SpeakTextAsync(text).get();
                        }
                        else if (e.getResult().getReason() == ResultReason.NoMatch) {
                            System.out.println("NOMATCH: Speech could not be recognized.");
                        }
                    });
                }
            });

            mediaPlayer.start();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}