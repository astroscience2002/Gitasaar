package com.gitasaar.reader;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.List;

/** Gita-based thematic suggestions: not generative AI, and never claims guaranteed solutions. */
public final class GuidanceActivity extends Activity {
    private final int navy=Color.rgb(24,45,78), saffron=Color.rgb(213,102,29);
    private final int gold=Color.rgb(239,174,72), ink=Color.rgb(72,45,32);
    private final int cream=Color.rgb(255,249,236), border=Color.rgb(238,217,181);
    private LinearLayout content;
    private EditText problem;
    private GitaRepository repository;
    private boolean english;
    private int dp(int v){return Math.round(v*getResources().getDisplayMetrics().density);}
    private GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    private GradientDrawable stroke(){GradientDrawable d=bg(Color.WHITE,18);d.setStroke(dp(1),border);return d;}
    private TextView text(String s,int size,int color,boolean bold) {
        TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);t.setLineSpacing(dp(3),1.15f);return t;
    }
    private LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
    private void margin(LinearLayout parent,View v,int top){LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.topMargin=dp(top);parent.addView(v,p);}
    private TextView action(String s){TextView b=text(s,16,Color.WHITE,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(18),dp(14),dp(18),dp(14));b.setBackground(bg(saffron,15));return b;}
    private String tr(String hi,String en){return english?en:hi;}
    @Override public void onCreate(Bundle saved){
        super.onCreate(saved);
        SharedPreferences p=getSharedPreferences(QuoteScheduler.PREFS,MODE_PRIVATE);
        english="en".equals(p.getString("app_language","hi"));
        getWindow().setStatusBarColor(navy);
        getWindow().setNavigationBarColor(cream);
        repository=new GitaRepository(this);
        LinearLayout screen=column();screen.setBackgroundColor(cream);setContentView(screen);
        LinearLayout header=column();header.setPadding(dp(21),dp(18),dp(21),dp(20));header.setBackgroundColor(navy);
        TextView back=text("‹   "+tr("गीता से मार्गदर्शन","Gita Guidance"),24,Color.WHITE,true);
        header.addView(back);back.setOnClickListener(v->finish());
        margin(header,text(tr("अपनी परेशानी साझा करें। श्लोक से प्रेरणा पाएँ।","Share what is troubling you. Reflect with the Gita."),13,Color.rgb(255,223,181),false),8);
        screen.addView(header);
        ScrollView scroll=new ScrollView(this);
        screen.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        content=column();content.setPadding(dp(18),dp(18),dp(18),dp(30));scroll.addView(content);
        KrishnaArtView art=new KrishnaArtView(this);
        content.addView(art,new LinearLayout.LayoutParams(-1,dp(146)));
        margin(content,text(tr("आपके मन में क्या चल रहा है?","What's on your mind?"),22,ink,true),19);
        margin(content,text(tr("हिंदी, English या Hinglish में लिखें। आपका सवाल इस फोन पर ही जाँचा जाएगा।","Write in Hindi, English or Hinglish. Your question is matched on this device."),13,Color.DKGRAY,false),8);
        problem=new EditText(this);problem.setTextSize(16);problem.setTextColor(ink);
        problem.setHint(tr("जैसे: मुझे भविष्य को लेकर बहुत चिंता होती है...","For example: I feel anxious about my future..."));
        problem.setHintTextColor(Color.GRAY);problem.setGravity(Gravity.TOP);
        problem.setPadding(dp(15),dp(16),dp(15),dp(16));problem.setMinLines(4);
        problem.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
        problem.setBackground(stroke());margin(content,problem,17);
        margin(content,text(tr("या इनमें से चुनें","Or choose a topic"),14,saffron,true),16);
        String[] suggestions=english?new String[]{"I feel stressed about my future","My efforts are not giving results","I am angry and upset","I cannot focus on studies"}:
            new String[]{"भविष्य की चिंता होती है","मेहनत के बाद भी सफलता नहीं मिलती","मुझे बहुत गुस्सा आता है","पढ़ाई में मन नहीं लगता"};
        for(String sample:suggestions){
            TextView chip=text("✦  "+sample,14,navy,false);
            chip.setPadding(dp(13),dp(12),dp(13),dp(12));chip.setBackground(stroke());margin(content,chip,8);
            chip.setOnClickListener(v->problem.setText(sample));
        }
        TextView search=action(tr("गीता से मार्गदर्शन पाएँ  →","Find Gita Guidance  →"));margin(content,search,19);
        search.setOnClickListener(v->{
            String input=problem.getText().toString().trim();
            if(input.isEmpty()){problem.setError(tr("अपनी बात लिखें","Please describe your concern"));return;}
            showResult(GuidanceEngine.recommend(input));
        });
        margin(content,text(tr("ध्यान दें: यह धार्मिक शिक्षा पर आधारित सामान्य मार्गदर्शन है, चिकित्सा, कानूनी या आपातकालीन सहायता का विकल्प नहीं।",
            "Note: This is spiritual reflection, not a substitute for medical, legal or emergency support."),12,Color.GRAY,false),17);
    }
    private void showResult(GuidanceEngine.Result result){
        while(content.getChildCount()>10)content.removeViewAt(content.getChildCount()-1);
        LinearLayout resultBlock=column();resultBlock.setPadding(dp(17),dp(17),dp(17),dp(18));resultBlock.setBackground(stroke());
        margin(resultBlock,text(tr("गीता का मार्गदर्शन","Guidance from the Gita"),14,saffron,true),0);
        margin(resultBlock,text(english?result.titleEn:result.titleHi,21,navy,true),8);
        margin(resultBlock,text(english?result.insightEn:result.insightHi,15,ink,false),11);
        if(result.urgent){
            margin(resultBlock,text(tr("अगर आप खुद को नुकसान पहुँचाने के खतरे में हैं, तुरंत स्थानीय emergency services या किसी भरोसेमंद व्यक्ति से मदद लें।",
               "If you may harm yourself, seek immediate support from local emergency services or someone you trust."),14,Color.rgb(155,38,28),true),10);
        }
        margin(resultBlock,text(tr("संबंधित श्लोक","Related verses"),17,saffron,true),20);
        margin(content,resultBlock,22);
        repository.load((verses,error)->{
            if(isFinishing()||isDestroyed())return;
            if(error!=null){
                margin(resultBlock,text(tr("श्लोक लोड नहीं हुए। इंटरनेट चालू रखकर दोबारा कोशिश करें।","Verses could not load. Please check your internet."),14,ink,false),12);
                return;
            }
            for(String id:result.references){
                String[] pair=id.split("-");
                GitaVerse v=repository.get(Integer.parseInt(pair[0]),Integer.parseInt(pair[1]));
                if(v==null)continue;
                LinearLayout card=column();card.setPadding(dp(13),dp(13),dp(13),dp(13));card.setBackground(bg(cream,13));
                margin(card,text(english?v.referenceEn():v.reference(),14,saffron,true),0);
                margin(card,text(v.sanskrit,18,ink,true),9);
                margin(card,text(v.meaning(english),15,ink,false),10);
                margin(resultBlock,card,12);
            }
            margin(resultBlock,text(tr("इन श्लोकों का संदर्भ पढ़ें और अपनी स्थिति के अनुसार सोचें। श्लोक किसी खास परिणाम की गारंटी नहीं देते।",
                "Read these verses in context. No verse guarantees a specific outcome."),12,Color.GRAY,false),15);
        });
    }
}
