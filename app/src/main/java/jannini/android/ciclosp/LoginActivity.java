package jannini.android.ciclosp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.Utils;

public class LoginActivity extends Activity {

    RelativeLayout rlLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rlLoading = (RelativeLayout) findViewById(R.id.rl_loading);

        // Hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //hideKeyboard();

    }

    public void login (View view) {

        hideKeyboard();

        EditText etLoginEmail = (EditText) findViewById(R.id.et_login_email);
        EditText etLoginPassword = (EditText) findViewById(R.id.et_login_password);

        String email = etLoginEmail.getText().toString();
        String password = etLoginPassword.getText().toString();

        if (email.trim().equals("")) {
            etLoginEmail.setError(getString(R.string.mandatory_field));
        } else if (password.trim().equals("")) {
            etLoginPassword.setError(getString(R.string.mandatory_field));
        } else {

            rlLoading.setVisibility(View.VISIBLE);

            Calls.login(email, password, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    try {
                        JSONObject job = new JSONObject(response);

                        int userId = job.getInt("USER_ID");
                        String name = job.getString("NAME");
                        String lastName = job.getString("LAST_NAME");
                        String email = job.getString("EMAIL");

                        Constant.USER_ID = userId;
                        Constant.USER_NAME = name;
                        Constant.USER_LAST_NAME = lastName;
                        Constant.USER_EMAIL = email;

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constant.SPKEY_USER_ID, userId);
                        editor.putBoolean(Constant.SPKEY_USER_LOGGED_IN, true);
                        editor.apply();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utils.showServerErrorToast(LoginActivity.this, response);
                    }

                    rlLoading.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    rlLoading.setVisibility(View.GONE);

                    switch (responseCode) {
                        case 400:
                            Utils.showToastWithMessage(LoginActivity.this, getString(R.string.email_not_registered));
                            break;
                        case 401:
                            Utils.showToastWithMessage(LoginActivity.this, getString(R.string.wrong_password));
                            break;
                        default:
                            Utils.showServerErrorToast(LoginActivity.this, response);
                            break;
                    }
                }
            });
        }
    }

    public void registerUser (View view) {

        hideKeyboard();

        EditText etRegisterName = (EditText) findViewById(R.id.et_register_name);
        EditText etRegisterLastName = (EditText) findViewById(R.id.et_register_last_name);
        EditText etRegisterEmail = (EditText) findViewById(R.id.et_register_email);
        EditText etRegisterPassword = (EditText) findViewById(R.id.et_register_password);

        String name = etRegisterName.getText().toString();
        String lastName = etRegisterLastName.getText().toString();
        String email = etRegisterEmail.getText().toString();
        String password = etRegisterPassword.getText().toString();

        if (name.trim().equals("")){
            etRegisterName.setError(getString(R.string.mandatory_field));
        } else if (lastName.trim().equals("")) {
            etRegisterLastName.setError(getString(R.string.mandatory_field));
        } else if (email.trim().equals("")) {
            etRegisterEmail.setError(getString(R.string.mandatory_field));
        } else if (password.length() < 8) {
            etRegisterPassword.setError(getString(R.string.password_too_short));
        } else {

            rlLoading.setVisibility(View.VISIBLE);
            Calls.registerUser(name, lastName, email, password, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    try {
                        JSONObject job = new JSONObject(response);

                        int userId = job.getInt("USER_ID");
                        String name = job.getString("NAME");
                        String lastName = job.getString("LAST_NAME");
                        String email = job.getString("EMAIL");

                        Constant.USER_ID = userId;
                        Constant.USER_NAME = name;
                        Constant.USER_LAST_NAME = lastName;
                        Constant.USER_EMAIL = email;

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putInt(Constant.SPKEY_USER_ID, userId);
                        editor.putBoolean(Constant.SPKEY_USER_LOGGED_IN, true);
                        editor.apply();

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Utils.showServerErrorToast(LoginActivity.this, response);
                    }

                    rlLoading.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    rlLoading.setVisibility(View.GONE);

                    switch (responseCode) {
                        case 400:
                            Utils.showToastWithMessage(LoginActivity.this, getString(R.string.email_already_registered));
                            break;
                        default:
                            Utils.showServerErrorToast(LoginActivity.this, response);
                            break;
                    }

                }
            });
        }
    }

    public void recoverPassword (View view) {
        hideKeyboard();

        EditText etRecoverEmail = (EditText) findViewById(R.id.et_recover_email);

        String email = etRecoverEmail.getText().toString();

        if (email.trim().equals("")) {
            etRecoverEmail.setError(getString(R.string.mandatory_field));
        } else {

            rlLoading.setVisibility(View.VISIBLE);
            Calls.recoverPassword(email, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    rlLoading.setVisibility(View.GONE);
                    Utils.showToastWithMessage(LoginActivity.this, getString(R.string.recovery_email_sent));

                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    rlLoading.setVisibility(View.GONE);

                    switch (responseCode) {
                        case 400:
                            Utils.showToastWithMessage(LoginActivity.this, getString(R.string.email_not_registered));
                            break;
                        default:
                            Utils.showServerErrorToast(LoginActivity.this, response);
                            break;
                    }
                }
            });
        }
    }

    public void switchToLogin (View view) {

        ScrollView svRegister = (ScrollView) findViewById(R.id.sv_register);
        svRegister.setVisibility(View.GONE);
        ScrollView svRecoverPassword = (ScrollView) findViewById(R.id.sv_recover_password);
        svRecoverPassword.setVisibility(View.GONE);
        ScrollView svLogin = (ScrollView) findViewById(R.id.sv_login);
        svLogin.setVisibility(View.VISIBLE);

        EditText etLoginPassword = (EditText) findViewById(R.id.et_login_password);
        etLoginPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                    login(null);
                }
                return false;
            }
        });
    }

    public void switchToRegister (View view) {
        ScrollView svRegister = (ScrollView) findViewById(R.id.sv_register);
        svRegister.setVisibility(View.VISIBLE);
        ScrollView svRecoverPassword = (ScrollView) findViewById(R.id.sv_recover_password);
        svRecoverPassword.setVisibility(View.GONE);
        ScrollView svLogin = (ScrollView) findViewById(R.id.sv_login);
        svLogin.setVisibility(View.GONE);

        EditText etRegisterPassword = (EditText) findViewById(R.id.et_register_password);
        etRegisterPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                    registerUser(null);
                }
                return false;
            }
        });
    }

    public void switchToRecoverPassword (View view) {
        ScrollView svRegister = (ScrollView) findViewById(R.id.sv_register);
        svRegister.setVisibility(View.GONE);
        ScrollView svRecoverPassword = (ScrollView) findViewById(R.id.sv_recover_password);
        svRecoverPassword.setVisibility(View.VISIBLE);
        ScrollView svLogin = (ScrollView) findViewById(R.id.sv_login);
        svLogin.setVisibility(View.GONE);

        EditText etRecoverEmail = (EditText) findViewById(R.id.et_recover_email);
        etRecoverEmail.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                    recoverPassword(null);
                }
                return false;
            }
        });
    }

    public void hideKeyboard () {

        ArrayList<EditText> etList = new ArrayList<>();
        etList.add((EditText) findViewById(R.id.et_login_email));
        etList.add((EditText) findViewById(R.id.et_login_password));
        etList.add((EditText) findViewById(R.id.et_register_name));
        etList.add((EditText) findViewById(R.id.et_register_last_name));
        etList.add((EditText) findViewById(R.id.et_register_email));
        etList.add((EditText) findViewById(R.id.et_register_password));
        etList.add((EditText) findViewById(R.id.et_recover_email));

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        for (EditText et : etList) {
            imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
        }
    }

}

