package com.gitasaar.reader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;

/** An original, lightweight Krishna-inspired illustration drawn directly in Android.
 * No third-party image download, tracking, or copyrighted photo is required. */
public final class KrishnaArtView extends View {
    private final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
    public KrishnaArtView(Context c){super(c);}
    private void color(int c){p.reset();p.setAntiAlias(true);p.setColor(c);p.setStyle(Paint.Style.FILL);}
    private void oval(Canvas c,int col,float l,float t,float r,float b){
        color(col);c.drawOval(l,t,r,b,p);
    }
    private void line(Canvas c,int col,float width,float x1,float y1,float x2,float y2){
        color(col);p.setStrokeWidth(width);p.setStrokeCap(Paint.Cap.ROUND);c.drawLine(x1,y1,x2,y2,p);
    }
    @Override protected void onDraw(Canvas canvas){
        float w=getWidth(),h=getHeight(); canvas.save();
        canvas.scale(w/350f,h/146f);
        color(Color.rgb(24,45,78));canvas.drawRoundRect(0,0,350,146,20,20,p);
        p.setShader(new RadialGradient(279,75,130,new int[]{0xFFB57431,0xFF283C63,0xFF182D4E},null,Shader.TileMode.CLAMP));
        canvas.drawRoundRect(0,0,350,146,20,20,p);p.setShader(null);
        oval(canvas,0x44FFC46C,212,-9,346,132);
        oval(canvas,0x55FFCC70,235,12,335,112);
        // stylized peacock feather and crown
        Path feather=new Path();feather.moveTo(284,30);feather.cubicTo(258,-3,299,-5,294,29);
        feather.cubicTo(315,-3,322,6,302,37);feather.close();
        color(0xFF4DAA91);canvas.drawPath(feather,p);
        oval(canvas,0xFF1C4C9A,285,6,303,32);oval(canvas,0xFFBA9B43,290,10,299,27);
        Path hair=new Path();hair.moveTo(263,53);hair.cubicTo(242,32,302,22,311,52);
        hair.cubicTo(333,86,318,110,291,113);hair.close();
        color(0xFF142541);canvas.drawPath(hair,p);
        oval(canvas,0xFFE2AD5E,258,35,305,60);
        for(int i=0;i<5;i++)oval(canvas,0xFFFFD88B,265+i*8,40,269+i*8,50);
        // serene blue Krishna face, ear, neck and body
        oval(canvas,0xFF2D6799,265,56,306,106);
        oval(canvas,0xFF3777AC,258,63,295,109);
        oval(canvas,0xFF295E91,275,96,297,126);
        oval(canvas,0xFF2F79B3,249,113,320,167);
        Path shawl=new Path();shawl.moveTo(242,145);shawl.lineTo(296,114);shawl.lineTo(315,143);shawl.close();
        color(0xFFEF9E35);canvas.drawPath(shawl,p);
        // eye and smile
        line(canvas,0xFF152842,2.1f,266,82,279,79);
        oval(canvas,0xFF172D51,275,79,279,83);
        line(canvas,0xFF7E2E3E,1.7f,262,98,273,99);
        // flute and fingers
        line(canvas,0xFFFFD782,5.5f,226,96,331,70);
        for(int i=0;i<4;i++)oval(canvas,0xFF734723,258+i*17,85-i*4,261+i*17,88-i*4);
        oval(canvas,0xFF2D6799,282,85,291,93);oval(canvas,0xFF2D6799,310,77,318,85);
        // text and ornaments
        color(0xFFFFE5AC);p.setTextSize(18);p.setFakeBoldText(true);
        canvas.drawText("ॐ  गीता सार",18,42,p);
        p.setTextSize(13);p.setFakeBoldText(false);
        canvas.drawText("॥ कर्म • ज्ञान • भक्ति ॥",18,67,p);
        p.setTextSize(11);canvas.drawText("18 अध्याय • 700 श्लोक",18,94,p);
        color(0xFFF4B34D);canvas.drawRoundRect(18,112,190,116,4,4,p);
        canvas.restore();
    }
}