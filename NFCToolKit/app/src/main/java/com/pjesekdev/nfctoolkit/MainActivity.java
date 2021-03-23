package com.pjesekdev.nfctoolkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton
            encodeFab,
            decodeFab,
            eraseFab;

    private TextView
            encodeFabTitle,
            decodeFabTitle,
            eraseFabTitle;

    private Animation
            fabOpenAnim,
            fabCloseAnim;

    private ImageView
            imageWelcomeText,
            imageLogo;

    private boolean isOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        imageWelcomeText = findViewById(R.id.imageWelcomeText);
        imageLogo = findViewById(R.id.imageLogo);

        FloatingActionButton mainFab = findViewById(R.id.main_add_fab);
        encodeFab = findViewById(R.id.encodeNFC_fab);
        decodeFab = findViewById(R.id.decodeNFC_fab);
        eraseFab = findViewById(R.id.eraseNFC_fab);

        encodeFabTitle = findViewById(R.id.encodeNFC);
        decodeFabTitle = findViewById(R.id.decodeNFC);
        eraseFabTitle = findViewById(R.id.eraseNFC);

        fabOpenAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_open);
        fabCloseAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close);

        isOpen = false;

        encodeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(EncodeActivity.class);
            }
        });

        eraseFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(EraseActivity.class);
            }
        });

        decodeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goTo(DecodeActivity.class);
            }
        });

        mainFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOpen){

                    setFabAnimation(fabCloseAnim);
                    setVisibility(View.VISIBLE, View.INVISIBLE);

                    isOpen = false;
                } else {

                    setFabAnimation(fabOpenAnim);
                    setVisibility(View.INVISIBLE, View.VISIBLE);

                    isOpen = true;
                }
            }
        });
    }

    private void setFabAnimation(Animation animation){
        encodeFab.setAnimation(animation);
        decodeFab.setAnimation(animation);
        eraseFab.setAnimation(animation);
    }

    private void setVisibility(int imagesVisibility, int fabVisibility){
        imageWelcomeText.setVisibility(imagesVisibility);
        imageLogo.setVisibility(imagesVisibility);

        encodeFabTitle.setVisibility(fabVisibility);
        decodeFabTitle.setVisibility(fabVisibility);
        eraseFabTitle.setVisibility(fabVisibility);
    }

    private void goTo(Class goToClass){
        Intent intent = new Intent(MainActivity.this, goToClass);
        startActivity(intent);
    }
}