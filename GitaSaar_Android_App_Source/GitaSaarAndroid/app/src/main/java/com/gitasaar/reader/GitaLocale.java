package com.gitasaar.reader;

import java.util.HashMap;
import java.util.Locale;

/** English UI copy for Gita Saar. The source verses remain intact in Sanskrit. */
public final class GitaLocale {
    private GitaLocale(){}
    private static final HashMap<String,String> EN=new HashMap<>();
    private static void m(String a,String b){EN.put(a,b);}
    public static final String[] CHAPTERS_EN={
        "Arjuna's Dilemma","Sankhya Yoga","Karma Yoga","Wisdom in Action",
        "Renunciation of Action","Yoga of Meditation","Knowledge and Wisdom",
        "The Eternal Brahman","Royal Knowledge","Divine Glories",
        "Universal Form","Bhakti Yoga","Field and Knower",
        "Three Gunas","The Supreme Person","Divine and Demonic Qualities",
        "Three Kinds of Faith","Liberation and Renunciation"
    };
    static {
        m("गीता सार","Krishna Gita");m("श्रीकृष्ण गीता","Krishna Gita");m("सुनें","Listen");m("श्रीमद्भगवद्गीता","Bhagavad Gita");
        m("॥ श्रीमद्भगवद्गीता ॥","॥ Bhagavad Gita ॥");
        m("आज","Home");m("अध्याय","Chapters");m("खोजें","Search");
        m("सेव","Saved");m("सेटिंग","Settings");m("गैलरी","Gallery");
        m("इंटरनेट कनेक्शन चाहिए","Internet connection required");
        m("गीता सार सिर्फ इंटरनेट चालू होने पर खुलेगा। मोबाइल डेटा या Wi-Fi ऑन करें और दोबारा कोशिश करें.","Gita Saar needs an active internet connection. Turn on mobile data or Wi-Fi and try again.");
        m("फिर से कोशिश करें","Try again");m("श्रीमद्भगवद्गीता • हर दिन एक नई सीख","Bhagavad Gita • Wisdom for every day");
        m("18 अध्याय","18 Chapters");m("संपूर्ण भगवद्गीता","Complete Bhagavad Gita");
        m("श्लोक","Verse");m("श्लोक और आसान हिंदी अर्थ","Sanskrit verse and meaning");
        m("श्लोक खोजें","Find a Verse");m("शब्द या 2:47 लिखकर खोजें","Search words or enter 2:47");
        m("मेरे पसंदीदा श्लोक","Saved Verses");m("आपके सेव किए हुए श्लोक","Your bookmarked verses");
        m("सेटिंग्स","Settings");m("नोटिफिकेशन और जानकारी","Notifications and information");
        m("गीता के 700 श्लोक लोड हो रहे हैं…","Loading 700 Gita verses…");
        m("पहली बार इंटरनेट से पूरा डेटा डाउनलोड होगा।","On first use, the Gita data is downloaded online.");
        m("गीता डेटा अभी नहीं खुल सका","Could not load Gita content");m("नेटवर्क की समस्या है","Network issue");
        m("दोबारा लोड करें","Reload");m("हर दिन गीता से\nएक नई सीख","Timeless wisdom\nfor every day");
        m("18 अध्याय • 700 श्लोक • हिंदी और English","18 chapters • 700 verses • Hindi & English");
        m("गीता पढ़ना शुरू करें  →","Read the Gita  →");m("✦  अपनी समस्या लिखें","✦  Seek Gita Guidance");
        m("चिंता, रिश्ते, करियर, गुस्सा या कोई और परेशानी। गीता के संबंधित श्लोकों से मार्गदर्शन पाएँ।","Stress, relationships, career or anger? Explore relevant teachings from the Gita.");
        m("गीता से मार्गदर्शन पाएँ  →","Get Gita Guidance  →");
        m("✦  आज का गीता संदेश","✦  Today's Gita Wisdom");m("पूरा श्लोक पढ़ें","Read full verse");
        m("🔔  सुबह गीता का संदेश पाएँ","🔔  Daily Gita reminder");
        m("हर दिन लगभग 8 बजे एक नया श्लोक और हिंदी अर्थ।","A new verse every day around 8 AM.");
        m("Daily Notification चालू करें","Enable daily reminders");
        m("अध्याय पढ़ें","Explore Chapters");m("सभी 18 अध्याय देखें  →","View all 18 chapters  →");
        m("कुरुक्षेत्र से जीवन की सीख तक","Life lessons from Kurukshetra");
        m("अपनी पसंद का अध्याय चुनें और एक-एक श्लोक हिंदी अर्थ के साथ पढ़ें।","Choose a chapter to read each verse and its meaning.");
        m("पूरा पढ़ें  ›","Read more  ›");m("इस अध्याय के श्लोक अभी उपलब्ध नहीं हैं।","Verses are not available for this chapter yet.");
        m("श्रीकृष्ण का संदेश","Krishna's Message");m("आसान हिंदी अर्थ","Meaning in English");
        m("♥  सेव हो गया","♥  Saved");m("♡  सेव करें","♡  Save");
        m("↗  शेयर","↗  Share");m("श्लोक शेयर करें","Share verse");
        m("शब्द, अर्थ या 2:47 लिखें","Search words or enter 2:47");
        m("पूरी गीता में संस्कृत और हिंदी अर्थ से खोजें।","Search Sanskrit verses and translations.");
        m("उदाहरण: कर्म, मन, योग, 2:47","Try: karma, yoga, mind, 2:47");
        m("कोई श्लोक नहीं मिला","No verses found");m(" परिणाम"," results");
        m(" · पहले 60 दिख रहे हैं"," · showing first 60");
        m("अभी कोई श्लोक सेव नहीं है","No saved verses yet");
        m("किसी भी श्लोक पर ‘सेव करें’ दबाएँ। वह यहाँ दिखाई देगा।","Tap Save on any verse to find it here.");
        m("🌐  भाषा / Language","🌐  App Language");
        m("हिंदी अर्थ","Hindi Meaning");m("🔔  रोज़ का गीता संदेश","🔔  Daily Gita Wisdom");
        m("हर सुबह लगभग 8 बजे श्लोक और उसका हिंदी अर्थ नोटिफिकेशन में पाएँ। समय फोन की बैटरी सेटिंग पर निर्भर है।","Get a verse notification around 8 AM daily. Exact timing depends on phone settings.");
        m("नोटिफिकेशन बंद करें","Disable notifications");m("नोटिफिकेशन चालू करें","Enable notifications");
        m("डेटा और प्राइवेसी","Data & Privacy");
        m("गीता के श्लोक HTTPS से डाउनलोड होकर फोन में cache होते हैं। App पढ़ने के लिए इंटरनेट चाहिए। सेव किए श्लोक आपके फोन में रहते हैं। Ads दिखाने के लिए Google AdMob इस्तेमाल होता है।",
          "Gita verses are downloaded over HTTPS and cached on your phone. An internet connection is required to open the app. Bookmarks stay on the device. Ads use Google AdMob.");
        m("इस क्षेत्र के लिए privacy form की ज़रूरत नहीं है।","No privacy form is required in your region.");
        m("श्लोक स्रोत","Verse Sources");
        m("Bhagavad Gita Dataset, Adiyogi Arts (MIT License). मूल संस्कृत श्लोक प्राचीन ग्रंथ हैं। हिंदी अर्थ स्रोत के अनुसार हैं।",
          "Bhagavad Gita Dataset, Adiyogi Arts (MIT License). Sanskrit is an ancient text; translations are attributed to the source.");
        m("सुबह 8 बजे के लिए गीता संदेश चालू किया गया।","Daily Gita reminder enabled for around 8 AM.");
        m("नोटिफिकेशन की अनुमति नहीं मिली।","Notification permission was not granted.");
        m("श्रीकृष्ण चित्र गैलरी","Krishna Gallery");
        m("भक्ति की सुंदर छवियाँ","Beautiful devotional illustrations");
        m("मुख्य स्क्रीन","Home");
        m("आज का संदेश देखें","See today's quote");
        m("भाषा चुनें","Choose Language");
        m("हिंदी","Hindi");m("अंग्रेज़ी","English");
        m("पूर्ण भगवद्गीता","Complete Bhagavad Gita");
        m("अध्यायों में से चुनें","Choose a chapter");
    }
    public static String en(String value){
        if(value==null)return "";
        String direct=EN.get(value);
        if(direct!=null)return direct;
        String x=value;
        for(int i=0;i<18;i++){
            String[] hi={"अर्जुनविषादयोग","सांख्ययोग","कर्मयोग","ज्ञानकर्मसन्यासयोग","कर्मसन्यासयोग","ध्यानयोग","ज्ञानविज्ञानयोग","अक्षरब्रह्मयोग","राजविद्याराजगुह्ययोग","विभूतियोग","विश्वरूपदर्शनयोग","भक्तियोग","क्षेत्रक्षेत्रज्ञविभागयोग","गुणत्रयविभागयोग","पुरुषोत्तमयोग","दैवासुरसम्पद्विभागयोग","श्रद्धात्रयविभागयोग","मोक्षसन्यासयोग"};
            x=x.replace(hi[i],CHAPTERS_EN[i]);
        }
        x=x.replace("अध्याय ","Chapter ").replace(" के बाकी श्लोक"," — all verses").replace(" श्लोक"," verses").replace("श्लोक ","Verse ");
        return x;
    }
}