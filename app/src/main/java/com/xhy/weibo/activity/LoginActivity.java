package com.xhy.weibo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.xhy.weibo.AppConfig;
import com.xhy.weibo.R;
import com.xhy.weibo.base.BaseActivity;
import com.xhy.weibo.AccessToken;
import com.xhy.weibo.constants.CommonConstants;
import com.xhy.weibo.db.DBManager;
import com.xhy.weibo.db.UserDB;
import com.xhy.weibo.model.Login;
import com.xhy.weibo.logic.UserLoginLogic;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements LoaderCallbacks<Cursor>, UserLoginLogic.LoginCallback {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private View focusView;
    private UserDB userDB;
    private String account;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.username);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
//                DBManager dbManager = new DBManager(LoginActivity.this);
//                dbManager.openDatabase();
//                SQLiteDatabase db = dbManager.getDatabase();
//                ContentValues values = new ContentValues();
//                values.put("id", 9);
//                values.put("account", "admin");
//                values.put("password", "admin");
//                values.put("registime", 1457849279);
//                values.put("token", "WlxaRAFIBRcEGgJQVBsFGwYUVEsHLwY2AGJdPA47AGoAYFE7BzVYO1Iw");
//                values.put("tokenStartTime", System.currentTimeMillis());
//                values.put("username", "admin");
//                values.put("face", "2015/sdassad.jpg");
//                values.put("used", 1);
//
//                db.insert(DatabaseHelper.USER_TABLE, null, values);
//                Cursor cursor = db.rawQuery("select * from user", null);
//                if (cursor.moveToFirst()) {
//                    StringBuilder content = new StringBuilder();
//                    do {
//                        content.append("id:").append(cursor.getInt(cursor.getColumnIndex("id")));
//                        content.append(",account:").append(cursor.getString(cursor.getColumnIndex("account")));
//                        content.append(",password:").append(cursor.getString(cursor.getColumnIndex("password")));
//                        content.append(",registime:").append(cursor.getInt(cursor.getColumnIndex("registime")));
//                        content.append(",token:").append(cursor.getString(cursor.getColumnIndex("token")));
//                        content.append(",used:").append(cursor.getInt(cursor.getColumnIndex("used")));
//                    } while (cursor.moveToNext());
//                    Logger.show("--->", content.toString());
//                }
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        userDB = new UserDB(this);

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUsernameView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    private void attemptLogin() {
//        if (mAuthTask != null) {
//            return;
//        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        account = mUsernameView.getText().toString();
        password = mPasswordView.getText().toString();

        boolean cancel = false;
        focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(account)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
           /* GsonRequest<LoginReciver> request = new GsonRequest<LoginReciver>(Request.Method.POST,
                    URLs.WEIBO_USER_LOGIN, LoginReciver.class, null,
                    new Response.Listener<LoginReciver>() {
                        @Override
                        public void onResponse(LoginReciver response) {
                            if (response.getCode() == 200) {
                                Login login = response.getInfo();
                                //设置密码 请求返回中不会返回密码 返回不安全
                                login.setTokenStartTime(System.currentTimeMillis());
                                login.setPassword(password);

                                //数据库操作
                                DBManager dbManager = new DBManager(LoginActivity.this);
                                dbManager.openDatabase();
                                SQLiteDatabase db = dbManager.getDatabase();
                                if (userDB.insertLogin(db, login)) {
                                    AppConfig.ACCESS_TOKEN = AccessToken.getInstance(login.getAccount(), password, LoginActivity.this);
                                    AppConfig.ACCESS_TOKEN.setToken(login.getToken());
                                    AppConfig.ACCESS_TOKEN.setTokenStartTime(login.getTokenStartTime());
                                    intent2Activity(InitActivity.class);
                                    finish();
                                } else {
                                    showProgress(false);
                                    mPasswordView.setError("数据库错误");
                                    focusView = mPasswordView;
                                    focusView.requestFocus();
                                }
                                dbManager.closeDatabase();

                            } else {
                                showProgress(false);
                                mPasswordView.setError(response.getError());
                                focusView = mPasswordView;
                                focusView.requestFocus();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showProgress(false);
                    mPasswordView.setError("错误,请稍后重试");
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("account", account);
                    map.put("password", password);
                    return map;
                }
            };
            /*VolleyQueueSingleton.getInstance(this).addToRequestQueue(request);*/
            UserLoginLogic.login(this, account, password, this);
//            mAuthTask = new UserLoginTask(account, password);
//            mAuthTask.execute((Void) null);
        }
    }

    @Override
    public void onLoginSuccess(Login login) {
        //设置密码 请求返回中不会返回密码 返回不安全
        login.setTokenStartTime(System.currentTimeMillis());
        login.setPassword(password);
        //数据库操作
        DBManager dbManager = new DBManager(LoginActivity.this);
        dbManager.openDatabase();
        SQLiteDatabase db = dbManager.getDatabase();
        if (userDB.insertLogin(db, login)) {
            AppConfig.ACCESS_TOKEN = AccessToken.getInstance(login.getAccount(), password, LoginActivity.this);
            AppConfig.ACCESS_TOKEN.setToken(login.getToken());
            AppConfig.ACCESS_TOKEN.setTokenStartTime(login.getTokenStartTime());
            intent2Activity(InitActivity.class);
            finish();
        } else {
            showProgress(false);
            mPasswordView.setError("数据库错误");
            focusView = mPasswordView;
            focusView.requestFocus();
        }
        dbManager.closeDatabase();
    }

    @Override
    public void onLoginFailure(int errorCode, String errorMessage) {
        showProgress(false);
        mPasswordView.setError(errorMessage);
        focusView = mPasswordView;
        focusView.requestFocus();
    }

    @Override
    public void onLoginError(Throwable error) {
        showProgress(false);
        mPasswordView.setError("错误,请稍后重试");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
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
            // The ViewPropertyAnimator APIs are not available, so simply show
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> users = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            users.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(users);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mUsernameView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
//            mAuthTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
//            mAuthTask = null;
            showProgress(false);
        }
    }
}

