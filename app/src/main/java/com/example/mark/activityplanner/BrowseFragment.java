package com.example.mark.activityplanner;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseFragment extends Fragment implements View.OnClickListener, SearchView.OnQueryTextListener {

    ListView listView;
    Toolbar mtoolbar;
    ArrayList<String> listItems = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    TextView emptyText;


    public BrowseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        mtoolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mtoolbar);
        setHasOptionsMenu(true);
        mtoolbar.setTitle("Username Search");

        listView = view.findViewById(R.id.user_list_id);
        emptyText = view.findViewById(R.id.tv_empty_id);

        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, listItems);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent viewProfile = new Intent(BrowseFragment.this.getActivity(), ViewProfile.class);
                viewProfile.putExtra("User", listView.getItemAtPosition(i).toString());
                startActivity(viewProfile);
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_items,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if(adapter == null){
            emptyText.setText("Enter Username Above to Search");
        }
        if(s.length() > 2) {
            find_users(s);
        }
        return false;
    }

    private void find_users(String s) {
        Log.d("myTag", s);
        SharedPreferences sharedPref = this.getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        final String mUsername = sharedPref.getString("username", null);
        ServerRequests server_requests = new ServerRequests(this.getActivity());
        server_requests.find_users(s, new Get_String_Callback() {
            @Override
            public void done(String returned_string) {
                //Log.d("myTag", returned_string);
                try{
                    JSONObject jObject = new JSONObject(returned_string);

                    adapter.clear();
                    for (int i = 0; i < jObject.length(); i++){
                        String username  = jObject.get("username"+i).toString();
                        //planAdapter.add(plan);
                        Log.d("myTag", username);
                        if(!username.equals(mUsername)) {
                            adapter.add(username);
                            emptyText.setText("");

                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
