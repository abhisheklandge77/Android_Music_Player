package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    RecyclerView songsListView;
    TextView noSongsText;
    ArrayList<AudioModel> songsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songsListView = (RecyclerView) findViewById(R.id.songsListView);
        noSongsText = (TextView) findViewById(R.id.noSongsText);

        if(!checkPermission()){
            requestPermission();
            return;
        }

        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DURATION
        };


        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null, null);

            while(cursor.moveToNext()){
                AudioModel songData = new AudioModel(cursor.getString(1), cursor.getString(0), cursor.getString(2));

                if(new File(songData.getPath()).exists()){
                    songsList.add(songData);
                }
            }

            if(songsList.size() == 0){
                noSongsText.setVisibility(View.VISIBLE);
            }else{
                songsListView.setLayoutManager(new LinearLayoutManager(this));
                songsListView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
            }
    }

    boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(this, "Read Permission Required ! Please allow from settings", Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(songsListView != null){
            songsListView.setAdapter(new MusicListAdapter(songsList, getApplicationContext()));
        }
    }
}