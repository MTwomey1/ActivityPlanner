package com.example.mark.activityplanner;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    TextView tv_fullname;

    public ProfileFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tv_fullname = view.findViewById(R.id.tv_fullname_id);

        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        String firstname = sharedPref.getString("firstname","");
        String lastname = sharedPref.getString("lastname","");
        tv_fullname.setText(firstname + " " + lastname);

        // Inflate the layout for this fragment
        return view;
    }

}
