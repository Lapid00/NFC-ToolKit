package com.example.nfctoolkit;

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

    private FloatingActionButton encodeFab;
    private FloatingActionButton decodeFav;
    private FloatingActionButton eraseFab;
    private TextView encodeNFC, decodeNFC, eraseNFC;

    private Animation fabOpenAnim, fabCloseAnim;
    private ImageView imageWelcomeText, imageLogo;

    private boolean isOpen;

    private TagTools tagTools;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        imageWelcomeText = findViewById(R.id.imageWelcomeText);
        imageLogo = findViewById(R.id.imageLogo);

        tagTools = new TagTools(this, getWindow().getDecorView().findViewById(android.R.id.content));
        tagTools.checkNFCState();

        FloatingActionButton mMainAddFab = findViewById(R.id.main_add_fab);
        encodeFab = findViewById(R.id.encodeNFC_fab);
        decodeFav = findViewById(R.id.decodeNFC_fab);
        eraseFab = findViewById(R.id.eraseNFC_fab);

        encodeNFC = findViewById(R.id.encodeNFC);
        decodeNFC = findViewById(R.id.decodeNFC);
        eraseNFC = findViewById(R.id.eraseNFC);

        fabOpenAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_open);
        fabCloseAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close);

        isOpen = false;

        mMainAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isOpen){

                    encodeFab.setAnimation(fabCloseAnim);
                    decodeFav.setAnimation(fabCloseAnim);
                    eraseFab.setAnimation(fabCloseAnim);

                    imageWelcomeText.setVisibility(View.VISIBLE);
                    imageLogo.setVisibility(View.VISIBLE);

                    encodeNFC.setVisibility(View.INVISIBLE);
                    decodeNFC.setVisibility(View.INVISIBLE);
                    eraseNFC.setVisibility(View.INVISIBLE);

                    isOpen = false;
                } else {

                    encodeFab.setAnimation(fabOpenAnim);
                    decodeFav.setAnimation(fabOpenAnim);
                    eraseFab.setAnimation(fabOpenAnim);

                    imageWelcomeText.setVisibility(View.INVISIBLE);
                    imageLogo.setVisibility(View.INVISIBLE);

                    encodeNFC.setVisibility(View.VISIBLE);
                    decodeNFC.setVisibility(View.VISIBLE);
                    eraseNFC.setVisibility(View.VISIBLE);

                    isOpen = true;
                }
            }
        });
    }

    public void openEncode(View v){
        Intent intent = new Intent(MainActivity.this, EncodeActivity.class);
        startActivity(intent);
    }

    public void openDecode(View v){
        Intent intent = new Intent(MainActivity.this, DecodeActivity.class);
        startActivity(intent);
    }

    public void openErase(View v){
        Intent intent = new Intent(MainActivity.this, EraseActivity.class);
        startActivity(intent);
    }
}