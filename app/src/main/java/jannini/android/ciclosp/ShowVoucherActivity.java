package jannini.android.ciclosp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import jannini.android.ciclosp.NetworkRequests.Utils;

public class ShowVoucherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_voucher);

        Intent intent = getIntent();
        String json = intent.getStringExtra(Constant.IEXTRA_VOUCHER_JSON);

        parseJson(json);

        RelativeLayout rlBackButton = (RelativeLayout) findViewById(R.id.rl_back_button);
        rlBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    public void parseJson(String json) {
        try {
            JSONObject job = new JSONObject(json);

            String responseCode = job.getString("RESPONSE_CODE");

            ProgressBar pb = (ProgressBar) findViewById(R.id.pb_loading_voucher);
            pb.setVisibility(View.GONE);

            switch (responseCode) {
                case Constant.API_RESPONSE_CODE_VOUCHER_LIMIT_REACHED_FOR_USER:
                    TextView tvVoucherLimitReached = (TextView) findViewById(R.id.tv_voucher_limit_reached);
                    tvVoucherLimitReached.setVisibility(View.VISIBLE);
                    break;
                case Constant.API_RESPONSE_CODE_VOUCHER_LIMIT_REACHED_FOR_DEAL:
                    TextView tvVoucherDealLimitReached = (TextView) findViewById(R.id.tv_voucher_deal_limit_reached);
                    tvVoucherDealLimitReached.setVisibility(View.VISIBLE);
                    break;
                case Constant.API_RESPONSE_CODE_VOUCHER_OK:

                    String voucherText = job.getString("EXPLANATION");
                    String code = job.getString("CODE");
                    String expDate = job.getString("EXPIRATION_DATE");
                    String expHour = job.getString("EXPIRATION_HOUR");

                    LinearLayout llVoucherDetails = (LinearLayout) findViewById(R.id.ll_voucher_details);
                    llVoucherDetails.setVisibility(View.VISIBLE);
                    TextView tvVoucherText = (TextView) findViewById(R.id.tv_voucher_explanation);
                    tvVoucherText.setText(voucherText);
                    TextView tvVoucherCode = (TextView) findViewById(R.id.tv_voucher_code);
                    tvVoucherCode.setText(code);
                    TextView tvVoucherValidity = (TextView) findViewById(R.id.tv_voucher_validity);
                    tvVoucherValidity.setText("Código valido até "+expDate+" às "+expHour);
                    break;
                default:
                    Utils.showErrorToast(this);
                    finish();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            Utils.showErrorToast(this);
            finish();
        }
    }
}
