package com.bizone.britannia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.bizone.britannia.entities.RoutePlanEntity;
import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.ShopEntity;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.SelectQueries;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by siddhesh on 7/26/16.
 */
public class FragmentOne extends Fragment implements View.OnClickListener{
    private static final String TAG=FragmentOne.class.getSimpleName();
    private Button village1, village2, village3, village4, village5, village6, village7, village8,
            village9, village10;
    private ImageView icon1, icon2, icon3, icon4, icon5, icon6, icon7, icon8, icon9, icon10;
    private RoutePlanEntity routePlanEntity;
//    private boolean isButtonClickable=false;
//    private int tabIndex, route_id;

    public FragmentOne(){}
    public static FragmentOne newInstance(RoutePlanEntity entity)
    {
        Logger.d(TAG,"inside newInstance: entity="+entity);
        FragmentOne myFragment = new FragmentOne();
        Bundle args = new Bundle();
        args.putSerializable("routePlanEntity", entity);
        myFragment.setArguments(args);
        Logger.d(TAG,"exiting newInstance");
        return myFragment;
    }

    private boolean setButtonClickable(){

        Logger.d(TAG,"inside setButtonClickable");
        boolean retval=false;
        Calendar routeCal= Calendar.getInstance();
        routeCal.setTime(Commons.milliToDateObj(routePlanEntity.date));
        Commons.normalizeCalendarTime(routeCal);
        int previousDays = 0;
        try {
            previousDays = Integer.parseInt(SelectQueries.getSetting(getActivity(), Settings.PAST_DAYS));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG,e);
        }
        int nextDays = 0;
        try {
            nextDays = Integer.parseInt(SelectQueries.getSetting(getActivity(), Settings.FUTURE_DAYS));
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e(TAG, e);
        }

        Calendar previousCal = Calendar.getInstance();
        Commons.normalizeCalendarTime(previousCal);
        previousCal.add(Calendar.DAY_OF_MONTH,-previousDays);


        Calendar nextCal = Calendar.getInstance();
        Commons.normalizeCalendarTime(nextCal);
        nextCal.add(Calendar.DAY_OF_MONTH,nextDays);


        if(routeCal.compareTo(previousCal) >= 0 && routeCal.compareTo(nextCal)<=0){
            retval= true;
        }

        Logger.d(TAG,"exitting setButtonClickable: retval="+retval);
        return retval;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Logger.d(TAG,"inside onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        routePlanEntity = (RoutePlanEntity) getArguments().getSerializable("routePlanEntity");
        village1 = (Button) rootView.findViewById(R.id.vil1);
        village2 = (Button) rootView.findViewById(R.id.vil2);
        village3 = (Button) rootView.findViewById(R.id.vil3);
        village4 = (Button) rootView.findViewById(R.id.vil4);
        village5 = (Button) rootView.findViewById(R.id.vil5);
        village6 = (Button) rootView.findViewById(R.id.vil6);
        village7 = (Button) rootView.findViewById(R.id.vil7);
        village8 = (Button) rootView.findViewById(R.id.vil8);
        village9 = (Button) rootView.findViewById(R.id.vil9);
        village10 = (Button) rootView.findViewById(R.id.vil10);

        icon1 = (ImageView) rootView.findViewById(R.id.img1);
        icon2 = (ImageView) rootView.findViewById(R.id.img2);
        icon3 = (ImageView) rootView.findViewById(R.id.img3);
        icon4 = (ImageView) rootView.findViewById(R.id.img4);
        icon5 = (ImageView) rootView.findViewById(R.id.img5);
        icon6 = (ImageView) rootView.findViewById(R.id.img6);
        icon7 = (ImageView) rootView.findViewById(R.id.img7);
        icon8 = (ImageView) rootView.findViewById(R.id.img8);
        icon9 = (ImageView) rootView.findViewById(R.id.img9);
        icon10 = (ImageView) rootView.findViewById(R.id.img10);

        Logger.d(TAG,"exitting OnCreateView");
        return rootView;
    }

    private void setImageIcon(ImageView icon, int villageid, String date, boolean isPast) {
        Logger.d(TAG,"inside setImageIcon : villageId="+villageid+"; date="+date+"; isPast="+isPast);
        ArrayList<ShopEntity> shopEntities = SelectQueries.getShopElementsByVid(getActivity(),villageid);

        if(shopEntities.size() == 0){
            icon.setImageDrawable(getResources().getDrawable(R.mipmap.exclam));
            return;
        }

        ArrayList<ShopEntity> filteredShopEntities = new ArrayList<>();


        if(isPast){
            filteredShopEntities = shopEntities;
        }else{

            for(ShopEntity entity:shopEntities){
                SaleEntity saleEntity = SelectQueries.getSaleElements(getActivity(),entity.sale_id);
                Logger.d(TAG,"saleEntity="+saleEntity);
                Logger.d(TAG, " setImageIcon : sale date= "+date+"; Sale Entity created= "+Commons.milliToDate(saleEntity.created + ""));
                if((date).equals(Commons.milliToDate(saleEntity.created + ""))) {
                    filteredShopEntities.add(entity);
                }
            }
        }

        Log.d(TAG," setImageIcon : filteredShopENtities="+filteredShopEntities);
    //    boolean summaryDone = SelectQueries.checkHaatSummaryDone(getContext(),villageid);

            if (filteredShopEntities.size() == 0) {
                icon.setImageDrawable(getResources().getDrawable(R.mipmap.exclam));
                return;
            }

            for (ShopEntity entity : filteredShopEntities) {
                SaleEntity saleEntity = SelectQueries.getSaleElements(getActivity(), entity.sale_id);
                if ((Constants.INPROGRESS).equals(saleEntity.status) || (Constants.OPEN).equals(saleEntity.status)) {
                    icon.setImageDrawable(getResources().getDrawable(R.mipmap.unverified));
                    return;
                }
            }
        int saleId = SelectQueries.getSaleIdByVilIdAndRId(getContext(),villageid,routePlanEntity.r_id);
        if(saleId == 0){
            icon.setImageDrawable(getResources().getDrawable(R.mipmap.exclam));
            return;
        }
        SaleEntity saleEntityHaat = SelectQueries.getSaleElements(getContext(),saleId);
           if((Constants.INPROGRESS).equals(saleEntityHaat.status) || (Constants.OPEN).equals(saleEntityHaat.status)){
               icon.setImageDrawable(getResources().getDrawable(R.mipmap.unverified));
           } else{
               icon.setImageDrawable(getResources().getDrawable(R.mipmap.verified));
           }

        Logger.d(TAG,"exitting setImageIcon");

    }

    @Override
    public void onResume() {
        super.onResume();

        Logger.d(TAG,"inside onResume");
        boolean isPast = false;
        Calendar today = Commons.normalizeCalendarTime(Calendar.getInstance());
        Calendar routeCal = Calendar.getInstance();
        routeCal.setTime(Commons.milliToDateObj(routePlanEntity.date));
        Commons.normalizeCalendarTime(routeCal);
        if(routeCal.compareTo(today)<0){
            isPast=true;
        }

        Logger.d(TAG, "Route Plan entity="+routePlanEntity);
        int count=0;
        if(routePlanEntity.villageid_1>0 || routePlanEntity.villageid_1==-2){
            count=1;
        }
        if(routePlanEntity.villageid_2>0 || routePlanEntity.villageid_2==-2){
            count=2;
        }
        if(routePlanEntity.villageid_3>0 || routePlanEntity.villageid_3==-2){
            count=3;
        }
        if(routePlanEntity.villageid_4>0 || routePlanEntity.villageid_4==-2){
            count=4;
        }
        if(routePlanEntity.villageid_5>0 || routePlanEntity.villageid_5==-2){
            count=5;
        }
        Logger.d(TAG,"count= "+count);
        String date = Commons.milliToDate(routePlanEntity.date);

        boolean isButtonClickable=setButtonClickable();


        for (int i = 1; i <= count; i++) {
            if(i==1){
                makeVillageVisible(village1, icon1, routePlanEntity.villagename_1, routePlanEntity.villageid_1, isButtonClickable, date, isPast);
            } else if(i==2){
                makeVillageVisible(village2, icon2, routePlanEntity.villagename_2, routePlanEntity.villageid_2, isButtonClickable, date, isPast);
            } else if(i==3){
                makeVillageVisible(village3, icon3, routePlanEntity.villagename_3, routePlanEntity.villageid_3, isButtonClickable, date, isPast);
            } else if(i==4){
                makeVillageVisible(village4, icon4, routePlanEntity.villagename_4, routePlanEntity.villageid_4, isButtonClickable, date, isPast);
            } else if(i==5) {
                makeVillageVisible(village5, icon5, routePlanEntity.villagename_5, routePlanEntity.villageid_5, isButtonClickable, date, isPast);
            }
        }
        Logger.d(TAG,"exitting onResume");
    }

    private void makeVillageVisible(Button village, ImageView icon, String villageName, int villageId, boolean isBtnClickable, String date, boolean isPast){
        Logger.d(TAG,"inside makeVillageVisible");
        village.setVisibility(View.VISIBLE);
        village.setText(villageName);
        icon.setVisibility(View.VISIBLE);
        if(villageId>0) {
            village.setOnClickListener(this);
        }
        if(!isBtnClickable) {
            village.setEnabled(false);
        }
        setImageIcon(icon, villageId, date, isPast);
        Logger.d(TAG,"exitting makeVillageVisible");
    }

    @Override
    public void onClick(View v) {
        Logger.d(TAG,"inside onClick");
        Intent intent = new Intent(getActivity(), SelectRetailerHaatActivity.class);
        switch (v.getId()){

            case R.id.vil1:
                intent.putExtra("vilName",routePlanEntity.villagename_1);
                intent.putExtra("vilId",routePlanEntity.villageid_1);
                break;
            case R.id.vil2:
                intent.putExtra("vilName",routePlanEntity.villagename_2);
                intent.putExtra("vilId",routePlanEntity.villageid_2);
                break;
            case R.id.vil3:
                intent.putExtra("vilName",routePlanEntity.villagename_3);
                intent.putExtra("vilId",routePlanEntity.villageid_3);
                break;
            case R.id.vil4:
                intent.putExtra("vilName",routePlanEntity.villagename_4);
                intent.putExtra("vilId",routePlanEntity.villageid_4);
                break;
            case R.id.vil5:
                intent.putExtra("vilName",routePlanEntity.villagename_5);
                intent.putExtra("vilId",routePlanEntity.villageid_5);
                break;
            case R.id.vil6:
            case R.id.vil7:
            case R.id.vil8:
            case R.id.vil9:
            case R.id.vil10:
        }
        intent.putExtra("route_id",routePlanEntity.r_id);
        startActivity(intent);
        Logger.d(TAG,"exitting onClick");
    }
}