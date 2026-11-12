package com.gitasaar.reader;

import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Device TTS player. Audio starts on one tap; no voice-picker or getVoices() calls.
 * If TTS initialization is pending, the requested chapter plays automatically
 * when the engine becomes ready.
 */
public final class GitaAudioPlayer {
    public interface Listener { void onStatus(String status, boolean playing); }

    private static final Locale HINDI = new Locale("hi", "IN");
    private final Activity activity;
    private final Listener listener;
    private final ArrayList<Segment> segments = new ArrayList<>();
    private TextToSpeech speech;
    private int position = 0, session = 0;
    private boolean ready = false, playing = false, pendingStart = false, closed = false;
    private String status = "Audio ready to play";

    private static final class Segment {
        final String text, label;
        final Locale locale;
        Segment(String text, String label, Locale locale) {
            this.text = text; this.label = label; this.locale = locale;
        }
    }

    public GitaAudioPlayer(Activity a, Listener callback) {
        activity = a; listener = callback;
        try {
            speech = new TextToSpeech(a, result -> a.runOnUiThread(() -> {
                if (closed) return;
                ready = result == TextToSpeech.SUCCESS && speech != null;
                if (ready) {
                    try {
                        speech.setSpeechRate(0.90f);
                        speech.setPitch(1.0f);
                        speech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override public void onStart(String id) { }
                            @Override public void onDone(String id) { advance(id); }
                            @Override public void onError(String id) { advance(id); }
                        });
                    } catch (RuntimeException ex) {
                        ready = false;
                    }
                }
                if (ready && pendingStart && !segments.isEmpty()) {
                    pendingStart = false;
                    playing = true;
                    speakCurrent();
                } else if (!ready) {
                    playing = false; pendingStart = false;
                    notifyUser("Speech engine unavailable. Install Hindi Text-to-Speech in your phone settings.");
                } else {
                    notifyUser("Audio ready to play");
                }
            }));
        } catch (RuntimeException ex) {
            speech = null; ready = false;
            notifyUser("Speech service not available on this phone");
        }
    }

    private void notifyUser(String message) {
        status = message;
        activity.runOnUiThread(() -> {
            if (!closed && !activity.isFinishing() && !activity.isDestroyed()) {
                listener.onStatus(status, playing);
            }
        });
    }

    private void advance(String spokenId) {
        activity.runOnUiThread(() -> {
            if (closed || !playing || !spokenId.equals("gita_" + session + "_" + position)) return;
            position++;
            if (position >= segments.size()) {
                playing = false;
                pendingStart = false;
                segments.clear(); position = 0;
                notifyUser("Finished listening");
            } else {
                speakCurrent();
            }
        });
    }

    public static String cleanForSpeech(String input) {
        if (input == null) return "";
        String clean = input.replace((char) 160, ' ').trim();
        // Strip prefixed source verse markers e.g. ॥1.1॥ and ।।2.47।।
        clean = clean.replaceAll("^[\\s\\u0964\\u0965|।:;()]*[0-9\\u0966-\\u096f]+\\s*[.\\u0964\\u0965:,-]\\s*[0-9\\u0966-\\u096f]+[\\s\\u0964\\u0965|।:;()]*", "");
        // Strip trailing Sanskrit verse marker, including ||१||, but keep narrative numbers.
        clean = clean.replaceAll("(?U)(?:[\\s|।\\u0965]+)[0-9\\u0966-\\u096f]+(?:\\s*[.:-]\\s*[0-9\\u0966-\\u096f]+)?[\\s|।\\u0965]*$", "");
        clean = clean.replaceAll("[|\\u0964\\u0965]{2,}", "।").replace('|', '।');
        return clean.replaceAll("\\s+", " ").trim();
    }

    private void addVerse(GitaVerse v, boolean meaning, boolean english) {
        String scripture = cleanForSpeech(v.sanskrit);
        if (!scripture.isEmpty()) {
            segments.add(new Segment(scripture, "Chapter " + v.chapter + " • Verse " + v.number, HINDI));
        }
        if (meaning) {
            String explanation = cleanForSpeech(v.meaning(english));
            if (!explanation.isEmpty()) segments.add(new Segment(
                    explanation, "Meaning " + v.chapter + ":" + v.number,
                    english ? Locale.ENGLISH : HINDI
            ));
        }
    }

    public boolean playVerse(GitaVerse verse, boolean meaning, boolean english) {
        if (verse == null || closed || speech == null) return false;
        stop();
        addVerse(verse, meaning, english);
        return start();
    }

    public boolean playChapter(List<GitaVerse> verses, boolean meaning, boolean english) {
        if (verses == null || verses.isEmpty() || closed || speech == null) return false;
        stop();
        for (GitaVerse verse : verses) addVerse(verse, meaning, english);
        return start();
    }

    private boolean start() {
        if (segments.isEmpty()) return false;
        if (!ready) {
            playing = false;
            pendingStart = true;
            notifyUser("Preparing audio…");
            return true;
        }
        pendingStart = false;
        playing = true;
        speakCurrent();
        return true;
    }

    private void speakCurrent() {
        if (closed || !ready || speech == null || !playing || position >= segments.size()) return;
        Segment current = segments.get(position);
        try {
            int language = speech.setLanguage(current.locale);
            if (language == TextToSpeech.LANG_NOT_SUPPORTED || language == TextToSpeech.LANG_MISSING_DATA) {
                playing = false;
                notifyUser("Please install a " + (current.locale.getLanguage().equals("en") ? "English" : "Hindi") + " TTS voice on your phone.");
                return;
            }
            int response = speech.speak(current.text, TextToSpeech.QUEUE_FLUSH, null,
                    "gita_" + session + "_" + position);
            if (response == TextToSpeech.ERROR) {
                playing = false;
                notifyUser("Could not start audio. Please check the phone's speech engine.");
                return;
            }
            notifyUser("▶ " + current.label + " (" + (position + 1) + "/" + segments.size() + ")");
        } catch (RuntimeException error) {
            playing = false;
            notifyUser("Audio could not start on this device.");
        }
    }

    public boolean isPlaying() { return playing || pendingStart; }
    public boolean hasPausedItem() { return !playing && !pendingStart && !segments.isEmpty(); }
    public String getStatus() { return status; }

    public void pause() {
        if (!playing && !pendingStart) return;
        playing = false; pendingStart = false; session++;
        try { if (speech != null) speech.stop(); } catch (RuntimeException ignored) { }
        notifyUser("Paused");
    }

    public void resume() {
        if (closed || playing || pendingStart || segments.isEmpty()) return;
        session++;
        start();
    }

    public void stop() {
        playing = false; pendingStart = false; session++; position = 0; segments.clear();
        try { if (speech != null) speech.stop(); } catch (RuntimeException ignored) { }
        notifyUser("Stopped");
    }

    public void close() {
        if (closed) return;
        stop();
        closed = true;
        try { if (speech != null) speech.shutdown(); } catch (RuntimeException ignored) { }
        speech = null; ready = false;
    }
}
