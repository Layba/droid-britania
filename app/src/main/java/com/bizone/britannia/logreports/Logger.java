package com.bizone.britannia.logreports;

import android.util.Log;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sagar on 26/7/16.
 */
public class Logger {

    public static void d(String TAG, String msg){
        Log.d(TAG,msg);
        try {
            FileWriter writer = new FileWriter(new File(Constants.logFileName), true);
            PrintWriter out = new PrintWriter(writer);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            out.println(format.format(new Date()) + "     " + TAG+"  "+msg);
            out.flush();
            out.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeLines(String TAG, String msg){
        Log.d(TAG,msg);
        try{
            int numberOfLines = Commons.countLines(Constants.logFileName);
            if(numberOfLines > 26000){// here 600
                Commons.removeLines(Constants.logFileName,TAG,msg);
            }else {
                writeLog(TAG,msg);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void writeLog(String TAG, String msg){
        try {

            FileWriter writer = new FileWriter(new File(Constants.logFileName), true);
            PrintWriter out = new PrintWriter(writer);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            out.println(format.format(new Date()) + "     " + TAG+"  "+msg);
            out.flush();
            out.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void e(String TAG, Throwable t){
        try {
            Log.e(TAG, t.getMessage());
        }catch (Exception e){
            e.printStackTrace();
        }
        try {
            FileWriter writer = new FileWriter(new File(Constants.logFileName), true);
            PrintWriter out = new PrintWriter(writer);
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

            StackTraceElement[]arr=t.getStackTrace();

            out.println(format.format(new Date()) + "     " + TAG+"  "+t.getMessage());
            for(StackTraceElement s:arr){
                out.println(s.toString());

            }

            out.flush();
            out.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
