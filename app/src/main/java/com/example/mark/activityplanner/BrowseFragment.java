package com.example.mark.activityplanner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;


/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseFragment extends Fragment implements View.OnClickListener {

    SearchView search;
    ListView listView;


    public BrowseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        search = view.findViewById(R.id.searchID);
        listView = view.findViewById(R.id.user_list_id);

        //search.setOnQueryTextFocusChangeListener((View.OnFocusChangeListener) this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.searchID:{

                break;
            }
        }
    }
}
