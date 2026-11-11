package com.gitasaar.reader;

import java.util.Locale;

/**
 * Privacy-first topic matching. This is NOT a generative AI or a medical/legal service.
 * Every recommendation points to an actual verse in the downloaded, attributed Gita dataset.
 * Search text remains on the phone; it is never transmitted by this feature.
 */
public final class GuidanceEngine {
    private GuidanceEngine() {}

    public static final class Result {
        public final String titleHi, titleEn, insightHi, insightEn;
        public final String[] references;
        public final boolean urgent;
        Result(String hi, String en, String adviceHi, String adviceEn, boolean urgent, String... refs) {
            this.titleHi=hi; this.titleEn=en; this.insightHi=adviceHi; this.insightEn=adviceEn;
            this.references=refs; this.urgent=urgent;
        }
    }
    private static final class Topic {
        final String hi,en,adviceHi,adviceEn;
        final String[] keywords,refs;
        Topic(String hi,String en,String adviceHi,String adviceEn,String[] words,String... references){
            this.hi=hi;this.en=en;this.adviceHi=adviceHi;this.adviceEn=adviceEn;
            this.keywords=words;this.refs=references;
        }
    }
    private static Topic topic(String hi,String en,String a,String b,String[] k,String... refs){
        return new Topic(hi,en,a,b,k,refs);
    }
    private static final Topic[] TOPICS={
        topic("तनाव और चिंता","Stress & anxiety",
            "अपने नियंत्रण में आने वाले काम पर ध्यान दें। एक समय में एक कदम लें और परिणाम की चिंता से थोड़ी दूरी बनाएं।",
            "Focus on the actions you can control. Take one step at a time and loosen your grip on the outcome.",
            new String[]{"तनाव","चिंता","परेशान","घबर","डर लग","anxious","anxiety","stress","worry","worried","tension","overthink","overthinking","बेचैन","chinta","pareshan","tens","ghabra","future"}, "2-47","2-48","6-26"),
        topic("क्रोध पर संयम","Handling anger",
            "गुस्सा आने पर तुरंत प्रतिक्रिया देने के बजाय थोड़ी देर रुकें। बाद में शांत होकर बात करना आसान होगा।",
            "Pause before reacting. Coming back to the conversation calmly often makes a better response possible.",
            new String[]{"गुस्स","क्रोध","नफरत","angry","anger","rage","fight","gussa","gusse","krodh","चिड़चिड़"}, "2-63","16-21","12-13"),
        topic("कर्म और सफलता","Effort & results",
            "कर्म पर ध्यान टिकाए रखें। प्रगति के छोटे लक्ष्य बनाएं; केवल नतीजे से अपनी कीमत न आंकें।",
            "Keep doing the work and set smaller goals. Your worth does not depend only on the result.",
            new String[]{"नौकरी","काम","करियर","सफल","असफल","परीक्षा","पढ़ाई","मेहनत","job","career","exam","success","failure","failed","study","result","business","paisa","paise","mehnat","naukri","safalta"}, "2-47","3-19","2-48"),
        topic("रिश्ते और व्यवहार","Relationships & kindness",
            "दूसरे की बात सम्मान से सुनें। आपसी व्यवहार में करुणा रखें, साथ ही अपनी सीमाएँ साफ रखें।",
            "Listen respectfully, respond with kindness, and keep healthy boundaries.",
            new String[]{"रिश्त","प्यार","पति","पत्नी","शादी","दोस्त","अलग","ब्रेकअप","relationship","love","partner","marriage","friend","breakup","trust","dhoka","pyar","rishta","family","परिवार"}, "12-13","6-32","2-14"),
        topic("शोक और बदलाव","Grief & change",
            "दुख को दबाने की जरूरत नहीं। गीता बदलाव को जीवन का हिस्सा मानती है; मदद माँगना भी ठीक है।",
            "You do not have to suppress grief. Change is part of life, and asking for support is okay.",
            new String[]{"दुख","दर्द","शोक","मौत","खो दिया","अकेल","sad","sadness","grief","death","loss","lonely","missing","heartbreak","dukhi","dukh"}, "2-13","2-22","2-14"),
        topic("मन और एकाग्रता","Focus & the mind",
            "मन भटके तो धीरे से वापस अभ्यास पर लौटें। छोटे, नियमित अभ्यास से एकाग्रता बेहतर हो सकती है।",
            "When your mind wanders, gently return to your practice. Small, regular efforts can help.",
            new String[]{"मन भटक","ध्यान","आलस","फोकस","नींद","डिस्ट्रैक","mind","focus","concentrat","meditat","procrastinat","distraction","phone","scroll","dhyana","neend"}, "6-26","6-35","6-17"),
        topic("निर्णय और उलझन","Choices & confusion",
            "तथ्यों पर सोचें, अपने मूल्यों को पहचानें और भरोसेमंद व्यक्ति से बात करें। निर्णय जल्दबाजी में न लें।",
            "Review the facts and your values, and speak to someone you trust before deciding.",
            new String[]{"निर्णय","फैसल","उलझ","समझ नहीं","दुविध","confus","decision","choose","choice","कन्फ्यू","क्या करूं","kya karu","samajh"}, "18-63","2-50","3-35"),
        topic("धैर्य और साहस","Courage & patience",
            "मुश्किल समय में छोटे कदम उठाते रहें। सहायता, धैर्य और अपने प्रयासों पर भरोसा रखें।",
            "Keep taking small steps through uncertainty. Lean on support, patience, and your efforts.",
            new String[]{"डरा","डर","साहस","हार","असहाय","fear","afraid","hopeless","courage","confidence","himmat","darr","haar"}, "2-14","4-10","18-66"),
        topic("इच्छाएँ और संतुलन","Desire & balance",
            "जरूरत और चाहत के बीच फर्क करें। जो आपके पास है, उसका सम्मान करते हुए संतुलन बनाने का प्रयास करें।",
            "Distinguish needs from wants. Work toward balance while appreciating what you have.",
            new String[]{"लालच","पैसा","ईर्ष्या","तुलना","लोभ","greed","jealous","money","wealth","compare","comparison","envy","lalach"}, "2-70","16-21","3-37")
    };

    public static Result recommend(String message){
        String text=message==null?"":message.trim().toLowerCase(Locale.ROOT);
        if(text.length()>500)text=text.substring(0,500);
        boolean urgent=hasAny(text,new String[]{"आत्महत्या","खुदकुशी","जान देना","जीना नहीं","suicide","kill myself","self harm","self-harm"});
        int best=-1,score=0;
        for(int i=0;i<TOPICS.length;i++){
            int found=0;
            for(String word:TOPICS[i].keywords) if(text.contains(word))found+=word.length()>5?2:1;
            if(found>score){score=found;best=i;}
        }
        if(best<0) return new Result("गीता से सामान्य मार्गदर्शन","General guidance",
            "आपकी लिखी बात का विषय स्पष्ट नहीं हुआ। नीचे कर्म, समभाव और मन के अभ्यास से जुड़े श्लोक दिए हैं।",
            "We could not confidently identify a topic. Here are verses on action, balance, and training the mind.",
            urgent,"2-47","2-48","6-26");
        Topic t=TOPICS[best];
        return new Result(t.hi,t.en,t.adviceHi,t.adviceEn,urgent,t.refs);
    }
    private static boolean hasAny(String text,String[] tokens){
        for(String t:tokens) if(text.contains(t))return true;
        return false;
    }
}
