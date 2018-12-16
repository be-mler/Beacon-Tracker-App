package saarland.cispa.trackblebeacons;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class DrawableConverter {

    Context context;

    public DrawableConverter(Context context) {
        this.context = context;
    }

    public Bitmap toBitmap(Drawable drawable, int widthDP, int heightDP) {
        Bitmap mutableBitmap = Bitmap.createBitmap(convertDpToPx(widthDP), convertDpToPx(heightDP), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, convertDpToPx(widthDP), convertDpToPx(heightDP));
        drawable.draw(canvas);

        return mutableBitmap;
    }

    public int convertDpToPx(float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
