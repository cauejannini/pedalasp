package jannini.android.ciclosp.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import jannini.android.ciclosp.Adapters.ServicesListAdapter;
import jannini.android.ciclosp.Activities.AddToMapActivity;
import jannini.android.ciclosp.Constant;
import jannini.android.ciclosp.R;

public class PlaceCategoryF extends Fragment {

    private OnECFragmentInteractionListener mListener;

    Button btAdd;
    EditText etOther;
    ListView listServices;

    public PlaceCategoryF() {
    }

    public static PlaceCategoryF newInstance() {
        PlaceCategoryF fragment = new PlaceCategoryF();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_place_category, container, false);

        listServices = (ListView) fragment.findViewById(R.id.list_services);
        final ArrayList<Integer> categoriesIdsArray = new ArrayList<>(Constant.mapPlaceCategories.keySet());
        Collections.sort(categoriesIdsArray);
        ArrayList<String> categoriesNames = new ArrayList<>();
        for (int i = 0; i < categoriesIdsArray.size(); i++) {
            categoriesNames.add(Constant.mapPlaceCategories.get(categoriesIdsArray.get(i)));
        }
        String[] categoriesArray = new String[categoriesNames.size()];
        categoriesArray = categoriesNames.toArray(categoriesArray);
        listServices.setAdapter(new ServicesListAdapter(getActivity(), categoriesArray));

        if (AddToMapActivity.functionSelected.equals("EDIT_PLACE")) {
            if (AddToMapActivity.categoryIdList != null) {
                for (int y = 0; y<categoriesIdsArray.size(); y++) {
                    if (AddToMapActivity.categoryIdList.contains(categoriesIdsArray.get(y))) {
                        listServices.setItemChecked(y, true);
                    }
                }
            }
            TextView tvFragmentTitle = (TextView) fragment.findViewById(R.id.tv_add_place_category_title);
            tvFragmentTitle.setText(getString(R.string.edit_place));
        }

        listServices.setOnItemClickListener(new OnListItemClickListener());

        btAdd = (Button) fragment.findViewById(R.id.bt_add);
        etOther = (EditText) fragment.findViewById(R.id.et_other);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringServiceList = "";

                for (int i = 0; i<listServices.getCount(); i++) {
                    if (listServices.isItemChecked(i)) {
                        if (stringServiceList.length() > 0) stringServiceList += ",";
                        stringServiceList += categoriesIdsArray.get(i);
                    }
                }

                EditText etOtherServices = (EditText) listServices.getChildAt(listServices.getChildCount()-1).findViewById(R.id.et_other_service);

                mListener.onECFragmentInteraction(stringServiceList, etOtherServices.getText().toString());
            }
        });

        return fragment;
    }

    class OnListItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            int n = listServices.getFirstVisiblePosition();

            if(listServices.isItemChecked(position)) {
                listServices.getChildAt(position-n).setBackgroundResource(R.drawable.tb_waterblue_c);
            } else {
                listServices.getChildAt(position-n).setBackgroundResource(R.drawable.tb_waterblue_nc);
            }

        }
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

        void onECFragmentInteraction(String stringCategoriesIds, String strOtherServices);
    }
}
