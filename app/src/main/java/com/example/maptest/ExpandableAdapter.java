package com.example.maptest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Toast;

import java.text.Format;
import java.util.ArrayList;

public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater inflater = null;

    private DialogClass dialog;
    // 1 = frag2, 2 = DB설정
    private int adapterType = 0;

    private ArrayList<String> groupList = null;
    private Expand_Child childList = null;
    private ViewHolder viewHolder = null;

    private ArrayList<ArrayList<DBinfo>> dGroupList = null;
    private DialogViewHolder dialogViewHolder = null;

    //frag2에 사용
    public ExpandableAdapter(Context context, ArrayList<String> groupList,
                             Expand_Child childList){
        super();
        inflater = LayoutInflater.from(context);
        this.groupList = groupList;
        this.childList = childList;
        this.context = context;
        this.adapterType = 1;
    }

    //DB설정 리스트에 사용
    public ExpandableAdapter(Context context, ArrayList<ArrayList<DBinfo>> dGroupList, DialogClass dialog){
        super();
        inflater = LayoutInflater.from(context);
        this.dGroupList = dGroupList;
        this.context = context;
        this.dialog = dialog;
        this.adapterType = 2;
    }

    @Override    //그룹 포지션 반환
    public String getGroup(int groupPosition) {
        String group = new String();

        if(adapterType==1){
            group = groupList.get(groupPosition);
        } else if (adapterType == 2){
            group = String.valueOf(dGroupList.get(groupPosition).get(0).getTurn());
        }
        Log.d("테스트", "getGroup 들어옴, gPosi-"+groupPosition +", 반환 - "+ group);
        return group;
    }

    @Override    //그룹 사이즈 반환
    public int getGroupCount() {
        int groupCount = 0;

        if (adapterType == 1) {
            groupCount = groupList.size();
        } else if (adapterType == 2) {
            groupCount = dGroupList.size();
        }
        Log.d("테스트", "getGroupCount 들어옴, 반환 -"+groupCount);
        return  groupCount;
    }

    @Override    //그룹 ID 반환
    public long getGroupId(int groupPosition) {
        Log.d("테스트", "getGroupId 들어옴, 반환 - "+groupPosition);
        return groupPosition;
    }

    //그룹뷰 타입개수반환
    @Override
    public int getGroupTypeCount() {
        int groupTypeCount = 0;

        if (adapterType == 1){
            groupTypeCount = groupList.size();
        } else if (adapterType == 2){
            groupTypeCount = dGroupList.size();
        }
        Log.d("테스트", "getGroupTypeCount 들어옴, 반환값 - " + groupTypeCount);
        return groupTypeCount;
    }

    //해당 그룹 타입반환
    @Override
    public int getGroupType(int groupPosition) {
        Log.d("테스트", "getGroupType 들어옴 groupPosition - "+groupPosition);
        return groupPosition;
    }

    @Override    //그룹뷰 각각의 행반환
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Log.d("테스트", "getGroupView 실행 - "+groupPosition);

        View view = groupset(groupPosition, isExpanded, convertView, parent);

        return view;
    }


    // 자식뷰 반환
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.d("테스트", "getChild 들어옴");
        switch (adapterType){
            case 1:
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
            case 2:
                return dGroupList.get(groupPosition).get(childPosition);
            default:
                return null;
        }
        /*
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

         */
    }

    //차일드뷰 사이즈 반환
    @Override
    public int getChildrenCount(int groupPosition) {
        Log.d("테스트", "getChildrenCount 들어옴 gPosi - " + groupPosition);
        switch (adapterType){
            case 1:
                switch (groupPosition){
                    case 0:
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
            case 2:
                return dGroupList.get(groupPosition).size();
            default:
                return 0;
        }
        /*
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

         */
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
        return groupPosition;
    }

    // 차일드뷰 각각의 행 반환
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d("테스트","getCildView 시작, gPosition - "+groupPosition +", cPosition - "+childPosition);

        View view = childset(groupPosition, childPosition, isLastChild, convertView, parent);

        return view;
    }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public boolean isChildSelectable(int i, int i1) { return false; }

    private View groupset(int groupPosition, boolean isExpanded, View convertV, ViewGroup parent){
        View view = convertV;
        switch (adapterType){
            case 1:
                if(view == null){
                    Log.d("테스트", "view == null - "+groupPosition);
                    viewHolder = new ViewHolder();
                    view = inflater.inflate(R.layout.expand_list, parent, false);

                    viewHolder.expand_GroupName = view.findViewById(R.id.expand_GroupName);

                    view.setTag(viewHolder);
                } else {
                    Log.d("테스트", "view != null - "+groupPosition);
                    viewHolder = (ViewHolder) view.getTag();
                }
                //뷰그룹 열려있으면 색변화
                if(isExpanded){
                    viewHolder.expand_GroupName.setBackgroundColor(Color.GREEN);
                } else {    // 닫혀있으면 변화
                    viewHolder.expand_GroupName.setBackgroundColor(Color.WHITE);
                }
                viewHolder.expand_GroupName.setText(getGroup(groupPosition));
                break;
            case 2:
                if(view == null){
                    dialogViewHolder = new DialogViewHolder();
                    view = inflater.inflate(R.layout.dialog_groupname, parent, false);
                    dialogViewHolder.dialog_GroupName = view.findViewById(R.id.dialog_GroupName);
                    view.setTag(dialogViewHolder);
                } else {
                    dialogViewHolder = (DialogViewHolder) view.getTag();
                }

                if(isExpanded){
                    dialogViewHolder.dialog_GroupName.setBackgroundColor(Color.GREEN);
                } else {    // 닫혀있으면 변화
                    dialogViewHolder.dialog_GroupName.setBackgroundColor(Color.WHITE);
                }
                dialogViewHolder.dialog_GroupName.setText(getGroup(groupPosition));
                break;
        }
        return view;
    }

    private View childset(final int groupPosition, int childPosition,
                          boolean isLastChild, View convertView, ViewGroup parent){
        Log.d("테스트","getCildView 시작, gPosition - "+groupPosition +", cPosition - "+childPosition);
        View view = convertView;
        Resources res = context.getResources();
        switch (adapterType){
            case 1:
                switch (getGroupType(groupPosition)) {
                    //Adapter타입 1 - child타입 1 인경우
                    case 0:
                        if (view == null) {
                            view = inflater.inflate(R.layout.expand_typeone, null);

                            viewHolder = new ViewHolder();
                            viewHolder.expand_OneRank = view.findViewById(R.id.expand_One_Rank);
                            viewHolder.expand_OneTotalMoney = view.findViewById(R.id.expand_One_TotalMoney);
                            viewHolder.expand_OnePeople = view.findViewById(R.id.expand_One_People);
                            viewHolder.expand_OnePersonalMoney = view.findViewById(R.id.expand_One_PersonalMoney);
                            viewHolder.expand_OneNorm = view.findViewById(R.id.expand_One_Norm);
                            //해당뷰에 ViewHolder 세팅
                            view.setTag(viewHolder);
                        } else { //해당뷰 이미 존재시, 저장되있던 ViewHolder 얻기
                            viewHolder = (ViewHolder) view.getTag();
                        }

                        ArrayList<String> settingone = (ArrayList<String>) getChild(groupPosition,childPosition);
                        viewHolder.expand_OneRank.setText(settingone.get(0));
                        viewHolder.expand_OneTotalMoney.setText(settingone.get(1));
                        viewHolder.expand_OnePeople.setText(settingone.get(2));
                        viewHolder.expand_OnePersonalMoney.setText(settingone.get(3));
                        viewHolder.expand_OneNorm.setText(settingone.get(4));
                        break;
                    //Adapter타입 1 - Child타입 2 일때
                    case 1:
                        if (view == null) {
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
                        viewHolder.expand_TwoResult.setText(settingtwo.getResult());
                        //이미지뷰 7개 세팅
                        String[] numberset = settingtwo.getNumberset().split(",");
                        for(int i=0; i<7; i++){
                            int number = Integer.parseInt(numberset[i]);
                            viewHolder.expand_TwoNumset[i].setImageResource(MainActivity.imgId[number-1]);
                        }
                        break;
                        //Adapter타입 1 - Child타입 3 일때
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
                break;
                //Adapter타입 2 일때
            case 2:
                if(view == null){
                    view = inflater.inflate(R.layout.dialog_listitem, null);
                    dialogViewHolder = new DialogViewHolder();
                    dialogViewHolder.dialog_Turn = view.findViewById(R.id.dialog_Turn);
                    dialogViewHolder.dialog_Hallpair = view.findViewById(R.id.dialog_HallPair);
                    dialogViewHolder.dialog_Delete = view.findViewById(R.id.dialog_delete);

                    for (int i=0; i<7; i++){
                        int id = res.getIdentifier("dialog_Num"+(i+1), "id", context.getPackageName());
                        dialogViewHolder.dialog_Numset[i] = view.findViewById(id);
                    }
                    view.setTag(dialogViewHolder);
                } else {
                    dialogViewHolder = (DialogViewHolder) view.getTag();
                }

                final DBinfo setting = (DBinfo) getChild(groupPosition,childPosition);
                dialogViewHolder.dialog_Turn.setText(String.valueOf(setting.getTurn()));
                dialogViewHolder.dialog_Hallpair.setText(setting.getHallfair());
                dialogViewHolder.numberset = setting.getNumberset();

                Log.d("다이얼로그", "numberset - " +setting.getNumberset());
                        String[] numberset = setting.getNumberset().split(",");
                for (int i=0; i<7; i++){
                    int number = Integer.parseInt(numberset[i]);
                    dialogViewHolder.dialog_Numset[i].setImageResource(MainActivity.imgId[number-1]);
                }

                //Delete 버튼 클릭리스너 설정
                dialogViewHolder.dialog_Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("다이얼로그", "클릭 numberset - "+ setting.getNumberset());
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete");
                        builder.setMessage(String.format("%d회차 - %s",
                                setting.getTurn(),
                                setting.getNumberset().replace(",", " ")));
                        //삭제버튼
                        builder.setPositiveButton("삭제",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(context,"삭제클릭",Toast.LENGTH_SHORT).show();
                                        DBOpenHelper dbOpenHelper = new DBOpenHelper(context);
                                        dbOpenHelper.deleteDB(setting.getNumberset());
                                    }
                                });
                        //취소버튼
                        builder.setNegativeButton("취소",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(context,"취소클릭",Toast.LENGTH_SHORT).show();
                                    }
                                });
                        builder.show();
                    }
                });
                break;
        }
        return view;
    }

    // 뷰홀더 클래스
    class ViewHolder {
        //Group
        public TextView expand_GroupName;
        //타입1 (당첨정보)
        public TextView expand_OneRank, expand_OneTotalMoney, expand_OnePeople, expand_OnePersonalMoney, expand_OneNorm;
        //타입2 (기록)
        public TextView expand_TwoTurn, expand_TwoHallpair, expand_TwoResult;
        public ImageView[] expand_TwoNumset = new ImageView[7];
        //타입3 (당첨금 지급기한)
        public TextView expand_ThreeAttention;
    }

    // Dialog뷰홀더 클래스
    class DialogViewHolder {
        //Group
        public TextView dialog_GroupName;

        //Child
        public TextView dialog_Turn, dialog_Hallpair;
        public Button dialog_Delete;
        public ImageView[] dialog_Numset = new ImageView[7];
        public String numberset;
    }
}
