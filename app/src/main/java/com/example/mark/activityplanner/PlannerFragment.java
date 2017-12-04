package com.example.mark.activityplanner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlannerFragment extends Fragment {

    private ListView lv;

    public PlannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_planner, container, false);

        lv = view.findViewById(R.id.listView);
        List<String> mArrayList = new ArrayList<>();
        for (int i = 1; i < 11; i++) {
            mArrayList.add("Plan "+ i);
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                mArrayList);
        lv.setAdapter(arrayAdapter);
        // Inflate the layout for this fragment
        return view;
    }

}
