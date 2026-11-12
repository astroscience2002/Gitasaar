package com.gitasaar.reader;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ApplicationExitInfo;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/** On-device crash diagnostic. Does not transmit or collect user questions. */
public final class CrashReporter {
    private static final String PREF="krishna_gita_crash_report";
    private static final String LOG="last_error";
    private static final String TIME="last_exit_checked";
    private static boolean installed=false;
    private CrashReporter(){}

    public static synchronized void install(Context context){
        if(installed)return;
        installed=true;
        Thread.UncaughtExceptionHandler previous=Thread.getDefaultUncaughtExceptionHandler();
        Context app=context.getApplicationContext();
        Thread.setDefaultUncaughtExceptionHandler((thread,error)->{
            try{
                StringWriter buffer=new StringWriter();
                error.printStackTrace(new PrintWriter(buffer));
                String report="Krishna Gita crash, "+Build.MANUFACTURER+" "+Build.MODEL+
                    ", Android "+Build.VERSION.RELEASE+"\n"+buffer;
                if(report.length()>12000)report=report.substring(0,12000);
                app.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit().putString(LOG,report).commit();
            }catch(Throwable ignored){}
            if(previous!=null)previous.uncaughtException(thread,error);
            else {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            }
        });
    }

    public static void record(Context context,Throwable error){
        try{
            StringWriter buffer=new StringWriter();
            error.printStackTrace(new PrintWriter(buffer));
            context.getSharedPreferences(PREF,Context.MODE_PRIVATE).edit()
                    .putString(LOG,buffer.toString().substring(0,Math.min(12000,buffer.toString().length())))
                    .apply();
        }catch(Throwable ignored){}
    }

    public static void showOnNextLaunch(Activity activity){
        try{
            android.content.SharedPreferences prefs=activity.getSharedPreferences(PREF,Context.MODE_PRIVATE);
            String report=prefs.getString(LOG,"");
            if(Build.VERSION.SDK_INT>=30){
                ActivityManager manager=(ActivityManager)activity.getSystemService(Context.ACTIVITY_SERVICE);
                if(manager!=null){
                    List<ApplicationExitInfo> history=manager.getHistoricalProcessExitReasons(activity.getPackageName(),0,5);
                    long lastChecked=prefs.getLong(TIME,0);
                    long latest=lastChecked;
                    for(ApplicationExitInfo info:history){
                        if(info.getTimestamp()<=lastChecked)continue;
                        latest=Math.max(latest,info.getTimestamp());
                        int reason=info.getReason();
                        if(reason==ApplicationExitInfo.REASON_CRASH ||
                            reason==ApplicationExitInfo.REASON_CRASH_NATIVE ||
                            reason==ApplicationExitInfo.REASON_LOW_MEMORY ||
                            reason==ApplicationExitInfo.REASON_ANR){
                            if(report.isEmpty()){
                                report="Previous exit: "+reasonText(reason)+
                                    "\nDevice: "+Build.MANUFACTURER+" "+Build.MODEL+
                                    "\nAndroid: "+Build.VERSION.RELEASE+
                                    "\nTime: "+info.getTimestamp()+
                                    "\nDetails: "+info.getDescription();
                            }
                        }
                    }
                    if(latest>lastChecked)prefs.edit().putLong(TIME,latest).apply();
                }
            }
            if(report.isEmpty())return;
            final String diagnostic=report;
            prefs.edit().remove(LOG).apply();
            new android.app.AlertDialog.Builder(activity)
                .setTitle("पिछली बार App बंद होने की रिपोर्ट")
                .setMessage("Crash की असली वजह जानने के लिए रिपोर्ट Copy करके chat में भेजें। कोई निजी सवाल इसमें नहीं लिया जाता।")
                .setPositiveButton("Copy report",(dialog,which)->{
                    ClipboardManager clipboard=(ClipboardManager)activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    if(clipboard!=null)clipboard.setPrimaryClip(ClipData.newPlainText("Krishna Gita crash",diagnostic));
                })
                .setNegativeButton("बंद करें",null).show();
        }catch(Throwable e){
            Log.e("KrishnaGita","Could not show crash diagnostic",e);
        }
    }

    private static String reasonText(int reason){
        if(reason==ApplicationExitInfo.REASON_CRASH)return "Java crash";
        if(reason==ApplicationExitInfo.REASON_CRASH_NATIVE)return "Native crash";
        if(reason==ApplicationExitInfo.REASON_LOW_MEMORY)return "Android killed app due to low memory";
        if(reason==ApplicationExitInfo.REASON_ANR)return "App not responding";
        return "Code "+reason;
    }
}
