package com.bizone.britannia.logreports;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.bizone.britannia.Commons;
import com.bizone.britannia.Constants;
import com.bizone.britannia.R;
import com.bizone.britannia.db.DBAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by siddhesh on 7/27/16.
 */
public class SendLog extends AppCompatActivity {
    private static final String TAG=SendLog.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_logs);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


    /*public void onSend(View v){
        Logger.d(TAG,"inside onSend");
        File dir = new File(Constants.fldr);
        for (File file : dir.listFiles()) {
            if (file.getName().endsWith((".zip"))) {
                file.delete();
            }
        }

        zipFilePath=Constants.fldr+System.currentTimeMillis()+".zip";
        generateZipFile();
        Uri uri = Uri.fromFile(new File(zipFilePath));
        Intent emailIntent=new Intent(Intent.ACTION_SEND);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        String imeiNumber = telephonyManager.getDeviceId()==null?"N.A.":telephonyManager.getDeviceId();
        String subject = "Tech-Support log: "+imeiNumber;
        //String body = SelectQueries.getSetting(this, Settings.USER_ID)+" requesting a tech-support call as <Please mention reason here>";

        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@bizonesoft.com"});
        emailIntent.putExtra(Intent.EXTRA_STREAM,uri);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
       // emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.setType("text/plain");
        startActivity(emailIntent);
    }*/
    private void generateZipFile() {
        Logger.d(TAG,"inside generateZipFile");
        String backupDBPath = Constants.fldr+ "/"+ System.currentTimeMillis() +".db";
        String currentDB = this.getDatabasePath(DBAdapter.DB_NAME).getAbsolutePath();
        Commons.copyTextFile(currentDB, backupDBPath);

        // declare an array for storing the files i.e the path
        // of your source files
        String[] s = new String[2];

        // Type the path of the files in here
        s[0] = backupDBPath;
        s[1] = Constants.logFileName; // /sdcard/ZipDemo/textfile.txt

        //zip(s, zipFilePath);
        File dbFile = new File(backupDBPath);
        if(dbFile.exists()){
            dbFile.delete();
        }
    }

    private void zip(String[] _files, String zipFileName) {
        Logger.d(TAG,"inside zip");
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
            byte data[] = new byte[1024];

            for (int i = 0; i < _files.length; i++) {

                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, 1024);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, 1024)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }
    }
}
