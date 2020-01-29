package com.example.maptest.Adapter.Expandable;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maptest.DataBase.DBOpenHelper;
import com.example.maptest.DataBase.DBinfo;
import com.example.maptest.Activity.Dialog.DialogClass;
import com.example.maptest.Activity.MainActivity;
import com.example.maptest.R;

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
    public ExpandableAdapter(Context context, ArrayList<String> groupList, Expand_Child childList){
        super();
        Log.d("확장리스트뷰", "ExpandableAdapter(context, groupList, childList) 메소드");
        inflater = LayoutInflater.from(context);
        this.groupList = groupList;
        this.childList = childList;
        this.context = context;
        this.adapterType = 1;
    }

    //DB설정 리스트에 사용
    public ExpandableAdapter(Context context, ArrayList<ArrayList<DBinfo>> dGroupList, DialogClass dialog){
        super();
        Log.d("확장리스트뷰", "ExpandableAdapter(context, dGroupList, dialog) 메소드");
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
        Log.d("확장리스트뷰", String.format("getGroup(gPos %d), return %s", groupPosition, group));
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
        Log.d("확장리스트뷰", String.format("getGroupCount(), return %d", groupCount));
        return  groupCount;
    }

    @Override    //그룹 ID 반환
    public long getGroupId(int groupPosition) {
        Log.d("확장리스트뷰", String.format("getGroupId(gPos %d), return %d",groupPosition,groupPosition));
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
        Log.d("확장리스트뷰", String.format("getGroupTypeCount(), return %d", groupTypeCount));
        return groupTypeCount;
    }

    //해당 그룹 타입반환
    @Override
    public int getGroupType(int groupPosition) {
        Log.d("확장리스트뷰", String.format("getGroupType(gPos %d), return %d", groupPosition, groupPosition));
        return groupPosition;
    }

    @Override    //그룹뷰 각각의 행반환
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Log.d("확장리스트뷰", String.format("getGroupView(gPos(%d), isExpand(%b), convertView, parent) 실행", groupPosition, isExpanded));

        View view = groupset(groupPosition, isExpanded, convertView, parent);

        return view;
    }


    // 자식뷰 반환
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        Log.d("확장리스트뷰", String.format("getChild(gPos(%d), cPos(%d)) 실행", groupPosition, childPosition));
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
    }

    //차일드뷰 사이즈 반환
    @Override
    public int getChildrenCount(int groupPosition) {
        Log.d("확장리스트뷰", String.format("getChildrenCount(gPos(%d)) 실행", groupPosition));
        switch (adapterType){
            case 1:
                switch (groupPosition){
                    case 0:
                        Log.d("확장리스트뷰", String.format("getChildrenCount(gPos(%d)), return %d", groupPosition,childList.typeOne.size()));
                        return childList.typeOne.size();
                    case 1:
                        Log.d("확장리스트뷰", String.format("getChildrenCount(gPos(%d)), return %d", groupPosition,childList.typeTwo.size()));
                        return childList.typeTwo.size();
                    case 2:
                        Log.d("확장리스트뷰", String.format("getChildrenCount(gPos(%d)), return %d", groupPosition,childList.typethree.size()));
                        return childList.typethree.size();
                    default:
                        Log.d("확장리스트뷰", String.format("getChildrenCount(gPos(%d)), return 0", groupPosition));
                        return 0;
                }
            case 2:
                Log.d("확장리스트뷰", String.format("getChildrenCount(gPos(%d)), return %d", groupPosition,dGroupList.get(groupPosition).size()));
                return dGroupList.get(groupPosition).size();
            default:
                return 0;
        }
    }

    // 차일드뷰 ID반환
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Log.d("확장리스트뷰", String.format("getChildId(gPos(%d), cPos(%d)), return %d", groupPosition, childPosition, 0));
        return 0;
    }

    //차일드뷰 타입개수반환 -차일드뷰 타입이 몇개인지 이거 중요 (3개반환)
    @Override
    public int getChildTypeCount() {
        Log.d("확장리스트뷰", String.format("getChildIdTypeCount(), return %d", getGroupTypeCount()));
        return getGroupTypeCount();
    }

    //차일드뷰 타입반환
    @Override
    public int getChildType(int groupPosition, int childPosition) {
        Log.d("확장리스트뷰", String.format("getChildType(gPos(%d), cPos(%d)), return %d", groupPosition,childPosition,groupPosition));
        return groupPosition;
    }

    // 차일드뷰 각각의 행 반환
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        Log.d("확장리스트뷰", String.format("getChildView(gPos(%d), cPos(%d), isLastChild(%b))", groupPosition,childPosition,isLastChild));
        View view = childset(groupPosition, childPosition, isLastChild, convertView, parent);

        return view;
    }

    @Override
    public boolean hasStableIds() { return false; }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        Log.d("확장리스트뷰", String.format("isChildSelectable(gPos(%d), cPos(%d)), return false", groupPosition,childPosition));
        return false;
    }

    private View groupset(int groupPosition, boolean isExpanded, View convertV, ViewGroup parent){
        Log.d("확장리스트뷰", String.format("groupset(gPos(%d), isExpanded(%b) convertV, parent)", groupPosition,isExpanded));
        View view = convertV;
        switch (adapterType){
            case 1:
                if(view == null){
                    Log.d("확장리스트뷰", "view == null, group - "+groupPosition);
                    viewHolder = new ViewHolder();
                    view = inflater.inflate(R.layout.expand_list, parent, false);

                    viewHolder.expand_GroupName = view.findViewById(R.id.expand_GroupName);
                    viewHolder.expand_GroupImage = view.findViewById(R.id.frag2_expand_group_iv);

                    view.setTag(viewHolder);
                } else {
                    Log.d("확장리스트뷰", "view != null, group - "+groupPosition);
                    viewHolder = (ViewHolder) view.getTag();
                }

                //뷰그룹 열려있으면 색변화#00BFFF
                if(isExpanded){
                    viewHolder.expand_GroupImage.setColorFilter(Color.parseColor("#ADD8E6"));
                } else {    // 닫혀있으면 변화
                    viewHolder.expand_GroupImage.setColorFilter(Color.parseColor("#00BFFF"));
                }
                viewHolder.expand_GroupName.setText(getGroup(groupPosition));
                break;
            case 2:
                if(view == null){
                    Log.d("확장리스트뷰", "case 2, if(view == null) -> true");
                    dialogViewHolder = new DialogViewHolder();
                    view = inflater.inflate(R.layout.dialog_groupname, parent, false);
                    dialogViewHolder.dialog_GroupName = view.findViewById(R.id.dialog_GroupName);
                    dialogViewHolder.dialog_GroupImage = view.findViewById(R.id.dialog_GroupImage);
                    view.setTag(dialogViewHolder);
                } else {
                    Log.d("확장리스트뷰", "case 2, if(view == null) -> false");
                    dialogViewHolder = (DialogViewHolder) view.getTag();
                }

                if(isExpanded){
                    dialogViewHolder.dialog_GroupImage.setColorFilter(Color.parseColor("#ADD8E6"));
                } else {    // 닫혀있으면 변화
                    dialogViewHolder.dialog_GroupImage.setColorFilter(Color.parseColor("#00BFFF"));
                }
                dialogViewHolder.dialog_GroupName.setText(getGroup(groupPosition));
                break;
        }
        return view;
    }

    private View childset(final int groupPosition, int childPosition,
                          boolean isLastChild, View convertView, ViewGroup parent){
        Log.d("확장리스트뷰",String.format("childset(gPos(%d), cPos(%d), isLastChild(%b) convertV, parent)", groupPosition, childPosition, isLastChild));
        View view = convertView;
        Resources res = context.getResources();
        switch (adapterType){
            case 1:
                Log.d("확장리스트뷰", "adapterType case 1");
                switch (getGroupType(groupPosition)) {
                    //Adapter타입 1 - child타입 1 인경우
                    case 0:
                        Log.d("확장리스트뷰", "getGroupType case 0");
                        if (view == null) {
                            Log.d("확장리스트뷰", "if(view == null) -> true");
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
                            Log.d("확장리스트뷰", "if(view == null) -> false");
                            viewHolder = (ViewHolder) view.getTag();
                        }

                        ArrayList<String> settingone = (ArrayList<String>) getChild(groupPosition,childPosition);
                        viewHolder.expand_OneRank.setText(settingone.get(0));
                        viewHolder.expand_OneTotalMoney.setText(settingone.get(1));
                        viewHolder.expand_OnePeople.setText(settingone.get(2)+"명");
                        viewHolder.expand_OnePersonalMoney.setText(settingone.get(3));
                        viewHolder.expand_OneNorm.setText(settingone.get(4));
                        break;
                    //Adapter타입 1 - Child타입 2 일때
                    case 1:
                        Log.d("확장리스트뷰", "getGroupType case 1");
                        if (view == null) {
                            Log.d("확장리스트뷰", "if(view == null) -> true");
                            view = inflater.inflate(R.layout.expand_typetwo, null);
                            viewHolder = new ViewHolder();
                            viewHolder.expand_TwoTurn = view.findViewById(R.id.expand_Two_Turn);
                            viewHolder.expand_TwoHallpair = view.findViewById(R.id.expand_Two_HallPair);
                            viewHolder.expand_TwoResult = view.findViewById(R.id.expand_Two_Result);
                            //findViewByID 반복문으로 실행 - 이미지뷰 7개
                            for(int i=0; i<6; i++){
                                int id = res.getIdentifier("expand_Two_Num"+(i+1),"id", context.getPackageName());
                                viewHolder.expand_TwoNumset[i] = view.findViewById(id);
                            }
                            //viewholder 저장
                            view.setTag(viewHolder);
                        } else {
                            Log.d("확장리스트뷰", "if(view == null) -> false");
                            //View가 이미존재한다면 - viewholder 갖고오기
                            viewHolder = (ViewHolder) view.getTag();
                        }


                        DBinfo settingtwo = (DBinfo) getChild(groupPosition,childPosition);
                        String numset = settingtwo.getNumset();
                        viewHolder.expand_TwoTurn.setText(String.valueOf(settingtwo.getTurn()));

                        String[] str = MainActivity.checkHallPair(numset).split(":");
                        viewHolder.expand_TwoHallpair.setText((String.format("홀수:짝수 (%s:%s)", str[0], str[1])));

                        String[] resultinfo = MainActivity.checkResult(numset, MainActivity.searchLottoInfo);         // 내기록과 당첨번호 당첨 확인하기
                        viewHolder.expand_TwoResult.setText(resultinfo[0]);             // [0] 당첨결과
                        viewHolder.expand_TwoResult.setTextColor(Color.parseColor(resultinfo[1]));  // [1] #000000 String타입 컬러값

                        //이미지뷰 6개 세팅
                        String[] numberset = numset.split(",");
                        for(int i=0; i<6; i++){
                            int number = Integer.parseInt(numberset[i]);
                            viewHolder.expand_TwoNumset[i].setImageResource(MainActivity.num_ID[number-1]);
                        }
                        break;
                        //Adapter타입 1 - Child타입 3 일때
                    case 2:
                        Log.d("확장리스트뷰", "getGroupType case 2");
                        if(view == null){
                            Log.d("확장리스트뷰", "if(view == null) -> true");
                            Log.d("확장리스트뷰","view == null,  gPosition - "+groupPosition +", cPosition - "+childPosition);
                            view = inflater.inflate(R.layout.expand_typethree, null);
                            viewHolder = new ViewHolder();
                            viewHolder.expand_ThreeAttention = view.findViewById(R.id.expand_Three_Attention);
                            view.setTag(viewHolder);
                        } else {
                            Log.d("확장리스트뷰", "if(view == null) -> false");
                            viewHolder = (ViewHolder) view.getTag();
                        }
                        String attention = (String) getChild(groupPosition,childPosition);
                        viewHolder.expand_ThreeAttention.setText(attention);
                        break;
                }
                break;
                //Adapter타입 2 일때
            case 2:
                Log.d("확장리스트뷰", "adapterType case 2");
                if(view == null){
                    Log.d("확장리스트뷰", "if(view == null) -> true");
                    view = inflater.inflate(R.layout.dialog_listitem, null);
                    dialogViewHolder = new DialogViewHolder();
                    dialogViewHolder.dialog_Turn = view.findViewById(R.id.dialog_Turn);
                    dialogViewHolder.dialog_Hallpair = view.findViewById(R.id.dialog_HallPair);
                    dialogViewHolder.dialog_Delete = view.findViewById(R.id.dialog_delete);

                    for (int i=0; i<6; i++){
                        int id = res.getIdentifier("dialog_Num"+(i+1), "id", context.getPackageName());
                        dialogViewHolder.dialog_Numset[i] = view.findViewById(id);
                    }
                    view.setTag(dialogViewHolder);
                } else {
                    Log.d("확장리스트뷰", "if(view == null) -> false");
                    dialogViewHolder = (DialogViewHolder) view.getTag();
                }

                final DBinfo setting = (DBinfo) getChild(groupPosition,childPosition);
                String numset = setting.getNumset();

                //만약 삭제되어서 View가 없어진 상태라면 null 리턴
                if(dialogViewHolder.dialog_Turn == null){
                    return null;
                } else {
                    dialogViewHolder.dialog_Turn.setText(String.valueOf(setting.getTurn()));
                    dialogViewHolder.dialog_Hallpair.setText(MainActivity.checkHallPair(numset));
                    dialogViewHolder.numberset = numset;

                    Log.d("확장리스트뷰", "numberset - " +numset);
                    String[] numberset = numset.split(",");

                    for (int i=0; i<6; i++){
                        int number = Integer.parseInt(numberset[i]);
                        dialogViewHolder.dialog_Numset[i].setImageResource(MainActivity.num_ID[number-1]);
                    }

                    //Delete 버튼 클릭리스너 설정
                    dialogViewHolder.dialog_Delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view1) {
                            Log.d("확장리스트뷰", "클릭 numberset - "+ setting.getNumset());
                            DialogClass dialogClass = new DialogClass(context, 5, setting);
                            dialogClass.show();
                        }
                    });
                }
                break;
        }
        return view;
    }

    // 뷰홀더 클래스
    class ViewHolder {
        //Group
        public TextView expand_GroupName;
        public ImageView expand_GroupImage;
        //타입1 (당첨정보)
        public TextView expand_OneRank, expand_OneTotalMoney, expand_OnePeople, expand_OnePersonalMoney, expand_OneNorm;
        //타입2 (기록)
        public TextView expand_TwoTurn, expand_TwoHallpair, expand_TwoResult;
        public ImageView[] expand_TwoNumset = new ImageView[6];
        //타입3 (당첨금 지급기한)
        public TextView expand_ThreeAttention;
    }

    // Dialog뷰홀더 클래스
    class DialogViewHolder {
        //Group
        public TextView dialog_GroupName;
        public ImageView dialog_GroupImage;

        //Child
        public TextView dialog_Turn, dialog_Hallpair;
        public Button dialog_Delete;
        public ImageView[] dialog_Numset = new ImageView[6];
        public String numberset;
    }
}
