package com.gitasaar.reader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

/**
 * Full-resolution Krishna and Arjuna paintings bundled in the APK.
 * Art: public-domain historical paintings via Wikimedia Commons.
 * See THIRD_PARTY_LICENSES.md for attribution and originals.
 */
public final class KrishnaVisual extends View {
    private static final int[] IMAGES={R.drawable.krishna_chariot,R.drawable.krishna_teaching,
            R.drawable.krishna_battle};
    private static final Bitmap[] CACHE=new Bitmap[IMAGES.length];
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG|Paint.DITHER_FLAG);
    private final int panel;
    private final float radius;
    public KrishnaVisual(Context context,int panel){
        super(context);this.panel=panel;
        this.radius=getResources().getDisplayMetrics().density*18;
        setLayerType(View.LAYER_TYPE_HARDWARE,null);
    }
    private Bitmap bitmap(){
        int index=panel==0?0:(panel-1)%IMAGES.length;
        if(CACHE[index]==null){
            BitmapFactory.Options o=new BitmapFactory.Options();
            o.inPreferredConfig=Bitmap.Config.RGB_565;
            CACHE[index]=BitmapFactory.decodeResource(getResources(),IMAGES[index],o);
        }
        return CACHE[index];
    }
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        Bitmap bitmap=bitmap();if(bitmap==null)return;
        int w=bitmap.getWidth(), h=bitmap.getHeight();
        float displayAspect=getWidth()/(float)Math.max(getHeight(),1);
        float sourceAspect=w/(float)h;
        Rect source;
        if(sourceAspect>displayAspect){
            int crop=(int)(h*displayAspect), left=(w-crop)/2;
            source=new Rect(left,0,left+crop,h);
        }else{
            int crop=(int)(w/displayAspect),top=(h-crop)/2;
            source=new Rect(0,top,w,top+crop);
        }
        RectF target=new RectF(0,0,getWidth(),getHeight());
        Path clip=new Path();clip.addRoundRect(target,radius,radius,Path.Direction.CW);
        canvas.save();canvas.clipPath(clip);
        canvas.drawBitmap(bitmap,source,target,paint);
        canvas.restore();
    }
}
