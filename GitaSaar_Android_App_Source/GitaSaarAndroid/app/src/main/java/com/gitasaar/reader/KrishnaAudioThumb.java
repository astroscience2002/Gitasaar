package com.gitasaar.reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.LruCache;
import android.view.View;
import java.io.InputStream;
import java.util.Locale;

/**
 * Memory-safe thumbnails for the Listen chapter list.
 *
 * A chapter image is only decoded in onDraw (visible items), never while all 18
 * cards are being built. The decode opens the asset with inJustDecodeBounds,
 * then loads a downsampled RGB_565 bitmap (~100 KB each instead of multi-MB).
 * The small LRU cache limits retained artwork to ~1.5 MB.
 */
public final class KrishnaAudioThumb extends View {
    private static final int CACHE_KB=1536;
    private static final int MAX_DECODE_PX=200;
    private static final LruCache<Integer,Bitmap> CACHE=new LruCache<Integer,Bitmap>(CACHE_KB){
        @Override protected int sizeOf(Integer key, Bitmap bitmap){
            return Math.max(1, bitmap.getByteCount()/1024);
        }
    };
    private static final float[] FACE_Y={
       .35f,.31f,.34f,.30f,.34f,.33f,.33f,.36f,.38f,
       .36f,.45f,.38f,.34f,.34f,.37f,.33f,.38f,.38f
    };
    private final int chapter;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG|Paint.DITHER_FLAG);
    private final float corner;
    public KrishnaAudioThumb(Context context,int chapter){
        super(context);
        this.chapter=Math.max(1,Math.min(18,chapter));
        corner=context.getResources().getDisplayMetrics().density*12;
        setLayerType(View.LAYER_TYPE_NONE,null);
    }
    private Bitmap loadThumb(){
        Bitmap cached=CACHE.get(chapter);
        if(cached!=null&&!cached.isRecycled())return cached;
        String asset=String.format(Locale.US,"krishna/chapter_%02d.webp",chapter);
        try {
            BitmapFactory.Options bounds=new BitmapFactory.Options();
            bounds.inJustDecodeBounds=true;
            try(InputStream in=getContext().getAssets().open(asset)){
                BitmapFactory.decodeStream(in,null,bounds);
            }
            if(bounds.outWidth<=0||bounds.outHeight<=0)return null;
            int sample=1;
            while(bounds.outWidth/(sample*2)>=MAX_DECODE_PX||
                  bounds.outHeight/(sample*2)>=MAX_DECODE_PX)sample*=2;
            BitmapFactory.Options opts=new BitmapFactory.Options();
            opts.inPreferredConfig=Bitmap.Config.RGB_565;
            opts.inSampleSize=sample;
            opts.inDither=true;
            opts.inScaled=false;
            Bitmap thumbnail;
            try(InputStream in=getContext().getAssets().open(asset)){
                thumbnail=BitmapFactory.decodeStream(in,null,opts);
            }
            if(thumbnail!=null){
                CACHE.put(chapter,thumbnail);
                return thumbnail;
            }
        }catch(Exception ignored){
            // Missing/corrupt art never prevents opening the Audio screen.
        }catch(OutOfMemoryError oom){
            CACHE.evictAll();
        }
        return null;
    }
    private static int clamp(int v,int lo,int hi){return Math.max(lo,Math.min(hi,v));}
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        final int width=getWidth(),height=getHeight();
        if(width<=0||height<=0)return;
        RectF frame=new RectF(0,0,width,height);
        Path clip=new Path();
        clip.addRoundRect(frame,corner,corner,Path.Direction.CW);
        canvas.save();
        canvas.clipPath(clip);
        canvas.drawColor(Color.rgb(28,53,80));
        Bitmap img=loadThumb();
        if(img!=null){
            int iw=img.getWidth(),ih=img.getHeight();
            float viewAspect=(float)width/height,sourceAspect=(float)iw/ih;
            Rect src;
            if(sourceAspect>viewAspect){
                int cropW=Math.max(1,Math.min(iw,Math.round(ih*viewAspect)));
                int left=clamp(Math.round(iw*.50f-cropW/2f),0,iw-cropW);
                src=new Rect(left,0,left+cropW,ih);
            }else{
                int cropH=Math.max(1,Math.min(ih,Math.round(iw/viewAspect)));
                int top=clamp(Math.round(ih*FACE_Y[chapter-1]-cropH/2f),0,ih-cropH);
                src=new Rect(0,top,iw,top+cropH);
            }
            canvas.drawBitmap(img,src,frame,paint);
        } else {
            paint.setColor(0xFFFFD78E);
            paint.setTextSize(Math.min(width,height)*.30f);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(String.valueOf(chapter),width/2f,height*.62f,paint);
            paint.setTextAlign(Paint.Align.LEFT);
            paint.setColor(Color.WHITE);
        }
        canvas.restore();
    }
}
