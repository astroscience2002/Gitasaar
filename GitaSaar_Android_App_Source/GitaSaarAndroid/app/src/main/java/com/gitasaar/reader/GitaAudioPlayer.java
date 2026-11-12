package com.gitasaar.reader;
import android.app.Activity;
import android.speech.tts.TextToSpeech;
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
    private static class Segment {
        final String text,label;
        final Locale locale;
        Segment(String text,String label,Locale locale){this.text=text;this.label=label;this.locale=locale;}
    }
    public GitaAudioPlayer(Activity a,Listener callback){
        activity=a;listener=callback;
        speech=new TextToSpeech(a,result->{
            ready=result==TextToSpeech.SUCCESS;
            if(ready&&speech!=null){
                speech.setSpeechRate(0.85f);
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
        segments.add(new Segment(verse.sanskrit,"Chapter "+verse.chapter+" • Verse "+verse.number,new Locale("hi","IN")));
        if(meaning)segments.add(new Segment(verse.meaning(english),"Meaning "+verse.chapter+":"+verse.number,english?Locale.ENGLISH:new Locale("hi","IN")));
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
        if(availability==TextToSpeech.LANG_MISSING_DATA||availability==TextToSpeech.LANG_NOT_SUPPORTED)
            speech.setLanguage(new Locale("hi","IN"));
        int outcome=speech.speak(v.text,TextToSpeech.QUEUE_FLUSH,null,"seg_"+session+"_"+position);
        if(outcome==TextToSpeech.ERROR){playing=false;report("Speech voice unavailable");}
        else report("▶  "+v.label+" ("+(position+1)+"/"+segments.size()+")");
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
