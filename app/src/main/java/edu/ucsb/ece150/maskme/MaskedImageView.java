package edu.ucsb.ece150.maskme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;

import com.google.android.gms.vision.face.Face;

public class MaskedImageView extends ImageView {
    SparseArray<Face> faces = null;
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

            for(int i = 0; i < faces.size(); i++) {
                float midpointX = 0.5f*(faces.valueAt(i).getLandmarks().get(0).getPosition().x + faces.valueAt(i).getLandmarks().get(1).getPosition().x); /*[i].getMidPoint(midpoint)*/;
                float midpointY = 0.5f*(faces.valueAt(i).getLandmarks().get(1).getPosition().y + faces.valueAt(i).getLandmarks().get(1).getPosition().y);
                final float x = scaleX * midpointX + transX;
                final float y = scaleY * midpointY + transY;
                final float faceSize = scaleX * (faces.valueAt(i).getLandmarks().get(1).getPosition().x - faces.valueAt(i).getLandmarks().get(0).getPosition().x); /*[i].eyesDistance()*/;

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

    public void maskFaces(/*FaceDetector.Face[]*/SparseArray<Face> faces, int count, int width, int height) {
        imageWidth = width;
        imageHeight = height;

        this.faces = faces;

//        System.arraycopy(faces, 0, this.faces, 0, count);
    }

    public void noFaces() {
        faces = null;
    }

    public void reset() {
        faces = null;
        setImageBitmap(null);
    }
}
