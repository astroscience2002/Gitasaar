package com.gitasaar.reader;

/** Immutable verse backed by the attributed Adiyogi Arts Sanskrit/Hindi/English dataset. */
public final class GitaVerse {
    public final int chapter, number;
    public final String chapterName, sanskrit, hindi, english;
    public GitaVerse(int chapter,int number,String chapterName,String sanskrit,String hindi,String english){
        this.chapter=chapter;this.number=number;this.chapterName=chapterName;
        this.sanskrit=sanskrit;this.hindi=hindi;this.english=english;
    }
    public GitaVerse(int chapter,int number,String chapterName,String sanskrit,String hindi){
        this(chapter,number,chapterName,sanskrit,hindi,"");
    }
    public String id(){return chapter+"-"+number;}
    public String reference(){return "अध्याय "+chapter+" · श्लोक "+number;}
    public String referenceEn(){return "Chapter "+chapter+" · Verse "+number;}
    public String meaning(boolean englishMode){
        if(englishMode && !english.isEmpty())return english;
        if(!englishMode && !hindi.isEmpty())return hindi;
        return englishMode?"English meaning is unavailable for this verse.": "इस श्लोक का हिंदी अर्थ उपलब्ध नहीं है।";
    }
}