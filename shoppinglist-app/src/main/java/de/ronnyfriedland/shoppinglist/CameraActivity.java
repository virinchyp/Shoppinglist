package de.ronnyfriedland.shoppinglist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Action to access camera
 * 
 * @author Ronny Friedland
 */
public class CameraActivity extends Activity {

    public static final Integer RESULT_OK = 4324;

    private boolean done = true;

    protected static final String PHOTO_TAKEN = "photo_taken";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void startCameraActivity() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.getBoolean(CameraActivity.PHOTO_TAKEN)) {
            done = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CameraActivity.PHOTO_TAKEN, done);
    }

}