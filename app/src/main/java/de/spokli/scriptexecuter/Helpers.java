package de.spokli.scriptexecuter;

import android.os.Environment;

import java.io.File;

/**
 * Created by Marco on 27.12.2015.
 */
public class Helpers {

    private static boolean externalStorageAvailable = false;
    private static boolean externalStorageWriteable = false;

    protected static File getDirectory() {
        // Find the root of the external storage.
        File root = android.os.Environment.getExternalStorageDirectory();

        File dir = new File(root.getAbsolutePath() + "/myscripts");
        return dir;
    }

    protected static boolean getExternalStorageAvailable(){
        checkExternalMedia();
        return externalStorageAvailable;
    }

    protected static boolean getExternalStorageWriteable(){
        checkExternalMedia();
        return externalStorageWriteable;
    }

    private static void checkExternalMedia() {
        externalStorageAvailable = false;
        externalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            // Can't read or write
            externalStorageAvailable = externalStorageWriteable = false;
        }
    }
}
