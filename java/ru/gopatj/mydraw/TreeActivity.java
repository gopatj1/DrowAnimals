package ru.gopatj.mydraw;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import pl.droidsonroids.gif.GifTextView;

public class TreeActivity extends Activity implements View.OnClickListener {
    /** Screen dimension */
    //RelativeLayout relativeLayout;
    DisplayMetrics displayMetrics;////////////////////////////

    /** Find preview image of tree and create intent for dialog activity */
    private ImageView previewImage;
    private Intent intent;

    //DrawView drawView;
    Button btnColorGreen, btnColorRed, btnColorBlue, btnColorPurple, btnMoveHedgehog, btnClear;

    MediaPlayer mediaPlayer;

    /** Finger + red flash animation and santa + magic animation */
    private static ImageView fingerImage;
    private static View redFlash;
    private static GifTextView santaGif;
    private static GifTextView magicGif, magicGif1;
    private static Animation animationFinger, animationRedFlash, animationSanta, animationMagic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        //relativeLayout = new RelativeLayout(this);
        displayMetrics = getResources().getDisplayMetrics();

        mediaPlayer = MediaPlayer.create(this, R.raw.fireworks_sound);//////////////////

        //drawView = new DrawView(this, null);

        // find finger image with red flash and animate it
        fingerImage = (ImageView) findViewById(R.id.fingerImage);

        /* //Move finger first way
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.mainLayoutHedgehog);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(1000, 500);
        // convert px value to dp
        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (15 * displayMetrics.density), (int) (225 * displayMetrics.density));
        //RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int)(displayMetrics.widthPixels * 0.375f);
        params.topMargin = (int)(displayMetrics.heightPixels * 0.75f);
        fingerImage.setLayoutParams(params);

        //move finder second way
        //fingerImage.setX(displayMetrics.widthPixels * 0.375f);
        //fingerImage.setY(displayMetrics.heightPixels * 0.75f);
        //fingerImage.setMaxWidth(fingerImage.getWidth() / 100);
        //fingerImage.setMaxHeight(fingerImage.getHeight() / 100);
        */

        previewImage = (ImageView) findViewById(R.id.previewImage);
        redFlash = (View) findViewById(R.id.redFlash);
        santaGif = (GifTextView) findViewById(R.id.santaGif);
        magicGif = (GifTextView) findViewById(R.id.magicGif);
        magicGif1 = (GifTextView) findViewById(R.id.magicGif1);
        fingerImage.setImageResource(R.drawable.finger);
        animationFinger = AnimationUtils.loadAnimation(this, R.anim.finger_animation);
        animationRedFlash = AnimationUtils.loadAnimation(this, R.anim.red_flash_alpha);
        animationSanta = AnimationUtils.loadAnimation(this, R.anim.santa_animation);
        animationMagic = AnimationUtils.loadAnimation(this, R.anim.magic_animation);

        // find buttons and set onClickListener
        btnColorGreen = (Button) findViewById(R.id.btnColorGreen);
        btnColorRed = (Button) findViewById(R.id.btnColorRed);
        btnColorBlue = (Button) findViewById(R.id.btnColorBlue);
        btnColorPurple = (Button) findViewById(R.id.btnColorPurple);
        btnMoveHedgehog = (Button) findViewById(R.id.btnAnimateTree);
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
                intent = new Intent(this, PreviewTreeActivity.class);
                startActivity(intent); // dialog activity
                break;
            case R.id.btnColorGreen:
                DrawTree.setPaintColor(Color.GREEN);
                break;
            case R.id.btnColorRed:
                DrawTree.setPaintColor(Color.RED);
                break;
            case R.id.btnColorBlue:
                DrawTree.setPaintColor(Color.BLUE);
                break;
            case R.id.btnColorPurple:
                DrawTree.setPaintColor(Color.parseColor("#ff33cc"));
                break;
            case R.id.btnAnimateTree:///////////////////////
                startSantaAnimation();

                /*
                //mediaPlayer.start();
                if (mediaPlayer != null) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
                mediaPlayer.reset();
                MediaPlayer.create(this, R.raw.fireworks_sound);
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
                break;/////////////////////////
            case R.id.btnClear:
                System.out.println("density " + displayMetrics.density);
                System.out.println("pos px" + displayMetrics.widthPixels / 8);
                System.out.println("pos dp " + displayMetrics.widthPixels / displayMetrics.density / 8);
                break;
        }
    }

    public static void startFingerAnimation(String status) {
        if(status.equals("incorrectActionDown"))
            redFlash.startAnimation(animationRedFlash);
        fingerImage.startAnimation(animationFinger);
    }

    public static void startSantaAnimation() {
        santaGif.startAnimation(animationSanta);
        magicGif.startAnimation(animationMagic);
        magicGif1.startAnimation(animationMagic);
    }
}
