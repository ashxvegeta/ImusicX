package com.example.imusicx;
import android.Manifest;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ListView listView;
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        listView  = findViewById(R.id.listView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_MEDIA_AUDIO) // or READ_MEDIA_IMAGES / VIDEO
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {

                            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                            ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());
                            String [] items = new String[mySongs.size()];
                            for (int i=0;i<mySongs.size();i++){
//                                  items[i] = mySongs.get(i).getName().replace(".mp3","");

                                String name = mySongs.get(i).getName().replace(".mp3", "");
                                if (name.length() > 30) {
                                    name = name.substring(0, 27) + "...";  // ✂️ Keep first 27 characters + ellipsis
                                }
                                items[i] = name;

                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,items);
                            listView.setAdapter(adapter);
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        } else {
            Dexter.withContext(this)
                    .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new PermissionListener() {
                        @Override
                        public void onPermissionGranted(PermissionGrantedResponse response) {
                            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionDenied(PermissionDeniedResponse response) {
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    }).check();
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    //function for getting all the mp3 files
    public ArrayList<File> fetchSongs(File file){
        ArrayList arrayList = new ArrayList();
        File[] songs = file.listFiles();
        if(songs!=null){
           for(File myFile:songs){
               if(!myFile.isHidden() && myFile.isDirectory()){
                   arrayList.addAll(fetchSongs(myFile));
               }else{
                   if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                       arrayList.add(myFile);
                   }
               }
           }
        }

        return arrayList;
    }
;}