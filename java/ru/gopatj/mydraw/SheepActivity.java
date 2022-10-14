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

import pl.droidsonroids.gif.GifTextView;

public class SheepActivity extends Activity implements View.OnClickListener {
    /** Find preview image of tree and create intent for dialog activity */
    private ImageView previewImage;
    private Intent intent;

    Button btnColorGreen, btnColorRed, btnColorBlue, btnColorWhite, btnSheepAnimation, btnClear;

    /** Finger + red flash animation and santa + magic animation */
    private static ImageView fingerImage;
    private static View redFlash;
    private static GifTextView magicGif, magicGif1;
    private static Animation animationFinger, animationRedFlash, animationMagic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheep);

        fingerImage = (ImageView) findViewById(R.id.fingerImage);
        previewImage = (ImageView) findViewById(R.id.previewImage);
        redFlash = (View) findViewById(R.id.redFlash);
        magicGif = (GifTextView) findViewById(R.id.magicGif);
        magicGif1 = (GifTextView) findViewById(R.id.magicGif1);
        fingerImage.setImageResource(R.drawable.finger);
        animationFinger = AnimationUtils.loadAnimation(this, R.anim.finger_animation);
        animationRedFlash = AnimationUtils.loadAnimation(this, R.anim.red_flash_alpha);
        animationMagic = AnimationUtils.loadAnimation(this, R.anim.magic_animation);

        // find buttons and set onClickListener
        btnColorGreen = (Button) findViewById(R.id.btnColorGreen);
        btnColorRed = (Button) findViewById(R.id.btnColorRed);
        btnColorBlue = (Button) findViewById(R.id.btnColorBlue);
        btnColorWhite = (Button) findViewById(R.id.btnColorWhite);
        btnSheepAnimation = (Button) findViewById(R.id.btnSheepAnimation);
        btnClear = (Button) findViewById(R.id.btnClear);
        btnColorGreen.setOnClickListener(this);
        btnColorRed.setOnClickListener(this);
        btnColorBlue.setOnClickListener(this);
        btnColorWhite.setOnClickListener(this);
        btnSheepAnimation.setOnClickListener(this);
        btnClear.setOnClickListener(this);
        previewImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.previewImage:
                intent = new Intent(this, PreviewSheepActivity.class);
                startActivity(intent); // dialog activity
                break;
            case R.id.btnColorGreen:
                DrawSheep.setPaintColor(Color.GREEN);
                break;
            case R.id.btnColorRed:
                DrawSheep.setPaintColor(Color.RED);
                break;
            case R.id.btnColorBlue:
                DrawSheep.setPaintColor(Color.BLUE);
                break;
            case R.id.btnColorWhite:
                DrawSheep.setPaintColor(Color.WHITE);
                break;
            case R.id.btnSheepAnimation:
                startMagicAnimation();
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

    public static void startMagicAnimation() {
        magicGif.startAnimation(animationMagic);
        magicGif1.startAnimation(animationMagic);
    }
}