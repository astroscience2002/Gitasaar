package com.gitasaar.reader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.LruCache;
import android.view.View;
import java.io.InputStream;
import java.util.Locale;
/** Full-frame, portrait-safe illustrations, loaded from the 18 HD artwork assets. */
public final class KrishnaVisual extends View {
    private static final LruCache<String,Bitmap> CACHE=new LruCache<String,Bitmap>(16384){
        @Override protected int sizeOf(String key,Bitmap bitmap){return bitmap.getByteCount()/1024;}
    };
    private static final int[] FALLBACK={R.drawable.krishna_chariot,R.drawable.krishna_teaching,R.drawable.krishna_battle};
    private final int panel;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG|Paint.DITHER_FLAG);
    private final float corner;
    public KrishnaVisual(Context context,int panel){
        super(context);this.panel=panel;corner=context.getResources().getDisplayMetrics().density*15f;
    }
    private String file(){
        if(panel==0)return "home_banner.webp";
        if(panel==19)return "guidance_banner.webp";
        if(panel==20)return "daily_quote.webp";
        if(panel==21)return "verse_banner.webp";
        return String.format(Locale.US,"chapter_%02d.webp",Math.max(1,Math.min(18,panel)));
    }
    private Bitmap getBitmap(){
        String name=file();
        Bitmap cached=CACHE.get(name);
        if(cached!=null&&!cached.isRecycled())return cached;
        try(InputStream input=getContext().getAssets().open("krishna/"+name)){
            BitmapFactory.Options options=new BitmapFactory.Options();
            options.inPreferredConfig=Bitmap.Config.RGB_565;
            Bitmap result=BitmapFactory.decodeStream(input,null,options);
            if(result!=null){CACHE.put(name,result);return result;}
        }catch(Exception ignored){}
        Bitmap backup=BitmapFactory.decodeResource(getResources(),FALLBACK[Math.floorMod(panel,FALLBACK.length)]);
        if(backup!=null)CACHE.put(name,backup);
        return backup;
    }
    // Focal points are tuned to the actual 22 Krishna artworks. These keep the
    // faces inside wide banner crops while avoiding side bars or blurred copies.
    private static final float[] Y_FOCUS={
        0f, .30f,.27f,.35f,.28f,.30f,.34f,.27f,.31f,.35f,
        .31f,.47f,.30f,.39f,.35f,.42f,.34f,.30f,.49f,
        .42f,.29f,.30f
    };
    private float focusY(){
        if(panel==0)return .49f; // Kurukshetra home banner
        return panel>0 && panel<Y_FOCUS.length ? Y_FOCUS[panel]:.40f;
    }
    private float focusX(){
        if(panel==17)return .36f; // Krishna reading on left of scene
        if(panel==18)return .62f; // Krishna teaching at right of scene
        if(panel==3)return .56f;  // include Krishna and Arjuna
        if(panel==19)return .54f; // guidance two-person scene
        return .50f;
    }
    private int clamp(int v,int min,int max){return Math.max(min,Math.min(v,max));}
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Bitmap bitmap=getBitmap();if(bitmap==null||getWidth()==0||getHeight()==0)return;
        float viewW=getWidth(),viewH=getHeight();
        int width=bitmap.getWidth(),height=bitmap.getHeight();
        float targetAspect=viewW/viewH,originalAspect=(float)width/height;
        android.graphics.Rect source;
        if(originalAspect>targetAspect){
            // Wide image in tall card: crop only left/right, keeping the focal point.
            int croppedW=Math.min(width,Math.max(1,Math.round(height*targetAspect)));
            int left=clamp(Math.round(width*focusX()-croppedW/2f),0,width-croppedW);
            source=new android.graphics.Rect(left,0,left+croppedW,height);
        }else{
            // Portrait image in wide card: crop only top/bottom, aimed at Krishna's face.
            int croppedH=Math.min(height,Math.max(1,Math.round(width/targetAspect)));
            int top=clamp(Math.round(height*focusY()-croppedH/2f),0,height-croppedH);
            source=new android.graphics.Rect(0,top,width,top+croppedH);
        }
        RectF frame=new RectF(0,0,viewW,viewH);
        Path clip=new Path();clip.addRoundRect(frame,corner,corner,Path.Direction.CW);
        canvas.save();canvas.clipPath(clip);
        canvas.drawBitmap(bitmap,source,frame,paint);
        canvas.restore();
    }
}