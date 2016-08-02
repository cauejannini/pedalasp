package jannini.android.ciclosp.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jannini.android.ciclosp.AddEstabelecimentoActivity;
import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EstabelecimentoInfoF.OnEIFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EstabelecimentoInfoF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstabelecimentoInfoF extends Fragment implements OnMapReadyCallback {

    private OnEIFragmentInteractionListener mListener;

    GoogleMap gMap;
    Marker markerShop;
    Geocoder geocoder;

    List<Address> addressList = new ArrayList<>();
    List<Address> addressListBase = new ArrayList<>();

    Button btLupa, btClearAddress, btToFrag2;
    EditText etName, etPhone, etAddress;
    ProgressBar pBarSearch;

    public EstabelecimentoInfoF() {
        // Required empty public constructor
    }

    public static EstabelecimentoInfoF newInstance() {
        return new EstabelecimentoInfoF();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_estabelecimento_info, container, false);
        btToFrag2 = (Button) fragment.findViewById(R.id.bt_to_frag2);
        etName = (EditText) fragment.findViewById(R.id.et_shop_name);
        etPhone = (EditText) fragment.findViewById(R.id.et_shop_phone);
        etAddress = (EditText) fragment.findViewById(R.id.et_shop_address);
        btLupa = (Button) fragment.findViewById(R.id.bt_lupa);
        btClearAddress = (Button) fragment.findViewById(R.id.bt_clear_address);
        pBarSearch = (ProgressBar) fragment.findViewById(R.id.progress_bar_search);

        btLupa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etAddress.getText().toString().trim().length() == 0) {
                    etAddress.setError(getString(R.string.mandatory_field));
                } else {
                    findAddress();
                }
            }
        });

        etAddress.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))) {
                    if (etAddress.getText().toString().trim().length() == 0) {
                        etAddress.setError(getString(R.string.mandatory_field));
                    } else {
                        findAddress();
                    }
                }
                return false;
            }
        });

        btClearAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAddress.setText("");
                if (markerShop != null) {
                    markerShop.remove();
                    markerShop = null;
                }
                btClearAddress.setVisibility(View.GONE);
            }
        });

        btToFrag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            toFrag2();
            }
        });

        geocoder = new Geocoder(getActivity());

        MapFragment mapFragment = (MapFragment) this.getChildFragmentManager().findFragmentById(R.id.map_select_loc);
        mapFragment.getMapAsync(this);

        return fragment;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            return;
        }
        gMap.setMyLocationEnabled(true);
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (AddEstabelecimentoActivity.userLatLng != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AddEstabelecimentoActivity.userLatLng, 15));
        } else {
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Constant.latLngCity, 12));
        }

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerShop != null) {
                    markerShop.remove();
                    markerShop = null;
                }

                markerShop = gMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_location)));

                // Geocode LatLng to Address
                final LatLng ll = latLng;

                new AsyncTask<String, Void, String>() {
                    protected void onPreExecute() {
                        btClearAddress.setVisibility(View.GONE);
                        pBarSearch.setVisibility(View.VISIBLE);

                    }

                    @Override
                    protected String doInBackground(String... params) {
                        String sAddress = "";
                        List<Address> adList = new ArrayList<>();
                        try {
                            adList = geocoder.getFromLocation(ll.latitude, ll.longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!adList.isEmpty()) {
                            Address address = adList.get(0);
                            sAddress = address.getAddressLine(0);
                            for (int i = 1; i <= address.getMaxAddressLineIndex(); i++) {
                                sAddress = sAddress + ", " + address.getAddressLine(i);
                            }
                        }
                        return sAddress;
                    }

                    protected void onPostExecute(String sAddress) {

                        pBarSearch.setVisibility(View.GONE);


                        if (sAddress.equals("")) {
                            etAddress.setText(getString(R.string.marcador_inserido));
                        } else {
                            etAddress.setText(sAddress);
                        }

                        btClearAddress.setVisibility(View.VISIBLE);
                    }
                }.execute();

            }
        });
    }

    public void findAddress() {

        // Hide keyboard
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etAddress.getWindowToken(), 0);

        addressList.clear();
        addressListBase.clear();

        // Get the string from the EditText
        final String s_address = etAddress.getText().toString();

        new AsyncTask<String, String, String>() {

            @Override
            protected void onPreExecute() {

                // Limpar marcadores antigos de outras buscas, antes de criar um novo.
                if (markerShop != null) {
                    markerShop.remove();
                    markerShop = null;
                }

                btClearAddress.setVisibility(View.GONE);
                pBarSearch.setVisibility(View.VISIBLE);

                if (!isNetworkAvailable()) {
                    cancel(true);
                }
            }

            @Override
            protected String doInBackground(String... params) {

                //Checar primeiro se algo foi digitado.
                if (!s_address.trim().equals("")) {
                    try {
                        addressListBase = geocoder.getFromLocationName(s_address, 10, Constant.llLat, Constant.llLng, Constant.urLat, Constant.urLng);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    cancel(true);
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                pBarSearch.setVisibility(View.GONE);

                final ArrayList<Address> addressList = new ArrayList<>();

                int u = 0;

                for (int i = 0; i < addressListBase.size(); i++) {

                    Address ad = addressListBase.get(i);

                    // Place locations in São Paulo on top of list
                    if (i != 0 && ad.getLocality() != null) {
                        if (ad.getLocality().equalsIgnoreCase("São Paulo")) {
                            addressList.add(addressList.get(u));
                            addressList.set(u, ad);
                            u++;
                        }
                    } else if (i != 0 && ad.getSubAdminArea() != null) {
                        if (ad.getSubAdminArea().equalsIgnoreCase("São Paulo")) {
                            addressList.add(addressList.get(u));
                            addressList.set(u, ad);
                            u++;
                        }
                    } else {
                        addressList.add(ad);
                    }
                }

                String[] s_addressList = null;
                ArrayList<String> array_address = new ArrayList<>();

                // Checar se o endere�o n�o por acaso foi encontrado. Caso negativo, ent�o lan�ar o AlertDialog no "else".
                if (!addressList.isEmpty()) {

                    // Create String[] with addresses
                    for (int i = 0; i < addressList.size(); i++) {

                        // Check number of AddressLine before using the second
                        if (addressList.get(i).getMaxAddressLineIndex() > 0) {
                            array_address.add(addressList.get(i).getAddressLine(0) + ", "
                                    + addressList.get(i).getAddressLine(1));
                        } else {
                            array_address.add(addressList.get(i).getAddressLine(0));
                        }

                        s_addressList = new String[array_address.size()];
                        s_addressList = array_address.toArray(s_addressList);
                    }

                    if (addressList.size() == 1) {
                        Address address = addressList.get(0);
                        if (address.hasLatitude() && address.hasLongitude()) {
                            double lat = address.getLatitude();
                            double lng = address.getLongitude();
                            LatLng coordinate = new LatLng(lat, lng);

                            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
                            gMap.animateCamera(cu);
                            markerShop = gMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(lat, lng))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_location))
                                    .title(address.getAddressLine(0)));

                            // Set the text on etSearch to be the complete address
                            String finalStringAddress = address.getAddressLine(0);
                            for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
                                finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
                            }
                            etAddress.setText(finalStringAddress);
                            btClearAddress.setVisibility(View.VISIBLE);
                        }

                    } else {
                        AlertDialog.Builder alert_enderecos = new AlertDialog.Builder(getActivity());
                        alert_enderecos.setTitle(getString(R.string.which_address))
                                .setItems(s_addressList, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Address address = addressList.get(which);
                                        if (address.hasLatitude() && address.hasLongitude()) {
                                            double lat = address.getLatitude();
                                            double lng = address.getLongitude();
                                            LatLng coordinate = new LatLng(lat, lng);

                                            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(coordinate, 17);
                                            gMap.animateCamera(cu);
                                            markerShop = gMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(lat, lng))
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapic_location))
                                                    .title(address.getAddressLine(0)));

                                            // Set the text on etSearch to be the complete address
                                            String finalStringAddress = address.getAddressLine(0);
                                            for (int x = 1; x < address.getMaxAddressLineIndex(); x++) {
                                                finalStringAddress = finalStringAddress + ", " + address.getAddressLine(x);
                                            }
                                            etAddress.setText(finalStringAddress);
                                            btClearAddress.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                        alert_enderecos.show();
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle(getString(R.string.end_nao_encontrado_titulo))
                            .setMessage(getString(R.string.end_nao_encontrado_mensagem))
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    // Create the AlertDialog object and return it
                    alert.show();
                }
            }

            protected void onCancelled() {

                pBarSearch.setVisibility(View.GONE);

                AlertDialog.Builder network_alert = new AlertDialog.Builder(getActivity());
                network_alert.setTitle(getString(R.string.network_alert_title))
                        .setMessage(getString(R.string.network_alert_dialog))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })
                        .setNegativeButton(getString(R.string.network_settings), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        });
                network_alert.show();
            }
        }.execute();
    }

    public void toFrag2() {
        String strName = etName.getText().toString();
        String strPhone = etPhone.getText().toString();
        String strAddress = etAddress.getText().toString();

        if (strName.trim().length() == 0) {
            etName.setError(getString(R.string.mandatory_field));
        } else if (markerShop == null) {
            Toast.makeText(getActivity(), getString(R.string.loc_selecione_localizacao), Toast.LENGTH_SHORT).show();
            etAddress.requestFocus();
        } else if (strAddress.trim().length() == 0) {
            etAddress.setError(getString(R.string.mandatory_field));
            etAddress.requestFocus();
        } else {
            mListener.onEIFragmentInteraction(strName, strPhone, strAddress, markerShop.getPosition());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnEIFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return (activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting());
    }

    public interface OnEIFragmentInteractionListener {

        void onEIFragmentInteraction(String strName, String strPhone, String strAddress, LatLng latLng);
    }
}
