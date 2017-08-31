package com.bizone.britannia;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

/**
 * Created by Sonam on 2/3/17.
 */
public class HistoryMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.day_start_history:
                startActivity(new Intent(this, LoginHistoryAtivity.class));
                break;

            case R.id.retailer_history:
                startActivity(new Intent(this, SaleListActivity.class));
                break;

            case R.id.haat_history:
                startActivity(new Intent(this, HaatSaleActivity.class));
                break;

            case R.id.purchase_history:
                startActivity(new Intent(this, PurchaseHistoryActivity.class));
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
           finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

}
