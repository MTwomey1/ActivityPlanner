package com.example.mark.activityplanner;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mark on 29/12/2017.
 */

public class PlanAdapter extends ArrayAdapter{
    List list = new ArrayList<>();

    public PlanAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Plan object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row;
        row = convertView;
        UserHolder userHolder;
        if (row == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.row_layout,parent,false);
            userHolder = new UserHolder();
            userHolder.tx_username = row.findViewById(R.id.tx_username);
            userHolder.tx_activity = row.findViewById(R.id.tx_activity);
            userHolder.tx_date = row.findViewById(R.id.tx_date);
            userHolder.tx_location = row.findViewById(R.id.tx_location);
            row.setTag(userHolder);
        }
        else{
            userHolder = (UserHolder)row.getTag();
        }

        //User user = (User) this.getItem(position);
        Plan plan = (Plan) this.getItem(position);
        userHolder.tx_username.setText(plan.getUsername());
        userHolder.tx_activity.setText(plan.getActivity());
        userHolder.tx_date.setText(plan.getDate());
        userHolder.tx_location.setText(plan.getLocation());
        return row;
    }

    static class UserHolder{
        TextView tx_username, tx_activity, tx_date, tx_location;
    }
}
