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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.NetworkRequests.Utils;

public class LoginActivity extends Activity {

    EditText etName, etLastName, etEmail, etPassword;

    RelativeLayout rlLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName = (EditText) findViewById(R.id.et_name);
        etLastName = (EditText) findViewById(R.id.et_last_name);
        etEmail = (EditText) findViewById(R.id.et_email);
        etPassword = (EditText) findViewById(R.id.et_password);
        rlLoading = (RelativeLayout) findViewById(R.id.rl_loading);

        // Hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //hideKeyboard();

    }

    public void login (View view) {

        hideKeyboard();

        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (email.trim().equals("")) {
            etEmail.setError(getString(R.string.mandatory_field));
        } else if (password.trim().equals("")) {
            etPassword.setError(getString(R.string.mandatory_field));
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

        String name = etName.getText().toString();
        String lastName = etLastName.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if (name.trim().equals("")){
            etName.setError(getString(R.string.mandatory_field));
        } else if (lastName.trim().equals("")) {
            etLastName.setError(getString(R.string.mandatory_field));
        } else if (email.trim().equals("")) {
            etEmail.setError(getString(R.string.mandatory_field));
        } else if (password.length() < 8) {
            etPassword.setError(getString(R.string.password_too_short));
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

        String email = etEmail.getText().toString();

        if (email.trim().equals("")) {
            etEmail.setError(getString(R.string.mandatory_field));
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
        etName.setVisibility(View.GONE);
        etLastName.setVisibility(View.GONE);
        etPassword.setVisibility(View.VISIBLE);

        Button btRegister = (Button) findViewById(R.id.bt_register);
        btRegister.setVisibility(View.GONE);
        Button btLogin = (Button) findViewById(R.id.bt_login);
        btLogin.setVisibility(View.VISIBLE);
        Button btRecoverPassword = (Button) findViewById(R.id.bt_recover_password);
        btRecoverPassword.setVisibility(View.GONE);

        TextView tvLogin = (TextView) findViewById(R.id.tv_login);
        tvLogin.setVisibility(View.VISIBLE);
        TextView tvRegister = (TextView) findViewById(R.id.tv_register);
        tvRegister.setVisibility(View.GONE);
        TextView tvRecoverPassword = (TextView) findViewById(R.id.tv_recover_password);
        tvRecoverPassword.setVisibility(View.GONE);

        TextView tvSwitchToRegister = (TextView) findViewById(R.id.tv_switch_to_register);
        tvSwitchToRegister.setVisibility(View.VISIBLE);
        TextView tvSwitchToLogin = (TextView) findViewById(R.id.tv_switch_to_login);
        tvSwitchToLogin.setVisibility(View.GONE);
        TextView tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        tvForgotPassword.setVisibility(View.VISIBLE);

        etPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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
        etName.setVisibility(View.VISIBLE);
        etLastName.setVisibility(View.VISIBLE);
        etPassword.setVisibility(View.VISIBLE);

        Button btRegister = (Button) findViewById(R.id.bt_register);
        btRegister.setVisibility(View.VISIBLE);
        Button btLogin = (Button) findViewById(R.id.bt_login);
        btLogin.setVisibility(View.GONE);
        Button btRecoverPassword = (Button) findViewById(R.id.bt_recover_password);
        btRecoverPassword.setVisibility(View.GONE);

        TextView tvLogin = (TextView) findViewById(R.id.tv_login);
        tvLogin.setVisibility(View.GONE);
        TextView tvRegister = (TextView) findViewById(R.id.tv_register);
        tvRegister.setVisibility(View.VISIBLE);
        TextView tvRecoverPassword = (TextView) findViewById(R.id.tv_recover_password);
        tvRecoverPassword.setVisibility(View.GONE);

        TextView tvSwitchToRegister = (TextView) findViewById(R.id.tv_switch_to_register);
        tvSwitchToRegister.setVisibility(View.GONE);
        TextView tvSwitchToLogin = (TextView) findViewById(R.id.tv_switch_to_login);
        tvSwitchToLogin.setVisibility(View.VISIBLE);
        TextView tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        tvForgotPassword.setVisibility(View.GONE);

        etPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
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
        etName.setVisibility(View.GONE);
        etLastName.setVisibility(View.GONE);
        etPassword.setVisibility(View.GONE);

        Button btRegister = (Button) findViewById(R.id.bt_register);
        btRegister.setVisibility(View.GONE);
        Button btLogin = (Button) findViewById(R.id.bt_login);
        btLogin.setVisibility(View.GONE);
        Button btRecoverPassword = (Button) findViewById(R.id.bt_recover_password);
        btRecoverPassword.setVisibility(View.VISIBLE);

        TextView tvLogin = (TextView) findViewById(R.id.tv_login);
        tvLogin.setVisibility(View.GONE);
        TextView tvRegister = (TextView) findViewById(R.id.tv_register);
        tvRegister.setVisibility(View.GONE);
        TextView tvRecoverPassword = (TextView) findViewById(R.id.tv_recover_password);
        tvRecoverPassword.setVisibility(View.VISIBLE);

        TextView tvSwitchToRegister = (TextView) findViewById(R.id.tv_switch_to_register);
        tvSwitchToRegister.setVisibility(View.VISIBLE);
        TextView tvSwitchToLogin = (TextView) findViewById(R.id.tv_switch_to_login);
        tvSwitchToLogin.setVisibility(View.VISIBLE);
        TextView tvForgotPassword = (TextView) findViewById(R.id.tv_forgot_password);
        tvForgotPassword.setVisibility(View.GONE);

        etPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                    registerUser(null);
                }
                return false;
            }
        });
    }

    public void hideKeyboard () {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etLastName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etEmail.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
    }

}

