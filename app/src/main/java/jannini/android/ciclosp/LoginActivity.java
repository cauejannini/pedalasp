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
    Button btOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        rlLoading = (RelativeLayout) findViewById(R.id.rl_loading);
        btOk = (Button) findViewById(R.id.bt_ok);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        // Hide keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //hideKeyboard();

    }

    public void login () {

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

                        String token = job.getString("token");

                        Constant.TOKEN = token;

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constant.SPKEY_TOKEN, token);
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

                        case 401:
                            Utils.showToastWithMessage(LoginActivity.this, getString(R.string.wrong_password));
                            break;
                        case 404:
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

    public void registerUser () {

        hideKeyboard();

        EditText etRegisterName = (EditText) findViewById(R.id.et_register_name);
        EditText etRegisterLastName = (EditText) findViewById(R.id.et_register_last_name);
        EditText etRegisterEmail = (EditText) findViewById(R.id.et_register_email);
        EditText etRegisterCity = (EditText) findViewById(R.id.et_register_city);
        EditText etRegisterPassword = (EditText) findViewById(R.id.et_register_password);
        EditText etRegisterConfirmPassword = (EditText) findViewById(R.id.et_register_confirm_password);

        String name = etRegisterName.getText().toString();
        String lastName = etRegisterLastName.getText().toString();
        String email = etRegisterEmail.getText().toString();
        String city = etRegisterCity.getText().toString();
        String password = etRegisterPassword.getText().toString();
        String confirmPassword = etRegisterConfirmPassword.getText().toString();

        if (name.trim().equals("")){
            etRegisterName.setError(getString(R.string.mandatory_field));
        } else if (lastName.trim().equals("")) {
            etRegisterLastName.setError(getString(R.string.mandatory_field));
        } else if (email.trim().equals("")) {
            etRegisterEmail.setError(getString(R.string.mandatory_field));
        } else if (city.trim().equals("")) {
            etRegisterCity.setError(getString(R.string.mandatory_field));
        } else if (password.length() < 8) {
            etRegisterPassword.setError(getString(R.string.password_too_short));
        } else if (!password.equals(confirmPassword)) {
            etRegisterConfirmPassword.setError(getString(R.string.password_do_not_match));
        } else {
            rlLoading.setVisibility(View.VISIBLE);
            Calls.registerUser(name, lastName, email, password, new CallHandler() {
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    try {
                        JSONObject job = new JSONObject(response);

                        int userId = job.getInt("id");
                        String name = job.getString("name");
                        String lastName = job.getString("last_name");
                        String email = job.getString("email");
                        String token = job.getString("token");

                        Constant.USER_NAME = name;
                        Constant.USER_LAST_NAME = lastName;
                        Constant.USER_EMAIL = email;
                        Constant.TOKEN = token;

                        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(Constant.SPKEY_TOKEN, Constant.TOKEN);
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

    public void recoverPassword () {
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
                    login();
                }
                return false;
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
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
                    registerUser();
                }
                return false;
            }
        });

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
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
                    recoverPassword();
                }
                return false;
            }
        });
        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recoverPassword();
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

