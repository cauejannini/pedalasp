package jannini.android.ciclosp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import jannini.android.ciclosp.Fragments.EstabelecimentoCategoryF;
import jannini.android.ciclosp.Fragments.EstabelecimentoInfoF;
import jannini.android.ciclosp.NetworkRequests.CallHandler;
import jannini.android.ciclosp.NetworkRequests.Calls;

public class AddEstabelecimentoActivity extends Activity
    implements  EstabelecimentoInfoF.OnEIFragmentInteractionListener,
                EstabelecimentoCategoryF.OnECFragmentInteractionListener{

    public static LatLng userLatLng;

    String name, phone, address;

    LatLng location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_estabelecimento);


        Double lat = getIntent().getDoubleExtra("lat", Constant.latLngCity.latitude);
        Double lng = getIntent().getDoubleExtra("lng", Constant.latLngCity.longitude);
        userLatLng = new LatLng(lat, lng);

        Fragment frag1 = EstabelecimentoInfoF.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, frag1).commit();

    }

    CallHandler addEstabelecimentoHandler = new CallHandler() {
        @Override
        public void onSuccess (int responseCode, String response) {
            Log.e("addEstabHandler", "SUCCESS" + ": " + response);
            Toast.makeText(AddEstabelecimentoActivity.this, "Estabelecimento enviado", Toast.LENGTH_SHORT).show();
            finish();
        }

        @Override
        public void onFailure (int responseCode, String response) {
            Log.e("addEstabHandler", "FAIL: " + responseCode + " " + response);
            Toast.makeText(AddEstabelecimentoActivity.this, "Erro", Toast.LENGTH_SHORT).show();
        }

    };

    @Override
    public void onEIFragmentInteraction(String strName, String strPhone, String strAddress, LatLng latLng) {
        name = strName;
        phone = strPhone;
        address = strAddress;
        location = latLng;

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, EstabelecimentoCategoryF.newInstance()).addToBackStack(null).commit();
    }

    @Override
    public void onECFragmentInteraction(boolean store, boolean workshop, boolean shower, boolean coffee, String other) {

        Calls.addEstabelecimento(name, address, String.valueOf(location.latitude), String.valueOf(location.longitude), phone, store, workshop, shower, coffee, other, addEstabelecimentoHandler);

    }
}
