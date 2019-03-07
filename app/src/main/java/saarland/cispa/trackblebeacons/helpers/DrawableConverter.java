package saarland.cispa.trackblebeacons.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

/**
 * Helper for converting drawable to Bitmaps
 */

public class DrawableConverter {

    private Context context;

    public DrawableConverter(Context context) {
        this.context = context;
    }


    /**
     * Convert a drawable to a bitmap
     * @param drawable the drawable
     * @param widthDP the width
     * @param heightDP the height
     * @return the converted Bitmap
     */
    public Bitmap toBitmap(Drawable drawable, int widthDP, int heightDP) {
        Bitmap mutableBitmap = Bitmap.createBitmap(convertDpToPx(widthDP), convertDpToPx(heightDP), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(mutableBitmap);
        drawable.setBounds(0, 0, convertDpToPx(widthDP), convertDpToPx(heightDP));
        drawable.draw(canvas);

        return mutableBitmap;
    }

    /**
     * Convert dp to pixel
     * @param dp the dp
     * @return the corresponding pixels
     */
    public int convertDpToPx(float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
