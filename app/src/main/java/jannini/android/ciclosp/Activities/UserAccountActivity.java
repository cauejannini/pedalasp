package jannini.android.ciclosp.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;
import jannini.android.ciclosp.Utils;
import jannini.android.ciclosp.R;

public class UserAccountActivity extends Activity {

    SharedPreferences sharedPreferences;
    RelativeLayout rlLoading;

    TextView tvName, tvEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        sharedPreferences = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, MODE_PRIVATE);

        rlLoading = (RelativeLayout) findViewById(R.id.rl_loading);
        rlLoading.setVisibility(View.VISIBLE);

        tvName = (TextView) findViewById(R.id.tv_name);
        tvEmail = (TextView) findViewById(R.id.tv_email);

        String token = sharedPreferences.getString(Constant.SPKEY_TOKEN, "");
        if (!token.trim().equals("")) {
            Constant.TOKEN = token;
            Calls.getUser(token, getUserCallHandler);
        } else {
            logOut(null);
        }

    }

    public void editAccount(View view) {
        LinearLayout llYourAccount = (LinearLayout) findViewById(R.id.ll_your_account);
        llYourAccount.setVisibility(View.GONE);
        LinearLayout llEditAccount = (LinearLayout) findViewById(R.id.ll_edit_your_account);
        llEditAccount.setVisibility(View.VISIBLE);
    }

    public void cancelEditAccount (View view) {
        LinearLayout llEditAccount = (LinearLayout) findViewById(R.id.ll_edit_your_account);
        llEditAccount.setVisibility(View.GONE);
        LinearLayout llYourAccount = (LinearLayout) findViewById(R.id.ll_your_account);
        llYourAccount.setVisibility(View.VISIBLE);
    }

    public void updateAccount(View view) {
        EditText etName = (EditText) findViewById(R.id.et_name);
        EditText etLastName = (EditText) findViewById(R.id.et_last_name);
        EditText etPassword = (EditText) findViewById(R.id.et_password);

        String name = etName.getText().toString();
        String lastName = etLastName.getText().toString();
        String password = etPassword.getText().toString();

        if (name.trim().equals("")) {
            etName.setError(getString(R.string.mandatory_field));
        } else if (lastName.trim().equals("")) {
            etLastName.setError(getString(R.string.mandatory_field));
        } else if (password.trim().equals("")) {
            etPassword.setError(getString(R.string.mandatory_field));
        } else {

            Calls.updateUserAccount(Constant.TOKEN, name, lastName, password, new CallHandler(){
                @Override
                public void onSuccess(int responseCode, String response) {
                    super.onSuccess(responseCode, response);

                    Utils.showToastWithMessage(UserAccountActivity.this, getString(R.string.account_updated));
                    rlLoading.setVisibility(View.VISIBLE);
                    Calls.getUser(Constant.TOKEN, getUserCallHandler);
                }

                @Override
                public void onFailure(int responseCode, String response) {
                    super.onFailure(responseCode, response);

                    switch (responseCode) {

                        case 401:
                            Utils.showToastWithMessage(UserAccountActivity.this, getString(R.string.wrong_password));
                            break;
                        case 404:
                            // USER ID INEXISTENT
                            Utils.showServerErrorToast(UserAccountActivity.this, response);
                            break;
                        case 500:
                            Utils.showServerErrorToast(UserAccountActivity.this, response);
                            break;
                    }
                }
            });
        }
    }

    CallHandler getUserCallHandler = new CallHandler() {
        @Override
        public void onSuccess(int responseCode, String response) {
            super.onSuccess(responseCode, response);
            Log.e("getUser", "SUCCESS: " + response);

            try {
                JSONObject job = new JSONObject(response);
                Constant.USER_NAME = job.getString("name");
                Constant.USER_LAST_NAME = job.getString("last_name");
                Constant.USER_EMAIL = job.getString("email");

                tvName.setText(Constant.USER_NAME+" "+Constant.USER_LAST_NAME);
                tvEmail.setText(Constant.USER_EMAIL);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            rlLoading.setVisibility(View.GONE);

        }

        @Override
        public void onFailure(int responseCode, String response) {
            super.onFailure(responseCode, response);
            Log.e("getUser", "FAIL: " + response);

            Utils.showServerErrorToast(UserAccountActivity.this, response);
            finish();

        }
    };

    public void logOut(View view) {

        sharedPreferences.edit().remove(Constant.SPKEY_TOKEN).apply();

        startActivity(new Intent(UserAccountActivity.this, LoginActivity.class));
        finish();
    }
}
