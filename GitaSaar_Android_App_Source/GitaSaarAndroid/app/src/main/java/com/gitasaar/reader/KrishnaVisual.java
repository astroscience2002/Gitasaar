package com.gitasaar.reader;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import android.view.View;

/** Krishna artwork cropped from the user's approved generated design. */
public final class KrishnaVisual extends View {
    private static final String DATA="/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAEMuMjoyKkM6NjpLR0NPZKZsZFxcZMySmnmm8dT++u3U6eX//////////+Xp////////////////////////////2wBDAUdLS2RXZMRsbMT//+n/////////////////////////////////////////////////////////////////////wAARCADEALQDASIAAhEBAxEB/8QAGQAAAwEBAQAAAAAAAAAAAAAAAAEDAgQF/8QAMRAAAgIBAwIFAwQCAgMBAAAAAAECEQMSITFBUQQTImGRMnGBFEJSoTNiU+EjY7HR/8QAGAEBAQEBAQAAAAAAAAAAAAAAAQACAwT/xAAhEQEBAQACAwABBQAAAAAAAAAAARECIRIxQVEDEzJCYf/aAAwDAQACEQMRAD8A4tutsV2y36XJW6oy8UoP1LYNhyppW6OqCejT0EsSa1R+CuNXsakCSbiy+OaezFojGMpz4XTuXx+XljehJ9qC8jh48UVLVt7bFRJKKpKkBlAAAERGKeTNrf0rgpmenFL4OaEpQ4YyJ0Qk9TTdq+ShzxjPJNavp5OgqSALFZlGArAkYWICJ2AgZA7XcDIEcShHL1kn7GpJPaUaZKF36py/DOjS2vTPUu0h/b/I8tc/l+VLXHePbsUko0pxS9wUqk4yVPs+pFt4ciSTcZboJfhz6rlisuLStqdmvDTThpqmtjNKm6tUZl/45LKuq3CXvtOoRiGXWrSo2a0GACm9MG7ogl4m9CS6sjFcLqaTnBapbp9Cnh9Mrmt3/wDBnKGxbhUIYgRAAAgAUFESGOgokyAak7q3QK3FNqmCABuApxqR0Y5Ne69jkKQbS22fT7nTlyxiTXVOMcldJRezOfPB+W909PVG45ZNPZqvxZWPqVypHK+3T058Lbx6lb09ToktWNWtghFR2j9PTYkm4tw6XwN/I9p4G4ycHe3Ox1Y2LFGSySdUn1G9OK23ZeNtlg09a3a3rkg5rNJXslvTMZMzlL27GGrui5bLh49zVJyeXKoR46nRGMYKoqkR8LFqLlLrwXZSYqdisy17iFY2nYGV9xrYk0AJib081T6EAnzapIy5NLTBOUurfQxKfqu1XRGoz9KRnyhxCUpQzT0vjlHRiyLJH3XKITjF5W2uTWFrG9LdqT2ZRL0AwEPNxu7v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";
    private static Bitmap image;
    private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private final int panel;
    public KrishnaVisual(Context context,int panel){super(context);this.panel=panel;setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    @Override protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        if(image==null)try{byte[] bytes=Base64.decode(DATA,Base64.DEFAULT);image=BitmapFactory.decodeByteArray(bytes,0,bytes.length);}catch(Exception ignored){}
        if(image==null)return;
        Rect source;
        if(panel==0)source=new Rect(0,0,180,112);
        else {int i=(panel-1)%3;source=new Rect(i*60,112,i*60+60,196);}
        RectF target=new RectF(0,0,getWidth(),getHeight());
        canvas.save();
        android.graphics.Path clip=new android.graphics.Path();
        clip.addRoundRect(target,16,16,android.graphics.Path.Direction.CW);
        canvas.clipPath(clip);
        canvas.drawBitmap(image,source,target,paint);
        canvas.restore();
    }
}