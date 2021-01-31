package com.amitthakare.imagereducer;

import android.os.Environment;

import java.io.File;

/**
 * Created by jhansi on 15/03/15.
 */
public class ScanConstants {


    public static String REDUCE_IMG_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();

    public static String ReducedFilePath;

}
