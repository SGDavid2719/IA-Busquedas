package ia.agafam;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Button {
    private Image image1, image2;
    private Paint paint = new Paint();
    private int left, top, right, bottom, width, height;
    private boolean selected;

    public Button(Joc m, int img1, int img2, int l, int t, int sx, int sy)
    {
        image1 = new Image(m, img1);
        image2 = new Image(m, img2);
        left = l;
        top = t;
        right = l+sx;
        bottom = t+sy;
        width = sx;
        height = sy;
        selected = false;
    }

    public void draw(Canvas c)
    {
        if (selected)
            image1.draw(c, left, top, width, height, 255);
        else
            image2.draw(c, left, top, width, height, 255);

    }

    public void toggle()
    {
        selected = !selected;
    }

    public void select()
    {
        selected = true;
    }

    public void unSelect()
    {
        selected = false;
    }

    public boolean isSelected()
    {
        return selected;
    }

    public boolean contains(float x2, float y2) {
        return (x2 > left && x2 < right) && (y2 > top && y2 < bottom);
    }
}