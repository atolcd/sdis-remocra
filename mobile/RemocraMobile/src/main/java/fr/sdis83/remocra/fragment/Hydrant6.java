package fr.sdis83.remocra.fragment;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import fr.sdis83.remocra.GlobalRemocra;
import fr.sdis83.remocra.R;
import fr.sdis83.remocra.database.HydrantTable;
import fr.sdis83.remocra.util.ImageUtils;

public class Hydrant6 extends AbstractHydrant {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private ImageView mImageView;
    private String filePath;
    private String tmpFilePath;
    private boolean needSave;
    private Bitmap mImageBitmap;

    public Hydrant6() {
        super(R.layout.hydrant6, HydrantTable.COLUMN_STATE_H6);
    }

    @Override
    public CharSequence getTabTitle() {
        return GlobalRemocra.getInstance().getContext().getString(R.string.photo);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("PHOTO_CAPTURE", filePath);
        outState.putString("TMP_PHOTO_CAPTURE", tmpFilePath);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            filePath = savedInstanceState.getString("PHOTO_CAPTURE");
            tmpFilePath = savedInstanceState.getString("TMP_PHOTO_CAPTURE");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        needSave = false;
        mImageView = (ImageView) view.findViewById(R.id.imageView);
        ImageButton btnCapture = (ImageButton) view.findViewById(R.id.capture);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCapture(view);
            }
        });
        return view;
    }

    @Override
    protected void onBeforeBind(Cursor cursor) {
        if (filePath != null) {
            handleBigCameraPhoto();
        } else {
            byte[] dataImage = cursor.getBlob(cursor.getColumnIndex(HydrantTable.COLUMN_PHOTO));
            if (dataImage != null && dataImage.length > 0) {
                mImageView.setImageBitmap(BitmapFactory.decodeByteArray(dataImage, 0, dataImage.length));
            }
        }
    }

    protected void doCapture(View view) {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = getOutputMediaFile(MEDIA_TYPE_IMAGE); // create a file to save the image
        tmpFilePath = file.getAbsolutePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)); // set the image file name
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                filePath = tmpFilePath;
                if (handleBigCameraPhoto()) {
                    needSave = true;
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
                tmpFilePath = null;
            } else {
                // Image capture failed, advise user
                tmpFilePath = null;
            }
        }
    }

    private boolean handleBigCameraPhoto() {
        if (filePath != null) {
            File file = new File(filePath);
            mImageBitmap = ImageUtils.showImageInImageView(file, mImageView);
            mImageView.refreshDrawableState();
            return mImageBitmap != null;
        }
        return false;
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        String s = Environment.getExternalStorageState();
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Remocra");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Remocra", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        // Create the file
        if (!mediaFile.exists()) {
            try {
                if (!mediaFile.createNewFile()) {
                    Log.d("Remocra", "failed to create file");
                    return null;
                }
            } catch (IOException e) {
                Log.d("Remocra", "failed to create file", e);
                return null;
            }
        }

        return mediaFile;
    }

    @Override
    public ContentValues getDataToSave() {
        ContentValues values = super.getDataToSave();
        values.put(HydrantTable.COLUMN_STATE_H6, true);
        if (needSave) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
            byte[] imgData = baos.toByteArray();
            values.put(HydrantTable.COLUMN_PHOTO, ImageUtils.getMediumBytes(imgData));
            values.put(HydrantTable.COLUMN_PHOTO_MINI, ImageUtils.getMiniatureBytes(imgData));
            values.put(HydrantTable.COLUMN_IS_NEW_PHOTO, true);
            needSave = false;

            // Nettoyage du fichier créé
            if (filePath!=null) {
                File file = new File(filePath);
                if (file.canWrite()) {
                    file.delete();
                }
                filePath = null;
            }
        }
        return values;
    }
}
