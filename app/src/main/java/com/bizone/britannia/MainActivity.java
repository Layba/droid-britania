package com.bizone.britannia;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bizone.britannia.db.DBAdapter;
import com.bizone.britannia.entities.RoutePlanEntity;
import com.bizone.britannia.internetTask.GetProductSkuTask;
import com.bizone.britannia.internetTask.GetRoutePlanTask;
import com.bizone.britannia.internetTask.GetShopRequestTask;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;
import com.bizone.britannia.utilities.SlidingTabLayout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Declaring Your View and Variables
    private ProgressDialog dialog;
    Toolbar toolbar;
    ViewPager pager;
    ViewPagerAdapter adapter;
    SlidingTabLayout tabs;
    String zipFilePath;
    TextView hyperLink;
    private static final int GET_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView loginId = (TextView) header.findViewById(R.id.id_text);
        loginId.setText(SelectQueries.getSetting(this, Settings.USER_ID));

        hyperLink = (TextView) findViewById(R.id.company);
        hyperLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://bizonesoft.com/"));
                startActivity(browserIntent);
            }
        });
        if ("".equals(SelectQueries.getSetting(this, Settings.FIRST_TIME))) {
            Logger.d(TAG, "GetRoutePlanTask");
            if (ContextCommons.isOnline(this)) {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Downloading data....");
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
                new GetRoutePlanTask(this, 0) {
                    @Override
                    protected void afterExecution(boolean result, String msg) {
                        if (result) {
                            getProductSkuTask();
                        } else if ("403".equals(msg)) {
                            // expire
                            closeDialog();
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Session expired. Please login again.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                            InsertQueries.setSetting(MainActivity.this, Settings.PASSWORD, "");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            closeDialog();
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
            } else {
                Toast.makeText(MainActivity.this, "Internet is unavailable. Please turn ON mobile data", Toast.LENGTH_LONG).show();
            }
        } else {
            initializeWidgets();
        }

        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.nav);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.black));

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        Calendar rightNow = Calendar.getInstance();
        int currentHour = rightNow.get(Calendar.HOUR_OF_DAY);
        long currentTime = System.currentTimeMillis();
        Logger.d(TAG, "currentTime = " + currentTime);
        String startDaySettingString = SelectQueries.getSetting(MainActivity.this,Settings.START_YOUR_DAY_AT);
        int startDaySetting;
        if(startDaySettingString.equals("")){
            startDaySetting = 8;
        }else {
            startDaySetting = Integer.parseInt(startDaySettingString);
        }
        String lastDay = SelectQueries.getSetting(MainActivity.this, Settings.START_TIME);
        if (!"".equals(lastDay)) {
            long lastStartDay = Long.parseLong(lastDay);
            Logger.d(TAG, "lastStartDay = " + lastStartDay);
            long dayOne = TimeUnit.DAYS.toMillis(1)-Constants.SIXHRSINMILLIS;
            Logger.d(TAG, "dayOne = " + dayOne);

            if (currentTime - lastStartDay > dayOne && currentHour >= startDaySetting) {
                startDayAlert();
            }
        } else if("".equals(lastDay) && currentHour >= startDaySetting){
            startDayAlert();
        }
    }

    private void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    private void getProductSkuTask() {

        new GetProductSkuTask(this) {
            @Override
            protected void afterExecution(boolean result, String msg) {
                if (result) {
                    if ("".equals(SelectQueries.getSetting(MainActivity.this, Settings.FIRST_TIME))) {
                        getPreviousShops();
                    }else {
                        initializeWidgets();
                        closeDialog();
                        Toast.makeText(MainActivity.this, "Data sync successful", Toast.LENGTH_LONG).show();
                    }
                } else if ("403".equals(msg)) {
                    // expire
                    closeDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Session expired. Please login again.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    InsertQueries.setSetting(MainActivity.this, Settings.PASSWORD, "");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    closeDialog();
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

    }

    private void getPreviousShops() {
        new GetShopRequestTask(this){

            @Override
            protected void afterExecution(boolean result, String msg) {
                if(result) {
                    InsertQueries.setSetting(MainActivity.this, Settings.FIRST_TIME, "done");
                    ContextCommons.setAlarm(MainActivity.this);
                    initializeWidgets();
                    closeDialog();
                    Toast.makeText(MainActivity.this, "Data downloaded successful", Toast.LENGTH_LONG).show();
                }else if("403".equals(msg)) {
                    // expire
                    closeDialog();
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("Session expired. Please login again.")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                    InsertQueries.setSetting(MainActivity.this, Settings.PASSWORD, "");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }else {
                    closeDialog();
                    Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();

        if ((dialog != null) && dialog.isShowing())
            dialog.dismiss();
        dialog = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeWidgets() {

        ArrayList<RoutePlanEntity> routePlanEntities = SelectQueries.getRoutePlanElements(this);
        Collections.sort(routePlanEntities, RoutePlanEntity.DATE_COMPARATOR);

        for (RoutePlanEntity e : routePlanEntities) {
            Log.d(TAG, "entity=" + e);
        }

        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), routePlanEntities);

        // Assigning ViewPager View and setting the adapter
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);

        // Assiging the Sliding Tab Layout View
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        tabs.setEnabled(false);

        // Setting Custom Color for the Scroll bar indicator of the Tab View
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.colorPrimary);
            }
        });

        // Setting the ViewPager For the SlidingTabsLayout
        tabs.setViewPager(pager);

        try {

            int index=0;
            Calendar today= Commons.normalizeCalendarTime(Calendar.getInstance());

            Calendar routeCal = null,lastRoutecal=null;
            for(int i=0; i<routePlanEntities.size(); i++){
                routeCal = Calendar.getInstance();
                routeCal.setTime(Commons.milliToDateObj(routePlanEntities.get(i).date));
                Commons.normalizeCalendarTime(routeCal);
                lastRoutecal = Calendar.getInstance();
                lastRoutecal.setTime(Commons.milliToDateObj(routePlanEntities.get(routePlanEntities.size()-1).date));
                Commons.normalizeCalendarTime(lastRoutecal);

                if(today.compareTo(routeCal)==0){
                    index=i;
                    break;
                }else if (today.compareTo(lastRoutecal)>0){
                    index=routePlanEntities.size();
                    break;
                }

            }

            Log.d(TAG,"index="+index);
            Log.d(TAG,"today="+today);
            Log.d(TAG,"route="+routeCal);
            pager.setCurrentItem(index,true);

        } catch (Exception e) {
            Logger.e(TAG,e);
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.upload_pending) {
            Intent i = new Intent(this, UploadSalesActivity.class);
            startActivity(i);
            return true;
        } else if(id == R.id.purchase) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(MainActivity.this, "Please give Location permission to proceed further.", Toast.LENGTH_LONG).show();
                return true;
            }
            long currentTime = System.currentTimeMillis();
            Logger.d(TAG, "currentTime = " + currentTime);
            String lastDay = SelectQueries.getSetting(MainActivity.this, Settings.START_TIME);
            if (!"".equals(lastDay)) {
                long lastStartDay = Long.parseLong(lastDay);
                Logger.d(TAG, "lastStartDay = " + lastStartDay);
                long dayOne = TimeUnit.DAYS.toMillis(1)-Constants.SIXHRSINMILLIS;
                Logger.d(TAG, "dayOne = " + dayOne);
                if (currentTime - lastStartDay < dayOne) {
                    Intent i = new Intent(this, PurchaseActivity.class);
                    startActivity(i);
                } else {
                    startDayAlert();
                }
            }
            else{
                startDayAlert();
            }
            return true;
        }
            else if (id == R.id.send_log) {
            Logger.removeLines(TAG,"removing lines from sending logs");
            Logger.d(TAG, "inside onSend");
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
                String subject = "Britannia BB Tech-Support log: "+imeiNumber;
                //String body = SelectQueries.getSetting(this, Settings.USER_ID)+" requesting a tech-support call as <Please mention reason here>";

                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {"support@bizonesoft.com"});
                emailIntent.putExtra(Intent.EXTRA_STREAM,uri);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                // emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                emailIntent.setType("text/plain");
                startActivity(emailIntent);

            return true;

        } else if(id == R.id.sync){
            if(ContextCommons.isOnline(this)) {
                dialog = new ProgressDialog(this);
                dialog.setMessage("Syncing....");
                dialog.show();
                int vid = SelectQueries.getRoutePlanVid(this);
                new GetRoutePlanTask(this, vid) {
                    @Override
                    protected void afterExecution(boolean result, String msg) {
                        if (result) {
                            getPreviousShops();
                        } else if ("403".equals(msg)) {
                            // expire
                            closeDialog();
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("Session expired. Please login again.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                            InsertQueries.setSetting(MainActivity.this, Settings.PASSWORD, "");
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            closeDialog();
                            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
                        }
                    }
                }.execute();
            }else {
                Toast.makeText(MainActivity.this, "Internet is unavailable. Please turn ON mobile data", Toast.LENGTH_LONG).show();
            }

        }else if(id == R.id.history) {
            Intent i = new Intent(this, HistoryMenuActivity.class);
            startActivity(i);
            return true;

        }else if(id == R.id.startday){
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(MainActivity.this,"Please give Location permission to proceed further.",Toast.LENGTH_LONG).show();
                return false;
            }
            Intent i=new Intent(this,StartDayActivity.class);
            startActivity(i);
            return true;

        }else if(id == R.id.callSupport){
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, GET_CALL);
            }else {
                String support = SelectQueries.getSetting(this,Settings.SUPPORT_CALL);
                if("".equals(support)) {
                    support = "+918652668310";
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                String phNum = "tel:" + support;
                callIntent.setData(Uri.parse(phNum));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Please grant call permission in Manage Apps." + support + ""
                            , Toast.LENGTH_LONG).show();
                    return true;
                }
                startActivity(callIntent);
                return true;

            }


        }else if(id == R.id.logout){
            new android.app.AlertDialog.Builder(MainActivity.this)
                    .setMessage("Do you really wish to Logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            InsertQueries.setSetting(MainActivity.this, Settings.PASSWORD, "");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.dismiss();
                        }
                    }).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


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
        s[1] = Constants.logFileName;

        zip(s, zipFilePath);
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
            Logger.d(TAG,"byte 1024");
            for (int i = 0; i < _files.length; i++) {
                Log.d(TAG,"file length"+_files.length);
                Log.d(TAG,"i = "+i);
                Log.v("Compress", "Adding: " + _files[i]);
                Logger.d("Compress", "Adding: ");
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, 1024);

                String fileName = "";
                if(i==0){
                    fileName = _files[i].substring(_files[i].lastIndexOf("/") + 1);
                }else {
                    fileName = _files[i].substring(_files[i].lastIndexOf("/") + 2);
                }
                ZipEntry entry = new ZipEntry(fileName);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Logger.d(TAG, "inside onRequestPermissionsResult");
        String message = "";
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String support = SelectQueries.getSetting(this,Settings.SUPPORT_CALL);
            if("".equals(support)) {
                support = "+918652668310";
            }
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            String phNum = "tel:" + support;
            callIntent.setData(Uri.parse(phNum));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Please grant call permission in Manage Apps." + support + ""
                        , Toast.LENGTH_LONG).show();
                return;
            }
            startActivity(callIntent);

        }else if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            message = getString(R.string.phone);
        }

        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //ActivityCompat.requestPermissions(ActivityStart.this, new String[]{Manifest.permission.READ_PHONE_STATE}, MY_READ_PHONE_STATE);
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }

        });

        alertDialog.show();
    }



    public void startDayAlert(){
        new android.app.AlertDialog.Builder(MainActivity.this)
                .setMessage("Start your day now?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Intent intent = new Intent(MainActivity.this, StartDayActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();
                    }
                }).show();
    }

}