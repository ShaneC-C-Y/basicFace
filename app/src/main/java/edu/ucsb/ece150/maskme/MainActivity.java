package edu.ucsb.ece150.maskme;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.FaceDetector;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    public enum Mode {
        PREVIEW,
        CAPTURE,
        MASK
    }

    public static final int MAX_FACES = 5;

    private MaskCameraSurfaceView mCameraSurface;
    private MaskedImageView mImageView;
    private TextView mTextView;
    private FrameLayout mCameraFrame;
    private Button mCameraButton;
    private Bitmap mImage;

    private Mode mMode = Mode.PREVIEW;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupCamera();
//        tToast("onCreate.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCameraSurface.stopCamera();
    }

    private void setupCamera() {
        /* your code here:
            - create the new masked camera surface view with application context
            - create the new masked image view with application context
            - set masked image view scale type to FIT_XY
            - get the camera frame from the resource R.id.cameraDisplay
            - get the camera button from R.id.cameraButton
            - set the button on click method to be our cameraButtonOnClick() method
            - add the new masked camera surface and masked image view to camera frame
            - bring the camera surface view to front
        */

        mCameraFrame = (FrameLayout) findViewById(R.id.cameraDisplay);
        mCameraButton = (Button) findViewById(R.id.cameraButton);
//        mTextView = (TextView) findViewById(R.id.textView);

        mCameraSurface = new MaskCameraSurfaceView(this);
        mImageView = new MaskedImageView(this);
        mTextView = new TextView(this);

//        resetCamera();//not sure
        mCameraButton.setText(getString(R.string.take_picture));

        mCameraFrame.addView(mCameraSurface);
        mCameraFrame.addView(mImageView);
        mCameraFrame.addView(mTextView);
        mCameraFrame.bringChildToFront(mCameraSurface);

        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                tToast("touch.");
                cameraButtonOnClick(v);
            }
        });
    }

    private Bitmap rotateImage(Bitmap image) {
        final Matrix matrix = new Matrix();

        matrix.postTranslate(0f - image.getWidth() / 2, 0f - image.getHeight() / 2);
        matrix.postRotate(mCameraSurface.getCurrentRotation());
        matrix.postTranslate(image.getWidth() / 2, image.getHeight() / 2);
        return Bitmap.createBitmap(image,0, 0, image.getWidth(), image.getHeight(), matrix, false);
    }


    private void takePicture() {

        mCameraSurface.capture(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                final Bitmap snapShot = rotateImage(BitmapFactory.decodeByteArray(data, 0, data.length));
                mImage = snapShot.copy(Bitmap.Config.RGB_565, false);
                mImageView.setImageBitmap(mImage);
//                mImageView.invalidate();
                mCameraFrame.bringChildToFront(mImageView);
            }
        });
    }

    private void addMask() {
        /* your code here:
            - make a new detector passing it mImage's width and height and the MAX_FACES variable
            - initialize an array of FaceDetector.Face objects the size of MAX_FACES
            - run facial detection with the "find faces" method
            - if there are some faces, call the maskFaces() method of the surface view
            - if not, call noFaces()
            - invalidate the surface view to refresh the drawing
        */

//        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
//        bitmapOption.inPreferredConfig = Bitmap.Config.RGB_565;
//        mImage = BitmapFactory.decodeResource(getResources(), R.drawable.kp,bitmapOption);
        FaceDetector face_detector = new FaceDetector(mImage.getWidth(), mImage.getHeight(), MAX_FACES);
        FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];
        int face_count;
        face_count = face_detector.findFaces(mImage, faces);
        if (face_count > 0)
            mImageView.maskFaces(faces, face_count, mImage.getWidth(), mImage.getHeight());
        else {
            mImageView.noFaces();
            mTextView.setText("no face, try again");
            mTextView.setTextSize(48f);
            mCameraFrame.bringChildToFront(mTextView);
        }
        // not sure mCameraSurface or mImageView here should be invalidate
//        mCameraSurface.invalidate();
        mImageView.invalidate();
    }

    private void resetCamera() {
        mCameraFrame.bringChildToFront(mCameraSurface);
        mImageView.reset();
        mCameraSurface.startPreview();
    }

    public void cameraButtonOnClick(View v) {

        switch (mMode) {
            case PREVIEW:
                takePicture();
                mCameraButton.setText(getString(R.string.add_mask));
                mMode = Mode.CAPTURE;
                break;
            case CAPTURE:
                addMask();
                mCameraButton.setText(getString(R.string.show_preview));
                mMode = Mode.MASK;
                break;
            case MASK:
                resetCamera();
//                tToast("resetCamera");
                mCameraButton.setText(getString(R.string.take_picture));
                mMode = Mode.PREVIEW;
                break;
            default:
                break;
        }
    }
    private void tToast(String s) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, s, duration);
        toast.show();
    }
}
