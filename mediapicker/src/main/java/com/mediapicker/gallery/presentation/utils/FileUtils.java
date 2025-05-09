package com.mediapicker.gallery.presentation.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class FileUtils {

    public static final String WHATSAPP_SEARCH_TERM = "'%WhatsApp%'";
    private static final String INTERNAL_PHOTO_PATH = "/DCIM/";
    private static final String IMAGE_EXTENSION = ".jpg";
    private static final String IMAGE_BASE_NAME = "image";

    public static File getNewPhotoFileOnPicturesDirectory() {
        File externalStorageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File tempFile = null;

        int index = 0;

        while (tempFile == null || tempFile.exists()) {
            String imageName = generateFileNameBaseOnTimeWithoutExt(IMAGE_BASE_NAME, index) + IMAGE_EXTENSION;
            tempFile = new File(externalStorageDirectory, imageName);

            index++;
        }

        return tempFile;
    }

    public static File getProfileNewPhotoDir(Context context) {
        File path = new File(context.getFilesDir(), "/profile/picture");
        if (!path.exists()) {
            path.mkdirs();
        }
        return new File(path, "profile" + IMAGE_EXTENSION);
    }

    public static File getNewPhotoFileOnExternalStorage() {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File tempFile = null;

        int index = 0;

        while (tempFile == null || tempFile.exists()) {
            String imageName = generateFileNameBaseOnTimeWithoutExt(IMAGE_BASE_NAME, index) + IMAGE_EXTENSION;
            tempFile = new File(externalStorageDirectory + INTERNAL_PHOTO_PATH, imageName);

            index++;
        }

        return tempFile;
    }

    public static String generateFileNameBaseOnTimeWithoutExt(String base, int index) {
        StringBuilder fileName = new StringBuilder();
        fileName.append(base);
        fileName.append("_");
        fileName.append(getCurrentDateInFileNameStringFormat());

        if (index > 0) {
            fileName.append("_");
            fileName.append(index);
        }

        return fileName.toString();
    }

    public static String getCurrentDateInFileNameStringFormat() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static boolean existsFile(String path) {
        return new File(path).exists();
    }
}
