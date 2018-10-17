package fr.sdis83.remocra.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by jpt on 17/10/13.
 */
public final class ImageUtils {

    public static Bitmap showImageInImageView(File file, ImageView imageView) {
        Bitmap image = getResizedImage(imageView.getWidth(), imageView.getHeight(), null, file);
        imageView.setImageBitmap(image);
        return image;
    }

    public static Bitmap getResizedImage(ImageView imageView, byte[] dataPhoto) {
        if (imageView == null) {
            return null;
        }
        return getResizedImage(imageView.getWidth(), imageView.getHeight(), dataPhoto, null);
    }

    public static Bitmap getResizedImage(int width, int height, byte[] dataPhoto) {
        return getResizedImage(width, height, dataPhoto, null);
    }

    private static Bitmap getResizedImage(int width, int height, byte[] dataPhoto, File file) {
        Bitmap image = null;
        if ((dataPhoto != null && dataPhoto.length > 0) || (file != null)) {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            if (file == null) {
                BitmapFactory.decodeByteArray(dataPhoto, 0, dataPhoto.length, bmOptions);
            } else {
                BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            }
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            int targetW = width;
            int targetH = height;

            int scaleFactor = 1;
            if ((targetW > 0) || (targetH > 0)) {
                scaleFactor = Math.min(photoW / targetW, photoH / targetH);
            } else {
                return null;
            }

		/* Set bitmap options to scale the image decode target */
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
            if (file == null) {
                image = BitmapFactory.decodeByteArray(dataPhoto, 0, dataPhoto.length, bmOptions);
            } else {
                image = BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
            }
        }
        return image;
    }

    public static byte[] getMiniatureBytes(byte[] imgData) {
        // Création d'une miniature de 100*40 (taille de l'image dans liste hydrant)
        Bitmap imageMini = ImageUtils.getResizedImage(100, 40, imgData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageMini.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        return baos.toByteArray();
    }

    public static byte[] getMediumBytes(byte[] imgData) {
        Bitmap imageMedium = ImageUtils.getResizedImage(500, 200, imgData);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageMedium.compress(Bitmap.CompressFormat.JPEG, 20, baos);
        return baos.toByteArray();
    }
}
