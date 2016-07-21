package jannini.android.ciclosp.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ToggleButton;

import jannini.android.ciclosp.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EstabelecimentoCategoryF.OnECFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EstabelecimentoCategoryF#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EstabelecimentoCategoryF extends Fragment {

    private OnECFragmentInteractionListener mListener;

    Button btAdd;
    ToggleButton tbStore, tbWorkshop, tbShower, tbCoffee;
    EditText etOther;

    public EstabelecimentoCategoryF() {
    }

    public static EstabelecimentoCategoryF newInstance() {
        EstabelecimentoCategoryF fragment = new EstabelecimentoCategoryF();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_estabelecimento_category, container, false);

        btAdd = (Button) fragment.findViewById(R.id.bt_add);
        tbStore = (ToggleButton) fragment.findViewById(R.id.tb_store);
        tbWorkshop = (ToggleButton) fragment.findViewById(R.id.tb_workshop);
        tbShower = (ToggleButton) fragment.findViewById(R.id.tb_shower);
        tbCoffee = (ToggleButton) fragment.findViewById(R.id.tb_coffee);
        etOther = (EditText) fragment.findViewById(R.id.et_other);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onECFragmentInteraction(tbStore.isChecked(), tbWorkshop.isChecked(), tbShower.isChecked(), tbCoffee.isChecked(), etOther.getText().toString());
            }
        });

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (OnECFragmentInteractionListener) context;
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
    public interface OnECFragmentInteractionListener {

        void onECFragmentInteraction(boolean store, boolean workshop, boolean shower, boolean coffee, String other);
    }
}
