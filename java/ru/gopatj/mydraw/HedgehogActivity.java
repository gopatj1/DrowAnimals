package ru.gopatj.mydraw;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

public class HedgehogActivity extends Activity implements View.OnClickListener {

    /** Find preview image of tree and create intent for dialog activity */
    ImageView previewImage;
    Intent intent;

    Button btnColorGreen, btnColorRed, btnColorBlue, btnColorPurple, btnMoveHedgehog, btnClear;

    /** Finger and red flash animations */
    private static ImageView fingerImage;
    private static View redFlash;
    private static Animation animationFinger, animationRedFlash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hedgehog);

        fingerImage = (ImageView) findViewById(R.id.fingerImage);
        previewImage = (ImageView) findViewById(R.id.previewImage);
        redFlash = (View) findViewById(R.id.redFlash);
        fingerImage.setImageResource(R.drawable.finger);
        animationFinger = AnimationUtils.loadAnimation(this, R.anim.finger_animation);
        animationRedFlash = AnimationUtils.loadAnimation(this, R.anim.red_flash_alpha);

        // find buttons and set onClickListener
        btnColorGreen = (Button) findViewById(R.id.btnColorGreen);
        btnColorRed = (Button) findViewById(R.id.btnColorRed);
        btnColorBlue = (Button) findViewById(R.id.btnColorBlue);
        btnColorPurple = (Button) findViewById(R.id.btnColorPurple);
        btnMoveHedgehog = (Button) findViewById(R.id.btnAnimateHedgehog);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnColorGreen.setOnClickListener(this);
        btnColorRed.setOnClickListener(this);
        btnColorBlue.setOnClickListener(this);
        btnColorPurple.setOnClickListener(this);
        btnMoveHedgehog.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        previewImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previewImage:
                intent = new Intent(this, PreviewHedgehogActivity.class);
                startActivity(intent); // dialog activity
                break;
            case R.id.btnColorGreen:
                DrawHedgehog.setPaintColor(Color.GREEN);
                break;
            case R.id.btnColorRed:
                DrawHedgehog.setPaintColor(Color.RED);
                break;
            case R.id.btnColorBlue:
                DrawHedgehog.setPaintColor(Color.BLUE);
                break;
            case R.id.btnColorPurple:
                DrawHedgehog.setPaintColor(Color.parseColor("#ff33cc"));
                break;
            case R.id.btnAnimateHedgehog:
                break;
            case R.id.btnClear:
                break;
        }
    }

    public static void startFingerAnimation(String status) {
        if(status.equals("incorrectActionDown"))
            redFlash.startAnimation(animationRedFlash);
        fingerImage.startAnimation(animationFinger);
    }
}