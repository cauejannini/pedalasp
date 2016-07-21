package jannini.android.ciclosp.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.model.LatLng;

import jannini.android.ciclosp.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EstabelecimentoInfoF.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EstabelecimentoInfoF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstabelecimentoInfoF extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    Button btToFrag2;
    EditText etName, etLocation;

    public EstabelecimentoInfoF() {
        // Required empty public constructor
    }

    public static EstabelecimentoInfoF newInstance() {
        EstabelecimentoInfoF fragment = new EstabelecimentoInfoF();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_add_estabelecimento_fragment1, container, false);
        btToFrag2 = (Button) fragment.findViewById(R.id.bt_to_frag2);
        etName = (EditText) fragment.findViewById(R.id.et_shop_name);
        etPhone = (EditText) fragment.findViewById(R.id.et_shop_phone);

        btToFrag2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strName = etName.getText().toString();
                String strLocation = etLocation.getText().toString();

                if (strName.trim().length() == 0) {
                    etName.setError("Campo obrigatório");
                } else {
                    mListener.onFragmentInteraction(strName, strLocation, );
                }
            }
        });

        return fragment;

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnFragmentInteractionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String strName, String strLocation, LatLng latLngLocation);
    }
}
