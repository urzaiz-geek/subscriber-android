package com.urzaizcoding.subscriber.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.urzaizcoding.subscriber.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static String EXTRA_CHAIN = "chain";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding ui = ActivityMainBinding.inflate(getLayoutInflater());
        ui.buttonEnrol.setOnClickListener(this::toEnrollPage);
        ui.buttonConsult.setOnClickListener(this::toStudentListPage);
        setContentView(ui.getRoot());
    }

    public void toEnrollPage(View btn){
        Intent registerActivityIntent = new Intent(this,RegisterStudentActivity.class);
        registerActivityIntent.putExtra(EXTRA_CHAIN,true);
        startActivity(registerActivityIntent);
    }

    public void toStudentListPage(View btn){
        Intent loadConsultActivityIntent = new Intent(this,StudentListViewActivity.class);
        startActivity(loadConsultActivityIntent);
    }
}