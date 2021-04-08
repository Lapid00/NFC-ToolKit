package com.pjesekdev.nfctoolkit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
        ConstraintLayout constraintLayout = findViewById(R.id.mainLayout);

        imageWelcomeText = findViewById(R.id.img_welcome);
        imageLogo = findViewById(R.id.img_logo);

        FloatingActionButton mainFab = findViewById(R.id.fab_main);
        encodeFab = findViewById(R.id.fab_encode);
        decodeFab = findViewById(R.id.fab_decode);
        eraseFab = findViewById(R.id.fab_erase);

        encodeFabTitle = findViewById(R.id.tv_encode);
        decodeFabTitle = findViewById(R.id.tv_decode);
        eraseFabTitle = findViewById(R.id.tv_erase);

        fabOpenAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_open);
        fabCloseAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close);

        isOpen = false;

        encodeFab.setOnClickListener(v -> goTo(EncodeActivity.class));

        eraseFab.setOnClickListener(v -> goTo(EraseActivity.class));

        decodeFab.setOnClickListener(v -> goTo(DecodeActivity.class));

        mainFab.setOnClickListener(v -> {

            if(isOpen){

                setFabAnimation(fabCloseAnim);
                setVisibility(View.VISIBLE, View.INVISIBLE);

                isOpen = false;
            } else {

                setFabAnimation(fabOpenAnim);
                setVisibility(View.INVISIBLE, View.VISIBLE);

                isOpen = true;
            }
        });

        TagTools tagTools = new TagTools(this, constraintLayout);
        tagTools.checkNFCState();
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