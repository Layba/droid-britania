package com.bizone.britannia;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bizone.britannia.entities.SaleEntity;
import com.bizone.britannia.entities.ShopEntity;
import com.bizone.britannia.logreports.Logger;
import com.bizone.britannia.queries.SelectQueries;

import java.util.ArrayList;

/**
 * Created by sagar on 20/3/17.
 */

public class RetailerListActivity extends AppCompatActivity {
    private static final String TAG=RetailerListActivity.class.getSimpleName();
    private AutoCompleteTextView actv;
    private String villageName;
    private int vilId, route_id;
    private ListView list;
    private EditText searchShop;
    private ArrayList<ShopEntity> shopEntities ;
    private ArrayList<String> strArrList;
    private CustomList adapter;

    private LinearLayout linearSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "inside onCreate");
        setContentView(R.layout.activity_shoplist);

        villageName = getIntent().getStringExtra("vilName");
        vilId = getIntent().getIntExtra("vilId", -1);
        adapter = new CustomList();
        linearSearch = (LinearLayout)findViewById(R.id.linear_search);
        actv = (AutoCompleteTextView) findViewById(R.id.search_shop);
        shopEntities= SelectQueries.getShopElementsByVid(this,vilId);
        strArrList = new ArrayList<String>();
        for(ShopEntity entity:shopEntities){
            strArrList.add(entity.shop_name);
        }

        if(shopEntities.size()>5){
            linearSearch.setVisibility(View.VISIBLE);
        }else{
            linearSearch.setVisibility(View.GONE);
        }

        searchShop = (EditText) findViewById(R.id.search_shop);

        searchShop.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                // RecceRetailerListActivity.this.adapter.getFilter().filter(cs);
                shopEntities = SelectQueries.getFilteredShops(RetailerListActivity.this,vilId,cs.toString());
                if(shopEntities != null && shopEntities.size()>0) {
                    RetailerListActivity.this.adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        route_id = getIntent().getIntExtra("route_id",0);
        ArrayAdapter<String> arAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,strArrList);
        actv.setAdapter(arAdapter);


        list=(ListView)findViewById(R.id.list_view);
        Logger.d(TAG,"setting adapter");
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                SaleEntity entity = SelectQueries.getSaleElements(RetailerListActivity.this, shopEntities.get(position).sale_id);
                if(!Constants.INPROGRESS.equals(entity.status)) {
                    Intent i = new Intent(RetailerListActivity.this, ShopActivity.class);
                    i.putExtra("saleId", shopEntities.get(position).sale_id);
                    i.putExtra("vilName", villageName);
                    i.putExtra("vilId", vilId);
                    i.putExtra("route_id",route_id);
                    if(Constants.CLOSED.equals(entity.status)) {
                        i.putExtra("status", "closed");
                        if(shopEntities.get(position).shop_id != 0){
                            i.putExtra("shopId",shopEntities.get(position).shop_id);
                        }
                    }
                    if(shopEntities.get(position).ser_shopId != 0){
                        i.putExtra("server_Id","true");
                    }
                    startActivity(i);
                } else {
                    Toast.makeText(RetailerListActivity.this,"Please upload data to proceed.",Toast.LENGTH_LONG).show();
                    Intent i = new Intent(RetailerListActivity.this,UploadSalesActivity.class);
                    startActivity(i);
                }

            }
        });
        Logger.d(TAG,"setActionBar");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Haat : "+villageName);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public void addShop(View v){
        Logger.d(TAG,"inside addShop");
        if(v.getId()==R.id.add){

            new android.app.AlertDialog.Builder(RetailerListActivity.this)
                    .setMessage("Are you sure you want to add new retailer?")
                    .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(RetailerListActivity.this,ShopActivity.class);
                            i.putExtra("vilName","");
                            i.putExtra("vilId",vilId);
                            i.putExtra("route_id",route_id);
                            startActivity(i);
                        }
                    })
                    .setNegativeButton("Select from list", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        }
    }
    public class CustomList extends BaseAdapter {

        @Override
        public int getCount() {
            return shopEntities.size();
        }

        @Override
        public Object getItem(int position) {
            return shopEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder{
            TextView shop_name;
            TextView visit;
            LinearLayout village_holder;
            TextView village;

        }
        @Override
        public View getView(int pos, View convertView, ViewGroup arg2) {
            Logger.d(TAG, "inside getView");


            ViewHolder holder = null;

            if(convertView == null) {
                convertView =getLayoutInflater().inflate(R.layout.shop_list_item, null);

                holder=new ViewHolder();
                holder.shop_name = (TextView) convertView.findViewById(R.id.shop_name);
                holder.visit = (TextView) convertView.findViewById(R.id.visit);
                holder.village = (TextView) convertView.findViewById(R.id.village);
                holder.village_holder = (LinearLayout) convertView.findViewById(R.id.village_group);
                holder.village_holder.setVisibility(View.VISIBLE);
                convertView.setTag(holder);

            }else {
                holder = (ViewHolder) convertView.getTag();
            }

            ShopEntity entity = shopEntities.get(pos);

            SaleEntity saleEntity = SelectQueries.getSaleElements(RetailerListActivity.this, entity.sale_id);

            Logger.d(TAG, "ShopEntity="+entity.toString());
            Logger.d(TAG,"SaleEntity="+saleEntity.toString());
            if (saleEntity.status.equals(Constants.OPEN)) {
                convertView.setBackgroundResource(R.color.status_open);
            } else if (saleEntity.status.equals(Constants.INPROGRESS)) {
                convertView.setBackgroundResource(R.color.status_inprogress);
            } else if (saleEntity.status.equals(Constants.CLOSED)) {
                convertView.setBackgroundResource(R.color.status_closed);
            }

            holder.shop_name.setText(entity.shop_name);
            holder.village.setText(entity.village);
            if(entity.updated == 0){
                holder.visit.setText(Commons.milliToDateWithYear(Long.parseLong(entity.created + "")));
            }else {
                holder.visit.setText(Commons.milliToDateWithYear(Long.parseLong(entity.updated + "")));
            }

            return convertView;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        try{

            if(shopEntities.size()>5){
                linearSearch.setVisibility(View.VISIBLE);
            }else{
                linearSearch.setVisibility(View.GONE);
            }
        }catch (Exception e){
            Logger.e(TAG,e);
            e.printStackTrace();
        }
    }
}

