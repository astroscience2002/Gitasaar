package com.gitasaar.reader;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Simple, independently managed chapter/verse audio.
 * The engine is created ONLY when playback is requested.
 * No regex, voice picker, or heavyweight audio screen dependencies.
 */
public final class GitaAudioPlayer {
    public interface Listener {
        void onStatus(String message, boolean playing);
    }

    private static final Locale HINDI = new Locale("hi", "IN");
    private final Activity activity;
    private final Listener listener;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ArrayList<Part> playlist = new ArrayList<>();

    private TextToSpeech engine;
    private int cursor = 0;
    private long generation = 0;
    private boolean initializing = false;
    private boolean ready = false;
    private boolean playing = false;
    private boolean waiting = false;
    private boolean closed = false;
    private String status = "सुनने के लिए अध्याय चुनें";

    private static final class Part {
        final String content;
        final String label;
        final Locale language;
        Part(String content, String label, Locale language) {
            this.content = content;
            this.label = label;
            this.language = language;
        }
    }

    public GitaAudioPlayer(Activity activity, Listener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    /**
     * Remove only verse-number markers, without java.util.regex.Pattern.
     * Compatible with Android ICU versions that reject Java's (?U) regexp flag.
     *
     * Examples:
     * "।।1.1।।धृतराष्ट्र ने कहा" -> "धृतराष्ट्र ने कहा"
     * "धर्मक्षेत्रे ... ||१||" -> "धर्मक्षेत्रे ..."
     */
    public static String cleanForSpeech(String input) {
        if (input == null) return "";
        String value = input.replace('\u00a0', ' ').trim();

        int i = 0;
        while (i < value.length() && isVerseFence(value.charAt(i))) i++;
        int firstStart = i;
        while (i < value.length() && isNumeral(value.charAt(i))) i++;
        if (i > firstStart && i < value.length() && isVerseSeparator(value.charAt(i))) {
            i++;
            while (i < value.length() && isVerseFence(value.charAt(i))) i++;
            int secondStart = i;
            while (i < value.length() && isNumeral(value.charAt(i))) i++;
            if (i > secondStart) {
                while (i < value.length() && isVerseFence(value.charAt(i))) i++;
                value = value.substring(i).trim();
            }
        }

        // Remove terminal shloka numbers such as ॥१॥ / ||1||.
        int end = value.length();
        int pos = end;
        while (pos > 0 && isVerseFence(value.charAt(pos - 1))) pos--;
        int numeralEnd = pos;
        while (pos > 0 && isNumeral(value.charAt(pos - 1))) pos--;
        if (pos < numeralEnd && pos > 0 && isVerseFence(value.charAt(pos - 1))) {
            while (pos > 0 && isVerseFence(value.charAt(pos - 1))) pos--;
            value = value.substring(0, pos).trim();
        }

        StringBuilder result = new StringBuilder(value.length());
        char previous = 0;
        boolean space = false;
        for (int k = 0; k < value.length(); k++) {
            char ch = value.charAt(k);
            if (Character.isWhitespace(ch)) {
                if (result.length() > 0) space = true;
                continue;
            }
            if (ch == '|' || ch == '\u0965') ch = '\u0964';
            if (ch == '\u0964' && previous == '\u0964') continue;
            if (space && result.length() > 0) result.append(' ');
            space = false;
            result.append(ch);
            previous = ch;
        }
        return result.toString().trim();
    }

    private static boolean isNumeral(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= '\u0966' && ch <= '\u096f');
    }

    private static boolean isVerseFence(char ch) {
        return ch == '|' || ch == '\u0964' || ch == '\u0965'
                || Character.isWhitespace(ch);
    }

    private static boolean isVerseSeparator(char ch) {
        return ch == '.' || ch == ':' || ch == '-' || ch == '\u0964' || ch == '\u0965';
    }

    private void appendVerse(GitaVerse verse, boolean meaning, boolean english) {
        String sanskrit = cleanForSpeech(verse.sanskrit);
        if (!sanskrit.isEmpty()) {
            playlist.add(new Part(sanskrit,
                    "अध्याय " + verse.chapter + " • श्लोक " + verse.number, HINDI));
        }
        if (meaning) {
            String explanation = cleanForSpeech(verse.meaning(english));
            if (!explanation.isEmpty()) {
                playlist.add(new Part(explanation,
                        "अर्थ " + verse.chapter + ":" + verse.number,
                        english ? Locale.ENGLISH : HINDI));
            }
        }
    }

    public boolean playVerse(GitaVerse verse, boolean meaning, boolean english) {
        if (closed || verse == null) return false;
        clearCurrent();
        appendVerse(verse, meaning, english);
        return begin();
    }

    public boolean playChapter(List<GitaVerse> verses, boolean meaning, boolean english) {
        if (closed || verses == null || verses.isEmpty()) return false;
        clearCurrent();
        for (GitaVerse verse : verses) {
            if (verse != null) appendVerse(verse, meaning, english);
        }
        return begin();
    }

    private boolean begin() {
        if (closed || playlist.isEmpty()) return false;
        if (!ready || engine == null) {
            waiting = true;
            playing = false;
            publish("ऑडियो तैयार हो रहा है…");
            initializeIfNeeded();
        } else {
            waiting = false;
            playing = true;
            speakPart();
        }
        return true;
    }

    private void initializeIfNeeded() {
        if (initializing || closed) return;
        initializing = true;
        try {
            // Use application context; never retain or display a voice picker.
            engine = new TextToSpeech(activity.getApplicationContext(), outcome ->
                    main.post(() -> onEngineInitialized(outcome)));
        } catch (Throwable failure) {
            initializing = false;
            ready = false;
            engine = null;
            waiting = false;
            playing = false;
            publish("इस फोन पर Text-to-Speech नहीं चल पाया");
        }
    }

    private void onEngineInitialized(int outcome) {
        if (closed) return;
        initializing = false;
        ready = outcome == TextToSpeech.SUCCESS && engine != null;
        if (!ready) {
            waiting = false;
            playing = false;
            publish("Hindi voice उपलब्ध नहीं है। फोन की Text-to-Speech सेटिंग जाँचें।");
            return;
        }
        try {
            engine.setSpeechRate(0.88f);
            engine.setPitch(1.0f);
            engine.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override public void onStart(String id) {}
                @Override public void onDone(String id) { completed(id); }
                @Override public void onError(String id) { failed(id); }
            });
        } catch (Throwable failure) {
            waiting = false;
            playing = false;
            ready = false;
            publish("Voice engine शुरू नहीं हो पाया");
            return;
        }

        if (waiting && !playlist.isEmpty()) {
            waiting = false;
            playing = true;
            speakPart();
        } else {
            publish("ऑडियो तैयार है");
        }
    }

    private void completed(String utteranceId) {
        main.post(() -> {
            if (!playing || closed || !sameUtterance(utteranceId)) return;
            cursor++;
            if (cursor >= playlist.size()) {
                playing = false;
                waiting = false;
                playlist.clear();
                cursor = 0;
                publish("पाठ पूरा हुआ");
            } else {
                speakPart();
            }
        });
    }

    private void failed(String utteranceId) {
        main.post(() -> {
            if (!playing || closed || !sameUtterance(utteranceId)) return;
            playing = false;
            publish("Audio नहीं चल पाया। फोन की Text-to-Speech voice जाँचें।");
        });
    }

    private boolean sameUtterance(String id) {
        return ("gita-" + generation + "-" + cursor).equals(id);
    }

    private void speakPart() {
        if (closed || !playing || !ready || engine == null) return;
        if (cursor < 0 || cursor >= playlist.size()) return;
        Part part = playlist.get(cursor);
        try {
            int availability = engine.setLanguage(part.language);
            if (availability == TextToSpeech.LANG_MISSING_DATA ||
                availability == TextToSpeech.LANG_NOT_SUPPORTED) {
                playing = false;
                publish("Hindi voice उपलब्ध नहीं है। Text-to-Speech सेटिंग जाँचें।");
                return;
            }
            int result = engine.speak(part.content, TextToSpeech.QUEUE_FLUSH, null,
                    "gita-" + generation + "-" + cursor);
            if (result == TextToSpeech.ERROR) {
                playing = false;
                publish("ऑडियो शुरू नहीं हुआ");
                return;
            }
            publish("▶ " + part.label + " (" + (cursor + 1) + "/" + playlist.size() + ")");
        } catch (Throwable failure) {
            playing = false;
            publish("ऑडियो प्ले नहीं हो पाया");
        }
    }

    private void clearCurrent() {
        generation++;
        cursor = 0;
        playing = false;
        waiting = false;
        playlist.clear();
        try { if (engine != null) engine.stop(); } catch (Throwable ignored) {}
    }

    private void publish(String message) {
        status = message;
        main.post(() -> {
            if (!closed && !activity.isFinishing() && !activity.isDestroyed()) {
                try { listener.onStatus(status, isPlaying()); }
                catch (Throwable ignored) { }
            }
        });
    }

    public boolean isPlaying() { return playing || waiting; }
    public boolean hasPausedItem() { return !playing && !waiting && !playlist.isEmpty(); }
    public String getStatus() { return status; }

    public void pause() {
        if (!playing && !waiting) return;
        generation++;
        playing = false;
        waiting = false;
        try { if (engine != null) engine.stop(); } catch (Throwable ignored) {}
        publish("रुका हुआ है");
    }

    public void resume() {
        if (closed || isPlaying() || playlist.isEmpty()) return;
        generation++;
        begin();
    }

    public void stop() {
        clearCurrent();
        publish("बंद किया");
    }

    public void close() {
        if (closed) return;
        clearCurrent();
        closed = true;
        try { if (engine != null) engine.shutdown(); } catch (Throwable ignored) {}
        engine = null;
        ready = false;
    }
}
