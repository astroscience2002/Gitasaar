package com.gitasaar.reader;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Standalone Bhagavad Gita Listen screen, rewritten independently from MainActivity.
 * One tap starts a chapter; no voice chooser or heavyweight image allocations.
 */
public final class GitaAudioActivity extends Activity {
    private static final int NAVY=0xFF162A4D;
    private static final int INK=0xFF1B3454;
    private static final int SAFFRON=0xFFD7651E;
    private static final int CREAM=0xFFFFF8E9;
    private static final int BORDER=0xFFE9C69D;

    private GitaRepository repository;
    private GitaAudioPlayer player;
    private TextView statusView;
    private TextView playbackButton;
    private LinearLayout chapterList;
    private int currentChapter;
    private boolean english;
    private boolean loaded;

    private static final String[] CHAPTERS_HI={
        "अर्जुनविषादयोग","सांख्ययोग","कर्मयोग","ज्ञानकर्मसन्यासयोग",
        "कर्मसन्यासयोग","ध्यानयोग","ज्ञानविज्ञानयोग","अक्षरब्रह्मयोग",
        "राजविद्याराजगुह्ययोग","विभूतियोग","विश्वरूपदर्शनयोग","भक्तियोग",
        "क्षेत्रक्षेत्रज्ञविभागयोग","गुणत्रयविभागयोग","पुरुषोत्तमयोग",
        "दैवासुरसम्पद्विभागयोग","श्रद्धात्रयविभागयोग","मोक्षसन्यासयोग"
    };
    private static final int[] COUNTS={
        47,72,43,42,29,47,30,28,34,42,55,20,34,27,20,24,28,78
    };

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        CrashReporter.install(this);
        getWindow().setStatusBarColor(NAVY);
        getWindow().setNavigationBarColor(CREAM);

        SharedPreferences prefs=getSharedPreferences(QuoteScheduler.PREFS,MODE_PRIVATE);
        english="en".equals(prefs.getString("app_language","hi"));
        currentChapter=Math.max(1,Math.min(18,getIntent().getIntExtra("chapter",1)));
        createUi();
        player=new GitaAudioPlayer(this,(message,playing)->{
            if(statusView!=null)statusView.setText(message);
            if(playbackButton!=null)playbackButton.setText(playing?
                (english?"Ⅱ  Pause":"Ⅱ  रोकें"):
                (english?"▶  Listen":"▶  सुनें"));
        });

        repository=new GitaRepository(this);
        statusView.setText(english?"Loading Bhagavad Gita…":"गीता के श्लोक तैयार हो रहे हैं…");
        repository.load((verses,error)->{
            if(isFinishing()||isDestroyed())return;
            if(error!=null || verses==null || verses.isEmpty()){
                statusView.setText(english?
                    "Audio data not available. Reopen and try again.":
                    "गीता का डेटा नहीं मिला। App दोबारा खोलें।");
                return;
            }
            loaded=true;
            chapterList.removeAllViews();
            for(int chapter=1;chapter<=18;chapter++)appendChapterRow(chapter);
            chapterList.post(()->{
                if(!isFinishing()&&!isDestroyed())startChapter(currentChapter);
            });
        });
    }

    private void createUi(){
        LinearLayout root=new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(CREAM);
        setContentView(root);

        LinearLayout titleRow=new LinearLayout(this);
        titleRow.setOrientation(LinearLayout.HORIZONTAL);
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        titleRow.setPadding(dp(12),dp(11),dp(16),dp(12));
        titleRow.setBackgroundColor(NAVY);
        TextView back=label("‹",32,Color.WHITE,true);
        back.setGravity(Gravity.CENTER);
        titleRow.addView(back,new LinearLayout.LayoutParams(dp(43),dp(50)));
        back.setOnClickListener(v->finish());

        LinearLayout titleColumn=new LinearLayout(this);
        titleColumn.setOrientation(LinearLayout.VERTICAL);
        titleColumn.addView(label(english?"Krishna Gita Audio":"श्रीकृष्ण गीता ऑडियो",22,0xFFFFDEA6,true));
        titleColumn.addView(label(english?"Listen to all 18 chapters":"18 अध्याय • 700 श्लोक",12,0xFFFFE9C6,false));
        titleRow.addView(titleColumn,new LinearLayout.LayoutParams(0,-2,1));
        root.addView(titleRow,new LinearLayout.LayoutParams(-1,-2));

        ScrollView scroll=new ScrollView(this);
        scroll.setFillViewport(true);
        LinearLayout content=new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16),dp(18),dp(16),dp(25));
        scroll.addView(content);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));

        LinearLayout playerCard=new LinearLayout(this);
        playerCard.setOrientation(LinearLayout.VERTICAL);
        playerCard.setPadding(dp(16),dp(19),dp(16),dp(18));
        playerCard.setBackground(rounded(Color.WHITE,18,BORDER));

        TextView heading=label(english?"Chapter audio":"अध्याय का पाठ",20,INK,true);
        playerCard.addView(heading);
        statusView=label("",14,INK,false);
        LinearLayout.LayoutParams st=new LinearLayout.LayoutParams(-1,-2);
        st.topMargin=dp(10);
        playerCard.addView(statusView,st);

        playbackButton=label(english?"▶  Listen":"▶  सुनें",17,Color.WHITE,true);
        playbackButton.setGravity(Gravity.CENTER);
        playbackButton.setBackground(rounded(SAFFRON,13,SAFFRON));
        LinearLayout.LayoutParams playParams=new LinearLayout.LayoutParams(-1,dp(53));
        playParams.topMargin=dp(17);
        playerCard.addView(playbackButton,playParams);
        playbackButton.setOnClickListener(v->{
            if(player==null)return;
            if(player.isPlaying())player.pause();
            else if(player.hasPausedItem())player.resume();
            else startChapter(currentChapter);
        });

        TextView stop=label(english?"■ Stop":"■ बंद करें",15,SAFFRON,true);
        stop.setGravity(Gravity.CENTER);
        stop.setBackground(rounded(CREAM,13,BORDER));
        LinearLayout.LayoutParams stopParams=new LinearLayout.LayoutParams(-1,dp(44));
        stopParams.topMargin=dp(9);
        playerCard.addView(stop,stopParams);
        stop.setOnClickListener(v->{if(player!=null)player.stop();});

        content.addView(playerCard,new LinearLayout.LayoutParams(-1,-2));
        TextView choose=label(english?"Choose a chapter":"अध्याय चुनें",20,INK,true);
        LinearLayout.LayoutParams chooseParams=new LinearLayout.LayoutParams(-1,-2);
        chooseParams.topMargin=dp(21);
        content.addView(choose,chooseParams);

        chapterList=new LinearLayout(this);
        chapterList.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams listParams=new LinearLayout.LayoutParams(-1,-2);
        listParams.topMargin=dp(10);
        content.addView(chapterList,listParams);
    }

    private void appendChapterRow(int chapter){
        LinearLayout tile=new LinearLayout(this);
        tile.setGravity(Gravity.CENTER_VERTICAL);
        tile.setOrientation(LinearLayout.HORIZONTAL);
        tile.setPadding(dp(10),dp(10),dp(10),dp(10));
        tile.setBackground(rounded(Color.WHITE,16,BORDER));

        tile.addView(new KrishnaAudioThumb(this,chapter),
                new LinearLayout.LayoutParams(dp(72),dp(76)));

        LinearLayout words=new LinearLayout(this);
        words.setOrientation(LinearLayout.VERTICAL);
        words.setPadding(dp(11),0,dp(5),0);
        words.addView(label((english?"Chapter ":"अध्याय ")+chapter,13,SAFFRON,true));
        words.addView(label(english?GitaLocale.CHAPTERS_EN[chapter-1]:
                CHAPTERS_HI[chapter-1],14,INK,true));
        words.addView(label(COUNTS[chapter-1]+(english?" verses • Tap to play":" श्लोक • सुनने के लिए दबाएँ"),
                12,0xFF795A4C,false));
        tile.addView(words,new LinearLayout.LayoutParams(0,-2,1));

        LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(-1,-2);
        params.bottomMargin=dp(11);
        chapterList.addView(tile,params);
        tile.setOnClickListener(v->startChapter(chapter));
    }

    private void startChapter(int chapter){
        currentChapter=chapter;
        if(player==null||repository==null||!loaded)return;
        List<GitaVerse> selected=new ArrayList<>();
        for(GitaVerse v:repository.getAll())
            if(v.chapter==chapter)selected.add(v);
        if(selected.isEmpty()){
            statusView.setText(english?"Chapter not loaded":"अध्याय उपलब्ध नहीं है");
            return;
        }
        if(!player.playChapter(selected,true,english))
            statusView.setText(english?"Unable to play audio":"ऑडियो शुरू नहीं हो पाया");
    }

    private int dp(float value){
        return Math.round(getResources().getDisplayMetrics().density*value);
    }

    private TextView label(String text,int size,int color,boolean strong){
        TextView view=new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER_VERTICAL);
        if(strong)view.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
        return view;
    }

    private GradientDrawable rounded(int color,int radius,int stroke){
        GradientDrawable drawable=new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1),stroke);
        return drawable;
    }

    @Override protected void onPause(){
        if(player!=null&&player.isPlaying())player.pause();
        super.onPause();
    }

    @Override protected void onDestroy(){
        if(player!=null)player.close();
        super.onDestroy();
    }
}
