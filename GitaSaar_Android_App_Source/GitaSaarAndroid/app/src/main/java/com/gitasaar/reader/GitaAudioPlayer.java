package com.gitasaar.reader;
import android.app.Activity;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.os.Build;
import android.content.SharedPreferences;
import java.util.Comparator;
import java.util.Set;
import android.speech.tts.UtteranceProgressListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
/** Narration using device text-to-speech; resumes at start of current verse/meaning. */
public final class GitaAudioPlayer {
    public interface Listener {void onStatus(String status, boolean playing);}
    private final Activity activity;
    private final Listener listener;
    private final ArrayList<Segment> segments=new ArrayList<>();
    private TextToSpeech speech;
    private int position=0,session=0;
    private boolean ready=false,playing=false;
    private String status="";
    private static final String PREFS="krishna_gita_audio";
    private static final String HI_VOICE="hi_voice",EN_VOICE="en_voice";
    private final SharedPreferences preferences;
    private final Locale HINDI=new Locale("hi","IN");
    private static class Segment {
        final String text,label;
        final Locale locale;
        Segment(String text,String label,Locale locale){this.text=text;this.label=label;this.locale=locale;}
    }
    public GitaAudioPlayer(Activity a,Listener callback){
        activity=a;listener=callback;preferences=a.getSharedPreferences(PREFS,Activity.MODE_PRIVATE);
        speech=new TextToSpeech(a,result->{
            ready=result==TextToSpeech.SUCCESS;
            if(ready&&speech!=null){
                speech.setSpeechRate(0.92f);
                speech.setPitch(1.0f);
                speech.setOnUtteranceProgressListener(new UtteranceProgressListener(){
                    @Override public void onStart(String id){}
                    @Override public void onDone(String id){onFinished(id);}
                    @Override public void onError(String id){onFinished(id);}
                });
            }
            report(ready?"Ready to listen":"Install a Hindi speech voice");
        });
    }
    private void report(String v){
        status=v;activity.runOnUiThread(()->listener.onStatus(status,playing));
    }
    private void onFinished(String id){activity.runOnUiThread(()->{
        if(!playing||!id.equals("seg_"+session+"_"+position))return;
        position++;
        if(position>=segments.size()){
            playing=false;segments.clear();position=0;report("Finished listening");
        }else playPart();
    });}
    private void addVerse(GitaVerse verse,boolean meaning,boolean english){
        segments.add(new Segment(cleanForSpeech(verse.sanskrit),"Chapter "+verse.chapter+" • Verse "+verse.number,new Locale("hi","IN")));
        if(meaning)segments.add(new Segment(cleanForSpeech(verse.meaning(english)),"Meaning "+verse.chapter+":"+verse.number,english?Locale.ENGLISH:new Locale("hi","IN")));
    }
    public boolean playVerse(GitaVerse verse,boolean meaning,boolean english){
        if(!ready||verse==null)return false;
        stop();addVerse(verse,meaning,english);playing=true;playPart();return true;
    }
    public boolean playChapter(List<GitaVerse> verses,boolean meaning,boolean english){
        if(!ready||verses==null||verses.isEmpty())return false;
        stop();for(GitaVerse v:verses)addVerse(v,meaning,english);
        playing=true;playPart();return true;
    }
    private void playPart(){
        if(!ready||speech==null||position>=segments.size())return;
        Segment v=segments.get(position);
        int availability=speech.setLanguage(v.locale);
        if(availability==TextToSpeech.LANG_MISSING_DATA||availability==TextToSpeech.LANG_NOT_SUPPORTED){
            playing=false;report("Install a "+(v.locale.getLanguage().equals("en")?"English":"Hindi")+" speech voice in Android settings");
            return;
        }
        Voice selected=findBestVoice(v.locale);
        if(selected!=null)speech.setVoice(selected);
        int outcome=speech.speak(v.text,TextToSpeech.QUEUE_FLUSH,null,"seg_"+session+"_"+position);
        if(outcome==TextToSpeech.ERROR){playing=false;report("Speech voice unavailable");}
        else report("▶  "+v.label+" ("+(position+1)+"/"+segments.size()+")");
    }
    /**
     * Keep verse numbers visible on screen, but never read them aloud.
     * Translations in the source begin with e.g. "।।1.1।।".
     * Sanskrit verses can end with "||१||".
     */
    public static String cleanForSpeech(String source){
        if(source==null)return "";
        String value=source.replace((char)160,' ').trim();
        value=value.replaceAll("^[\\s\\u0964\\u0965|।:;()]*[0-9\\u0966-\\u096f]+\\s*[.\\u0964\\u0965:,-]\\s*[0-9\\u0966-\\u096f]+[\\s\\u0964\\u0965|।:;()]*","");
        value=value.replaceAll("(?U)(?:[\\s|।\\u0965]+)[0-9\\u0966-\\u096f]+(?:\\s*[.:-]\\s*[0-9\\u0966-\\u096f]+)?[\\s|।\\u0965]*$","");
        value=value.replaceAll("[|\\u0964\\u0965]{2,}","।");
        value=value.replace('|','।');
        value=value.replaceAll("\\s+"," ").trim();
        return value;
    }
    public java.util.List<Voice> voices(Locale locale){
        java.util.ArrayList<Voice> matches=new java.util.ArrayList<>();
        if(speech==null||!ready)return matches;
        Set<Voice> all=speech.getVoices();
        if(all==null)return matches;
        for(Voice v:all){
            if(v.getLocale()!=null&&v.getLocale().getLanguage().equals(locale.getLanguage())&&
               v.getFeatures()!=null&&!v.getFeatures().contains("notInstalled"))matches.add(v);
        }
        matches.sort((a,b)->Integer.compare(score(b,locale),score(a,locale)));
        return matches;
    }
    private int score(Voice voice,Locale locale){
        int value=voice.getQuality()*5;
        if(!voice.isNetworkConnectionRequired())value+=3;
        if(voice.getLocale().getCountry().equals(locale.getCountry()))value+=5;
        return value;
    }
    private Voice findBestVoice(Locale locale){
        java.util.List<Voice> available=voices(locale);
        String preferred=preferences.getString(locale.getLanguage().equals("en")?EN_VOICE:HI_VOICE,"");
        for(Voice voice:available)if(voice.getName().equals(preferred))return voice;
        return available.isEmpty()?null:available.get(0);
    }
    public java.util.List<Voice> availableHindiVoices(){return voices(HINDI);}
    public boolean chooseHindiVoice(String name){
        for(Voice voice:availableHindiVoices()){
            if(voice.getName().equals(name)){
                preferences.edit().putString(HI_VOICE,name).apply();
                pause();
                report("Voice selected: "+name);
                return true;
            }
        }
        return false;
    }
    public String getStatus(){return status;}
    public boolean isPlaying(){return playing;}
    public void pause(){
        if(!playing)return;
        playing=false;session++;
        if(speech!=null)speech.stop();report("Paused • tap Resume");
    }
    public void resume(){if(ready&&!playing&&!segments.isEmpty()){session++;playing=true;playPart();}}
    public void stop(){
        playing=false;session++;position=0;segments.clear();
        if(speech!=null)speech.stop();report("Stopped");
    }
    public void close(){stop();if(speech!=null){speech.shutdown();speech=null;}ready=false;}
}
