package com.JLotto.JLotto.Adapter.Expandable;

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

import com.JLotto.JLotto.Activity.Dialog.DialogClass;
import com.JLotto.JLotto.DataBase.DBinfo;
import com.JLotto.JLotto.Activity.MainActivity;
import com.JLotto.JLotto.R;
import com.JLotto.JLotto.Util.Logger;

import java.util.ArrayList;

public class ExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private LayoutInflater inflater = null;

    private ArrayList<String> groupList = null;
    private Expand_Child childList = null;
    private ViewHolder viewHolder = null;

    //frag2에 사용
    public ExpandableAdapter(Context context, ArrayList<String> groupList, Expand_Child childList) {
        super();
        Logger.d("확장리스트뷰", "ExpandableAdapter(context, groupList, childList) 메소드");
        this.inflater = LayoutInflater.from(context);
        this.groupList = groupList;
        this.childList = childList;
        this.context = context;
    }

    @Override    //그룹 포지션 반환
    public String getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override    //그룹 사이즈 반환
    public int getGroupCount() {
        return groupList.size();
    }

    // 그룹 ID 반환
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // 그룹 View Type 개수 반환
    @Override
    public int getGroupTypeCount() {
        return groupList.size();
    }

    // 해당 그룹 타입반환
    @Override
    public int getGroupType(int groupPosition) {
        return groupPosition;
    }

    // 그룹뷰 각각의 행반환
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return groupset(groupPosition, isExpanded, convertView, parent);
    }

    // 자식뷰 반환
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case 0:
                return childList.getTypeOne().get(childPosition);
            case 1:
                return childList.getTypeTwo().get(childPosition);
            case 2:
                return childList.getTypethree().get(childPosition);
            default:
                return null;
        }
    }

    //차일드뷰 사이즈 반환
    @Override
    public int getChildrenCount(int groupPosition) {
        switch (groupPosition) {
            case 0:
                return childList.typeOne.size();
            case 1:
                return childList.typeTwo.size();
            case 2:
                return childList.typethree.size();
            default:
                return 0;
        }
    }

    // 차일드뷰 ID반환
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // Child View 타입개수반환 -차일드뷰 타입이 몇개인지 이거 중요 (3개반환)
    @Override
    public int getChildTypeCount() {
        return getGroupTypeCount();
    }

    // Child View 타입반환
    @Override
    public int getChildType(int groupPosition, int childPosition) {
        return groupPosition;
    }

    // Child View 각각의 행 반환
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        return childset(groupPosition, childPosition, isLastChild, convertView, parent);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private View groupset(int groupPosition, boolean isExpanded, View convertV, ViewGroup parent) {
        Logger.d("확장리스트뷰", String.format("groupset(gPos(%d), isExpanded(%b) convertV(%b), parent(%b))"
                , groupPosition, isExpanded, convertV == null, parent == null));
        View view = convertV;
        if (view == null) {
            Logger.d("확장리스트뷰", "view == null, group - " + groupPosition);
            viewHolder = new ViewHolder();
            view = inflater.inflate(R.layout.expand_list, parent, false);

            viewHolder.expand_GroupName = view.findViewById(R.id.expand_GroupName);
            viewHolder.expand_GroupImage = view.findViewById(R.id.frag2_expand_group_iv);

            view.setTag(viewHolder);
        } else {
            Logger.d("확장리스트뷰", "view != null, group - " + groupPosition);
            viewHolder = (ViewHolder) view.getTag();
        }

        //뷰그룹 열려있으면 색변화#00BFFF
        if (isExpanded) {
            viewHolder.expand_GroupImage.setColorFilter(Color.parseColor("#ADD8E6"));
        } else {    // 닫혀있으면 변화
            viewHolder.expand_GroupImage.setColorFilter(Color.parseColor("#00BFFF"));
        }
        viewHolder.expand_GroupName.setText(getGroup(groupPosition));
        return view;
    }

    private View childset(final int groupPosition, int childPosition,
                          boolean isLastChild, View convertView, ViewGroup parent) {
        View view = convertView;
        Resources res = context.getResources();
        Logger.d("확장리스트뷰", "adapterType case 1");
        switch (getGroupType(groupPosition)) {
            //Adapter타입 1 - child타입 1 인경우
            case 0:
                Logger.d("확장리스트뷰", "getGroupType case 0");
                if (view == null) {
                    Logger.d("확장리스트뷰", "if(view == null) -> true");
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
                    Logger.d("확장리스트뷰", "if(view == null) -> false");
                    viewHolder = (ViewHolder) view.getTag();
                }

                ArrayList<String> settingone = (ArrayList<String>) getChild(groupPosition, childPosition);
                viewHolder.expand_OneRank.setText(settingone.get(0));
                viewHolder.expand_OneTotalMoney.setText(settingone.get(1));
                viewHolder.expand_OnePeople.setText(settingone.get(2) + "명");
                viewHolder.expand_OnePersonalMoney.setText(settingone.get(3));
                viewHolder.expand_OneNorm.setText(settingone.get(4));
                break;
            //Adapter타입 1 - Child타입 2 일때
            case 1:
                Logger.d("확장리스트뷰", "getGroupType case 1");
                if (view == null) {
                    Logger.d("확장리스트뷰", "if(view == null) -> true");
                    view = inflater.inflate(R.layout.expand_typetwo, null);
                    viewHolder = new ViewHolder();
                    viewHolder.expand_TwoTurn = view.findViewById(R.id.expand_Two_Turn);
                    viewHolder.expand_TwoHallpair = view.findViewById(R.id.expand_Two_HallPair);
                    viewHolder.expand_TwoResult = view.findViewById(R.id.expand_Two_Result);
                    //findViewByID 반복문으로 실행 - 이미지뷰 7개
                    for (int i = 0; i < 6; i++) {
                        int id = res.getIdentifier("expand_Two_Num" + (i + 1), "id", context.getPackageName());
                        viewHolder.expand_TwoNumset[i] = view.findViewById(id);
                    }
                    //viewholder 저장
                    view.setTag(viewHolder);
                } else {
                    Logger.d("확장리스트뷰", "if(view == null) -> false");
                    //View가 이미존재한다면 - viewholder 갖고오기
                    viewHolder = (ViewHolder) view.getTag();
                }


                DBinfo settingtwo = (DBinfo) getChild(groupPosition, childPosition);
                String numset = settingtwo.getNumset();
                viewHolder.expand_TwoTurn.setText(String.valueOf(settingtwo.getTurn()));

                String[] str = MainActivity.checkHallPair(numset).split(":");
                viewHolder.expand_TwoHallpair.setText((String.format("홀수:짝수 (%s:%s)", str[0], str[1])));

                String[] resultinfo = MainActivity.checkResult(numset, MainActivity.searchLottoInfo);         // 내기록과 당첨번호 당첨 확인하기
                viewHolder.expand_TwoResult.setText(resultinfo[0]);             // [0] 당첨결과
                viewHolder.expand_TwoResult.setTextColor(Color.parseColor(resultinfo[1]));  // [1] #000000 String타입 컬러값

                //이미지뷰 6개 세팅
                String[] numberset = numset.split(",");
                for (int i = 0; i < 6; i++) {
                    int number = Integer.parseInt(numberset[i]);
                    viewHolder.expand_TwoNumset[i].setImageResource(MainActivity.num_ID[number - 1]);
                }
                break;
            //Adapter타입 1 - Child타입 3 일때
            case 2:
                Logger.d("확장리스트뷰", "getGroupType case 2");
                if (view == null) {
                    Logger.d("확장리스트뷰", "if(view == null) -> true");
                    Logger.d("확장리스트뷰", "view == null,  gPosition - " + groupPosition + ", cPosition - " + childPosition);
                    view = inflater.inflate(R.layout.expand_typethree, null);
                    viewHolder = new ViewHolder();
                    viewHolder.expand_ThreeAttention = view.findViewById(R.id.expand_Three_Attention);
                    view.setTag(viewHolder);
                } else {
                    Logger.d("확장리스트뷰", "if(view == null) -> false");
                    viewHolder = (ViewHolder) view.getTag();
                }
                String attention = (String) getChild(groupPosition, childPosition);
                viewHolder.expand_ThreeAttention.setText(attention);
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
}
