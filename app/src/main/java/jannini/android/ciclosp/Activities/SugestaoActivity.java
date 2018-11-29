package jannini.android.ciclosp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.MyApplication;
import jannini.android.ciclosp.R;
import jannini.android.ciclosp.Utils;

public class SugestaoActivity extends Activity {
	
	String nome;
	String email;
	String mensagem;
	
	EditText et_nome;
	EditText et_email;
	EditText et_mensagem;

	SharedPreferences sp;

	String placeId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sugestao);

		et_nome = (EditText) findViewById(R.id.nome);
		et_email = (EditText) findViewById(R.id.email);
		et_mensagem = (EditText) findViewById(R.id.mensagem);

		sp = getApplicationContext().getSharedPreferences(Constant.SPKEY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
		et_nome.setText(sp.getString(Constant.SPKEY_USER_NAME, ""));
		et_email.setText(sp.getString(Constant.SPKEY_USER_EMAIL, ""));

		Intent i = getIntent();
		String placeName = i.getStringExtra(Constant.IEXTRA_PLACE_NAME);
		placeId = String.valueOf(i.getIntExtra(Constant.IEXTRA_PLACE_ID_INT, 0));

		if (placeName != null && !placeId.equals("0")) {
			if (!placeName.equals("") && !placeId.equals("")){
				et_mensagem.setText("Olá, " + Utils.newline + Utils.newline + "Sou o proprietário do estabelecimento " + placeName + " e gostaria de confirmar as informações no mapa do Pedala SP.");
			}
		}
		
	}
	
	public void sendEmail (View v){

		nome = et_nome.getText().toString();
		email = et_email.getText().toString();

		mensagem = et_mensagem.getText().toString();

		if (!placeId.equals("")) {
			mensagem += " | Place ID: " + placeId;
		}

		new postData().execute();

		SharedPreferences.Editor editor = sp.edit();
		editor.putString(Constant.SPKEY_USER_NAME, nome);
		editor.putString(Constant.SPKEY_USER_EMAIL, email);
		editor.apply();

		Toast.makeText(getApplicationContext(), R.string.obrigado_feedback, Toast.LENGTH_LONG).show();
		
		finish();
	}
	
	//Class que é uma AsyncTask para enviar os dados para o PHP do servidor que vai executar o envio do email.
	class postData extends AsyncTask<String, String, String>{
		
		protected String doInBackground(String... args) {

            try {

                URL url = new URL(Constant.url_send_email);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestMethod("POST");

                // Criar o OutputStream para carregar a mensagem
                OutputStream os = connection.getOutputStream();
                BufferedWriter buffWriter = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                buffWriter.write("subject="+"Sugestão Pedala SP"+"&"+
                                "body="+mensagem+"&"+
                                "from="+email+"&"+
                                "name="+nome
                );
                buffWriter.flush();
                buffWriter.close();
                os.close();

                connection.connect();

                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                is.close();
                String response = sb.toString();

                Log.i("Sugestao.postData r", response);

            } catch (IOException e) {
                e.printStackTrace();
            }
		    
		    return null;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sugestao, menu);
		//getActionBar().setDisplayShowTitleEnabled(false);
		return true;
	}
	
	@Override
	protected void onResume() {
	  super.onResume();
	  MyApplication.activityResumed();
	}

	@Override
	protected void onPause() {
	  super.onPause();
	  MyApplication.activityPaused();
	}

}
