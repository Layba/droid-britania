package com.bizone.britannia;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.InsertQueries;
import com.bizone.britannia.queries.SelectQueries;
import com.crashlytics.android.Crashlytics;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.fabric.sdk.android.Fabric;

/**
 * Created by siddhesh on 7/25/16.
 */
public class LoginActivity extends AppCompatActivity{
    private static final String TAG=LoginActivity.class.getSimpleName();
    private static final int GET_MY_LOCATION = 1;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    String email;
    String password ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Fabric.with(this, new Crashlytics());
        }catch(Exception e){
            Logger.e(TAG,e);
            e.printStackTrace();
        }
        Logger.d(TAG, "inside onCreate");
        setContentView(R.layout.activity_login);

        String userPassword = SelectQueries.getSetting(this, Settings.PASSWORD);

        if(!"".equals(userPassword)){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

        String userName = SelectQueries.getSetting(this,Settings.USER_ID);
        mEmailView = (EditText) findViewById(R.id.email);
        if(!"".equals(userName)){
            mEmailView.setText(userName);
            mEmailView.setFocusable(false);
            mEmailView.setClickable(false);
        }

        mPasswordView = (EditText) findViewById(R.id.password);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        Logger.d(TAG,"Exitting onCreate");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        Logger.d(TAG, "inside onOptionsItemSelected");
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        Logger.d(TAG,"Exitting onOptionsItemSelected");
        return super.onOptionsItemSelected(menuItem);
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        Logger.d(TAG, "inside attemptLogin");
        if (mAuthTask != null) {
            return;
        }
        email = mEmailView.getText().toString();
        password = mPasswordView.getText().toString();
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.
        if (TextUtils.isEmpty(email) && !cancel) {
            mEmailView.setError("Username is required");
            focusView = mEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(password) && !cancel) {
            mPasswordView.setError("Password is required");
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            if(ContextCommons.isOnline(this)) {
//check permission here
                 if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CALL_PHONE,Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_PHONE_STATE}, GET_MY_LOCATION);

                } else  if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, GET_MY_LOCATION);

                } else if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CALL_PHONE,Manifest.permission.READ_CALL_LOG,Manifest.permission.READ_PHONE_STATE}, GET_MY_LOCATION);

                } else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE}, GET_MY_LOCATION);

                } else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, GET_MY_LOCATION);

                } else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.CALL_PHONE,
                            Manifest.permission.READ_CALL_LOG, Manifest.permission.READ_PHONE_STATE}, GET_MY_LOCATION);

                } else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, GET_MY_LOCATION);
                } else {

                    showProgress(true);
                    mAuthTask = new UserLoginTask(email, password);
                    mAuthTask.execute((Void) null);
                }

            }else{
                mPasswordView.setError("No internet connection. Please try again later.");
                mPasswordView.requestFocus();
            }
        }
        Logger.d(TAG,"Exitting attemptLogin");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Logger.d(TAG, "inside onRequestPermissionsResult");
        if (requestCode == GET_MY_LOCATION) {
            if (grantResults.length == 6 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED && grantResults[5] == PackageManager.PERMISSION_GRANTED) {

                showProgress(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            } else if (grantResults.length == 5 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED
                    && grantResults[4] == PackageManager.PERMISSION_GRANTED) {


            } else if (grantResults.length == 4 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED && grantResults[3] == PackageManager.PERMISSION_GRANTED) {
                showProgress(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);

            } else if (grantResults.length == 3 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                showProgress(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);

            } else if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                showProgress(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);

            } else if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showProgress(true);
                mAuthTask = new UserLoginTask(email, password);
                mAuthTask.execute((Void) null);
            } else {
                String message = "";
                if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                {
                    message = getString(R.string.loc_write_read);

                } else if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    message = getString(R.string.loc_write);


                } else if ((ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                {
                    message = getString(R.string.loc_read);

                } else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                        && (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED))
                {
                    message = getString(R.string.read_write);

                } else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    message = getString(R.string.write);

                }
                else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)
                {
                    message = getString(R.string.phone);

                } else if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                {
                    message = getString(R.string.loc);
                }

                AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
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

        }
        Logger.d(TAG, "exitting onRequestPermissionsResult");
    }


    private boolean isPasswordValid(String password) {
        Logger.d(TAG, "inside isPasswordValid");
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        Logger.d(TAG, "inside showProgress");
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        Logger.d(TAG, "exitting showProgress");
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private String errMsg;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            boolean retval = false;

            Logger.d(TAG,"inside doinbackround");

            BufferedReader inputStream = null;
            HttpURLConnection urlConnection = null;
            Integer result = 0;
            StringBuffer token=new StringBuffer();

            try {
            /* forming th java.net.URL object */
                URL url=new URL(Constants.LOGIN_URL);
                Logger.d(TAG, "url="+url.toString());
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoInput (true);
                urlConnection.setDoOutput (true);
                urlConnection.setUseCaches (false);
                urlConnection.setRequestMethod( "POST" );
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("X-CSRF-Token",token.toString());
                //Create JSONObject here
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("username", mEmail);
                jsonParam.put("password", mPassword);

                String imei = "123456789012345";
                try {
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    imei = telephonyManager.getDeviceId();
                }catch (Exception e){
                    Logger.e(TAG,e);
                    e.printStackTrace();
                }
                jsonParam.put("imei", imei);
                jsonParam.put("model", Commons.getDeviceName());

                DataOutputStream printout = new DataOutputStream (urlConnection.getOutputStream ());
                printout.writeBytes(jsonParam.toString());
                printout.flush();
                printout.close();
                int statusCode = urlConnection.getResponseCode();
                Logger.d(TAG,"statusCode="+statusCode);
                token=new StringBuffer();
                     /* 200 represents HTTP OK */
                if (statusCode == 200) {
                    inputStream = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(),"utf-8"));
                    String line;
                    while ((line = inputStream.readLine()) != null) {
                        token.append(line);
                    }
                    inputStream.close();
                    Logger.d(TAG, "token=" + token);
                    retval=saveUsrPass(LoginActivity.this,token.toString(),mEmail,mPassword);
                    if(!retval){
                        errMsg = "Error while login. Please try again later.";
                    }
                }else{
                    String resMsg;
                    resMsg = urlConnection.getResponseMessage();
                    Logger.d(TAG,"resMsg=" + resMsg);
                    if("".equals(resMsg)) {
                        errMsg = Commons.getWSErrors(statusCode);
                    }else {
                        errMsg = resMsg;
                    }
                }

            }catch (Exception e){
                errMsg="Error processing Username and Password";
                Logger.e(TAG,e);
                e.printStackTrace();
            }

            return retval;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            Logger.d(TAG, "onPostExecute="+success);
            if (success) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            } else {
                mPasswordView.setError(errMsg);
                mPasswordView.requestFocus();
            }
            Logger.d(TAG, "exitting onPostExecute");
        }

        @Override
        protected void onCancelled() {
            Logger.d(TAG,"inside onCancelled");
            mAuthTask = null;
            showProgress(false);
            Logger.d(TAG,"exitting onCancelled");
        }

        private boolean saveUsrPass(Context context, String jsonString,String mEmail,String mPassword){
            boolean retval=false;
            Logger.d(TAG,"inside saveUsrPass");
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                String session_name = jsonObject.getString("session_name");
                String session_id = jsonObject.getString("sessid");
                String csrfToken = jsonObject.getString("token");
                String deviceId = jsonObject.getString("device_id");
                String state = jsonObject.getString("state");
                String gallery = jsonObject.getString("gallery");

                InsertQueries.setSetting(context, Settings.USER_ID, mEmail);
                InsertQueries.setSetting(context, Settings.PASSWORD, mPassword);
                InsertQueries.setSetting(context, Settings.DEVICE_ID, deviceId);
                InsertQueries.setSetting(context, Settings.SESS_NAME_ID, session_name + "=" + session_id);
                InsertQueries.setSetting(context, Settings.TOKEN, csrfToken);
                InsertQueries.setSetting(context, Settings.STATE, state);
                InsertQueries.setSetting(context, Settings.GALLERY, gallery);

                JSONObject settingObject = jsonObject.getJSONObject("settings");

                String past = settingObject.optString("edit_past_days", "0");
                String future = settingObject.optString("edit_future_days","0");
                String support  = settingObject.getString("helpdesk_number");

                InsertQueries.setSetting(context, Settings.PAST_DAYS, past);
                InsertQueries.setSetting(context, Settings.FUTURE_DAYS, future);
                InsertQueries.setSetting(context, Settings.SUPPORT_CALL, support);

                retval = true;
            }catch (Exception e){
                Logger.e(TAG,e);
                e.printStackTrace();
            }
            Logger.d(TAG,"inside saveUsrPass : retval = "+retval);
            return retval;
        }
    }

}
