package edu.ucsb.ece150.maskme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.widget.ImageView;
import android.util.Log;
import android.widget.Toast;
import android.graphics.Matrix;

public class MaskedImageView extends ImageView {
    FaceDetector.Face[] faces = null;
    int imageWidth;
    int imageHeight;

    public MaskedImageView(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final float scaleX = (float) canvas.getWidth() / (float) imageWidth;
        final float scaleY = (float) canvas.getHeight() / (float) imageHeight;
        final float transX = ((float) canvas.getWidth() - scaleX * imageWidth) / 2f;
        final float transY = ((float) canvas.getHeight() - scaleY * imageHeight) / 2f;

        if (faces != null) {
            final PointF midpoint = new PointF();

            for(int i = 0; i < faces.length; i++) {
                faces[i].getMidPoint(midpoint);

                final float x = scaleX * midpoint.x + transX;
                final float y = scaleY * midpoint.y + transY;
                final float faceSize = scaleX * faces[i].eyesDistance();

                drawMask(x, y, faceSize, canvas);
            }
        } else {
            Log.d("maskme", "no faces");
        }
    }

    private void drawMask(final float x, final float y, final float faceSize, final Canvas canvas) {
        /* your code here: draw a mask over the image */
        android.graphics.Paint mPaint = new android.graphics.Paint();
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.images);
        float h_icon = icon.getHeight();
        float w_icon = icon.getWidth();
        Matrix matrix = new Matrix();
        matrix.postScale(faceSize*3 / h_icon, faceSize*3 / h_icon);
        Bitmap resizeBitmap = Bitmap.createBitmap(icon, 0, 0, icon.getWidth(), icon.getHeight(), matrix, true);
        canvas.drawBitmap(resizeBitmap, x - resizeBitmap.getHeight()/2, y - resizeBitmap.getWidth()/2, mPaint);
    }

    public void maskFaces(FaceDetector.Face[] faces, int count, int width, int height) {
        imageWidth = width;
        imageHeight = height;

        this.faces = new FaceDetector.Face[count];

        System.arraycopy(faces, 0, this.faces, 0, count);
    }

    public void noFaces() {
        faces = null;
    }

    public void reset() {
        faces = null;
        setImageBitmap(null);
    }
}
