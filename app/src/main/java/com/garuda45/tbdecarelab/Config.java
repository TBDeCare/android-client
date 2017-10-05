package com.garuda45.tbdecarelab;

/**
 * Created by mure on 2/3/2017.
 */
public class Config {
    public static final String MY_PREFS_NAME = "TBDECARE_LAB";
    // File upload url (replace the ip with your server address)
    public static String SERVER = "";

    // Directory name to store captured images and videos
    public static final String IMAGE_DIRECTORY_NAME = "TBDeCare Lab Photos";

    public static String getFileUploadUrl() {
        if (SERVER.substring(SERVER.length()-1).equals("/")) {
            return SERVER;
        }
        else {
            return SERVER;
        }
    }
}