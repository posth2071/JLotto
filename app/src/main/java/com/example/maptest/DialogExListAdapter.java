package com.example.maptest;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DialogExListAdapter extends BaseExpandableListAdapter{
    private ArrayList<ArrayList<DBinfo>> dGroupList;
    private Context context;
    private DialogViewHolder viewHolder = null;
    private LayoutInflater inflater = null;

    public DialogExListAdapter(Context context, ArrayList<ArrayList<DBinfo>> dGroupList) {
        super();
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            viewHolder = new DialogViewHolder();
            view = inflater.inflate(R.layout.dialog_groupname, parent, false);

            viewHolder.dialog_GroupName = view.findViewById(R.id.dialog_GroupName);
            view.setTag(viewHolder);
        } else {
            viewHolder = (DialogViewHolder) view.getTag();
        }

        if (isExpanded) {
            viewHolder.dialog_GroupName.setBackgroundColor(Color.GREEN);
        } else {    // 닫혀있으면 변화
            viewHolder.dialog_GroupName.setBackgroundColor(Color.WHITE);
        }
        ArrayList<DBinfo> list = (ArrayList<DBinfo>) getGroup(groupPosition);
        viewHolder.dialog_GroupName.setText(String.valueOf(list.get(0)));
        return view;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return dGroupList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return dGroupList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        Resources res = context.getResources();

        if (view == null) {
            view = inflater.inflate(R.layout.dialog_listitem, null);
            viewHolder = new DialogViewHolder();
            viewHolder.dialog_Turn = view.findViewById(R.id.dialog_Turn);
            viewHolder.dialog_Hallpair = view.findViewById(R.id.dialog_HallPair);
            viewHolder.dialog_Delete = view.findViewById(R.id.dialog_delete);
            viewHolder.dialog_Delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context.getApplicationContext(), "클릭", Toast.LENGTH_SHORT).show();
                }
            });
            for (int i = 0; i < 7; i++) {
                int id = res.getIdentifier("dialog_Num" + (i + 1), "id", context.getPackageName());
                viewHolder.dialog_Numset[i] = view.findViewById(id);
            }

            view.setTag(viewHolder);
        } else {
            viewHolder = (DialogViewHolder) view.getTag();
        }

        DBinfo setting = (DBinfo) getChild(groupPosition, childPosition);
        viewHolder.dialog_Turn.setText(String.valueOf(setting.getTurn()));
        viewHolder.dialog_Hallpair.setText(setting.getHallfair());

        String[] numberset = setting.getNumberset().split(",");
        for (int i = 0; i < 7; i++) {
            int number = Integer.parseInt(numberset[i]);
            viewHolder.dialog_Numset[i].setImageResource(MainActivity.imgId[number - 1]);
        }
        return view;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return dGroupList.get(groupPosition).get(childPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return dGroupList.get(groupPosition).size();
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return false;
    }

    class DialogViewHolder {
        //Group
        public TextView dialog_GroupName;

        //타입2
        public TextView dialog_Turn, dialog_Hallpair;
        public Button dialog_Delete;
        public ImageView[] dialog_Numset = new ImageView[7];
    }

}