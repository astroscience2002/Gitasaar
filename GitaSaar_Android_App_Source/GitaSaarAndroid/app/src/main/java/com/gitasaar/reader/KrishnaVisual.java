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
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Bitmap bitmap=getBitmap();if(bitmap==null||getWidth()==0||getHeight()==0)return;
        float w=getWidth(),h=getHeight();
        float scale=Math.min(w/bitmap.getWidth(),h/bitmap.getHeight());
        float iw=bitmap.getWidth()*scale,ih=bitmap.getHeight()*scale;
        float x=(w-iw)/2f,y=(h-ih)/2f;
        RectF frame=new RectF(0,0,w,h);
        Path clip=new Path();clip.addRoundRect(frame,corner,corner,Path.Direction.CW);
        canvas.save();canvas.clipPath(clip);
        canvas.drawColor(0xFF1C3550);
        float cover=Math.max(w/bitmap.getWidth(),h/bitmap.getHeight());
        float bw=bitmap.getWidth()*cover,bh=bitmap.getHeight()*cover;
        Paint soft=new Paint(paint);soft.setAlpha(55);
        canvas.drawBitmap(bitmap,null,new RectF((w-bw)/2f,(h-bh)/2f,(w+bw)/2f,(h+bh)/2f),soft);
        canvas.drawBitmap(bitmap,null,new RectF(x,y,x+iw,y+ih),paint);
        canvas.restore();
    }
}