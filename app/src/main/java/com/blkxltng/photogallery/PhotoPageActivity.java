package com.blkxltng.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

/**
 * Created by firej on 11/14/2017.
 */

public class PhotoPageActivity extends SingleFragmentActivity {

    protected OnBackPressedListener mOnBackPressedListener;

    //Create a callback for the fragment to access onBackPressed()
    public interface OnBackPressedListener {
        void backPressed();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.mOnBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if(mOnBackPressedListener != null) {
            mOnBackPressedListener.backPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        mOnBackPressedListener = null; //Set the listener back to null
        super.onDestroy();
    }

    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        return PhotoPageFragment.newInstance(getIntent().getData());
    }
}
