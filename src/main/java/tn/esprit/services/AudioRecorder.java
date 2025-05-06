package tn.esprit.services;

import javafx.stage.FileChooser;

import javax.sound.sampled.*;
import java.io.*;
import java.util.UUID;

public class AudioRecorder {

    private TargetDataLine microphone;
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
    private AudioFormat format;
    private File audioFile;

    public AudioRecorder() {
        format = new AudioFormat(16000, 16, 1, true, true);
    }

    public void startRecording(String filename) {
        try {
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("Line not supported");
                return;
            }
            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open(format);
            microphone.start();

            audioFile = new File("voice_comments/" + filename + ".wav");
            Thread thread = new Thread(() -> {
                try (AudioInputStream ais = new AudioInputStream(microphone)) {
                    AudioSystem.write(ais, fileType, audioFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (microphone != null) {
            microphone.stop();
            microphone.close();
        }
    }

    public File getAudioFile() {
        return audioFile;
    }
}

