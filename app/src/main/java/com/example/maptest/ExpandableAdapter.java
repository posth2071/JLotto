package com.example.maptest;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private ArrayList<String> groupList = null;
    private Expand_Child childList = null;
    private ViewHolder viewHolder = null;
    private LayoutInflater inflater = null;

    public ExpandableAdapter(Context context, ArrayList<String> groupList,
                             Expand_Child childList){
        super();
        inflater = LayoutInflater.from(context);
        this.groupList = groupList;
        this.childList = childList;
        this.context = context;
    }

    @Override    //그룹뷰 각각의 행반환
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Log.d("테스트", "getGroupView 실행 - "+groupPosition);
        View view = convertView;

        if(view == null){
            Log.d("테스트", "view == null - "+groupPosition);
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.expand_list, parent, false);

            viewHolder.tv_GroupName = view.findViewById(R.id.expand_GroupName);
            view.setTag(viewHolder);
        } else {
            Log.d("테스트", "view != null - "+groupPosition);
            viewHolder = (ViewHolder) view.getTag();
        }

        //뷰그룹 열려있으면 색변화
        if(isExpanded){
            viewHolder.tv_GroupName.setBackgroundColor(Color.GREEN);
        } else {    // 닫혀있으면 변화
            viewHolder.tv_GroupName.setBackgroundColor(Color.WHITE);
        }
        viewHolder.tv_GroupName.setText(getGroup(groupPosition));

        return view;
    }

    // 차일드뷰 각각의 행 반환
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d("테스트","getCildView 시작, gPosition - "+groupPosition +", cPosition - "+childPosition);
        View view = convertView;
        Resources res = context.getResources();

        switch (getGroupType(groupPosition)) {
            case 0:
                if (view == null) {
                    Log.d("테스트","view == null,  gPosition - "+groupPosition +", cPosition - "+childPosition);
                    view = inflater.inflate(R.layout.expand_typeone, null);
                    viewHolder = new ViewHolder();
                    viewHolder.expand_OneRank = view.findViewById(R.id.expand_One_Rank);
                    viewHolder.expand_OneTotalMoney = view.findViewById(R.id.expand_One_TotalMoney);
                    viewHolder.expand_OnePeople = view.findViewById(R.id.expand_One_People);
                    viewHolder.expand_OnePersonalMoney = view.findViewById(R.id.expand_One_PersonalMoney);
                    viewHolder.expand_OneNorm = view.findViewById(R.id.expand_One_Norm);

                    view.setTag(viewHolder);
                } else {
                    Log.d("테스트","view != null,  gPosition - "+groupPosition +", cPosition - "+childPosition);
                    viewHolder = (ViewHolder) view.getTag();
                }

                ArrayList<String> settingone = (ArrayList<String>) getChild(groupPosition,childPosition);
                viewHolder.expand_OneRank.setText(settingone.get(0));
                viewHolder.expand_OneTotalMoney.setText(settingone.get(1));
                viewHolder.expand_OnePeople.setText(settingone.get(2));
                viewHolder.expand_OnePersonalMoney.setText(settingone.get(3));
                viewHolder.expand_OneNorm.setText(settingone.get(4));

                break;
            case 1:
                if (view == null) {
                    Log.d("테스트","view == null,  gPosition - "+groupPosition +", cPosition - "+childPosition);
                    view = inflater.inflate(R.layout.expand_typetwo, null);
                    viewHolder = new ViewHolder();
                    viewHolder.expand_TwoTurn = view.findViewById(R.id.expand_Two_Turn);
                    viewHolder.expand_TwoHallpair = view.findViewById(R.id.expand_Two_HallPair);
                    viewHolder.expand_TwoResult = view.findViewById(R.id.expand_Two_Result);

                    //findViewByID 반복문으로 실행 - 이미지뷰 7개
                    for(int i=0; i<7; i++){
                        int id = res.getIdentifier("expand_Two_Num"+(i+1),"id", context.getPackageName());
                        viewHolder.expand_TwoNumset[i] = view.findViewById(id);
                    }
                    //viewholder 저장
                    view.setTag(viewHolder);
                } else {
                    //View가 이미존재한다면 - viewholder 갖고오기
                    viewHolder = (ViewHolder) view.getTag();
                }
                DBinfo settingtwo = (DBinfo) getChild(groupPosition,childPosition);
                viewHolder.expand_TwoTurn.setText(String.valueOf(settingtwo.getTurn()));
                viewHolder.expand_TwoHallpair.setText((settingtwo.getHallfair()));
                String[] numberset = settingtwo.getNumberset().split(",");
                for(int i=0; i<7; i++){
                    int number = Integer.parseInt(numberset[i]);
                    viewHolder.expand_TwoNumset[i].setImageResource(MainActivity.imgId[number-1]);
                }
                //viewHolder.bt_Child.setBackgroundColor(Integer.parseInt(getChild(groupPosition, childPosition)));
                viewHolder.expand_TwoResult.setText(settingtwo.getResult());
                break;
            case 2:
                if(view == null){
                    Log.d("테스트","view == null,  gPosition - "+groupPosition +", cPosition - "+childPosition);
                    view = inflater.inflate(R.layout.expand_typethree, null);
                    viewHolder = new ViewHolder();
                    viewHolder.expand_ThreeAttention = view.findViewById(R.id.expand_Three_Attention);
                    view.setTag(viewHolder);
                    } else {
                    viewHolder = (ViewHolder) view.getTag();
                }
                String attention = (String) getChild(groupPosition,childPosition);
                viewHolder.expand_ThreeAttention.setText(attention);
                break;
        }
        return view;
    }

    @Override    //그룹 포지션 반환
    public String getGroup(int groupPosition) {
        Log.d("테스트", "getGroup 들어옴, gPosi-"+groupPosition +", 반환 - "+groupList.get(groupPosition));
        return groupList.get(groupPosition);
    }

    @Override    //그룹 사이즈 반환
    public int getGroupCount() {
        Log.d("테스트", "getGroupCount 들어옴, 반환 groupList.size -"+groupList.size());
        return groupList.size();
    }

    @Override    //그룹 ID 반환
    public long getGroupId(int groupPosition) {
        Log.d("테스트", "getGroupId 들어옴 gPosi리턴 - "+0);
        return groupPosition;
    }



    // 자식뷰 반환
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.d("테스트", "getChild 들어옴");
        switch (groupPosition){
            case 0:
                ArrayList<ArrayList<String>> childOne = childList.getTypeOne();
                return childOne.get(childPosition);
            case 1:
                ArrayList<DBinfo> childTwo = childList.getTypeTwo();
                return childTwo.get(childPosition);
            case 2:
                ArrayList<String> childThree = childList.getTypethree();
                return childThree.get(childPosition);
            default:
                return null;
        }
    }
    //차일드뷰 사이즈 반환
    @Override
    public int getChildrenCount(int groupPosition) {
        Log.d("테스트", "getChildrenCount 들어옴 gPosi - " + groupPosition);
        switch (groupPosition) {
            case 0:
                Log.d("테스트", "childrenCount 반환 - "+childList.typeOne.size());
                return childList.typeOne.size();
            case 1:
                Log.d("테스트", "childrenCount 반환 - "+childList.typeTwo.size());
                return childList.typeTwo.size();
            case 2:
                Log.d("테스트", "childrenCount 반환 - "+childList.typethree.size());
                return childList.typethree.size();
            default:
                Log.d("테스트", "childrenCount 반환 - default");
                return 0;
        }
    }

    // 차일드뷰 ID반환
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Log.d("테스트", "getChildId 들어옴 gPosi -" +groupPosition + ", cPosi반환 - "+childPosition );
        return 0;
    }

    //차일드뷰 타입개수반환 -차일드뷰 타입이 몇개인지 이거 중요 (3개반환)
    @Override
    public int getChildTypeCount() {
        Log.d("테스트", "getChildTypeCount 들어옴, 반환 1");
        return getGroupTypeCount();
    }

    //차일드뷰 타입반환
    @Override
    public int getChildType(int groupPosition, int childPosition) {
        Log.d("테스트", "getChildType 들어옴, gPosi - "+groupPosition + ", cPosi - "+childPosition);
        switch (groupPosition) {
            case 0:
                Log.d("테스트", "getChildType 반환, getTypeOne().size() - "+childList.getTypeOne().size());
                return 0;
                //return childList.getTypeOne().size();
            case 1:
                Log.d("테스트", "getChildType 반환, getTypetwo().size() - "+childList.getTypeTwo().size());
                return 1;
                //return childList.getTypeTwo().size();
            case 2:
                Log.d("테스트", "getChildType 반환, getTypethree().size() - "+childList.getTypethree().size());
                return 2;
                //return childList.getTypethree().size();
            default:
                Log.d("테스트", "getChildType 반환, - 1");
                return 0;

        }
    }

    //그룹뷰 타입개수반환
    @Override
    public int getGroupTypeCount() {
        Log.d("테스트", "getGroupTypeCount 들어옴, 반환값 " + groupList.size());
        return groupList.size();
    }

    //해당 그룹 타입반환
    @Override
    public int getGroupType(int groupPosition) {
        Log.d("테스트", "getGroupType 들어옴 groupPosition - "+groupPosition);
        switch (groupPosition){
            case 0:
                Log.d("테스트", "getGroupType 반환 - 0");
                return 0;
            case 1:
                Log.d("테스트", "getGroupType 반환 - 1");
                return 1;
            case 2:
                Log.d("테스트", "getGroupType 반환 - 2");
                return 2;
            default:
                Log.d("테스트", "getGroupType 반환 - 0");
                return 0;
        }
    }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public boolean isChildSelectable(int i, int i1) { return false; }

    class ViewHolder {
        //GroupName
        public TextView tv_GroupName;

        //타입1
        public TextView expand_OneRank, expand_OneTotalMoney, expand_OnePeople, expand_OnePersonalMoney, expand_OneNorm;

        //타입2
        public TextView expand_TwoTurn, expand_TwoHallpair, expand_TwoResult;
        public ImageView[] expand_TwoNumset = new ImageView[7];

        //타입3
        public TextView expand_ThreeAttention;
    }
}
