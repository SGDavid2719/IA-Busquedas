package ia.agafam;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Image
{
    private Bitmap bmp, bmpScaled;
    private int x;
    private int y;
    private boolean visible;
    private int width;
    private int height;  // de la imatge

    private Joc view;

    private int dibuix;
    private int posX;
    private int posY;
    private int ampla;   // de la instància
    private int alt;     // de la instància
    private int alpha;   // nivell de transparència
    private boolean selected;


    public Image(Joc g, int dib)
    {
        selected = false;
        dibuix = dib;
        alpha = 255;  // totalment opac
        visible = true;
        view = g;

        bmp = BitmapFactory.decodeResource(view.getResources(), dibuix);

        this.width = bmp.getWidth();
        this.height = bmp.getHeight();
    }

    public void draw(Canvas canvas, int x, int y, int w, int h, int alpha)
    {
        if (!visible) return;

        Rect src = new Rect(0, 0, width, height);
        Rect dst = new Rect(x, y, x+w, y+h);
        Paint paint = new Paint();
        paint.setAlpha(alpha);

        canvas.drawBitmap(bmp, src, dst, paint);

        posX = x;
        posY = y;
        ampla = w;
        alt = h;
    }

    public boolean contains(float x2, float y2) {
        return (x2 > posX && x2 < posX + ampla) && (y2 > posY && y2 < posY + alt);
    }

    public void select()
    {
        selected = true;
    }

    public void unselect()
    {
        selected = false;
    }

    public void setPosition(int a, int b)
    {
        x = a;
        y = b;
    }

    public boolean isVisible()
    {
        return visible;
    }

    public void setVisible(boolean v)
    {
        visible = v;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
