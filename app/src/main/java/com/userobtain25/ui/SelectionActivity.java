package com.userobtain25.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import com.userobtain25.R;
import com.userobtain25.SplashActivity;

public class SelectionActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout l1Email;
    AppCompatTextView txtLater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);
        initView();
    }

    private void initView() {


        SharedPreferences settings = getSharedPreferences("UserObtain", 0);
//Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if(hasLoggedIn)
        {
            //Go directly to main activity.
            Intent i = new Intent(SelectionActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            // Show Login Activity
            l1Email = findViewById(R.id.l1Email);
            txtLater = findViewById(R.id.txtLater);
            l1Email.setOnClickListener(this);
            txtLater.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.l1Email:
                Intent i = new Intent(SelectionActivity.this, LoginActivity.class);
                startActivity(i);
                finish();;
                break;
            case R.id.txtLater:
                Intent i1 = new Intent(SelectionActivity.this, HomeActivity.class);
                i1.putExtra("type","0");
                startActivity(i1);
                finish();;
                break;
        }
    }
}
