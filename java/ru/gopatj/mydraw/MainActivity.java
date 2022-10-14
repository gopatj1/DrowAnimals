package ru.gopatj.mydraw;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imgToHedgehog = (ImageView) findViewById(R.id.imageToHedgehog);
        ImageView imgToTree = (ImageView) findViewById(R.id.imageToTree);
        ImageView imgToSheep = (ImageView) findViewById(R.id.imageToSheep);
        imgToHedgehog.setOnClickListener(this);
        imgToTree.setOnClickListener(this);
        imgToSheep.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.imageToHedgehog:
                intent = new Intent(this, HedgehogActivity.class);
                startActivity(intent);
                break;
            case R.id.imageToTree:
                intent = new Intent(this, TreeActivity.class);
                startActivity(intent);
                break;
            case R.id.imageToSheep:
                intent = new Intent(this, SheepActivity.class);
                startActivity(intent);
                break;
        }

    }
}
