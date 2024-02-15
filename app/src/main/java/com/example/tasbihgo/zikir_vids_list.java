package com.example.tasbihgo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class zikir_vids_list extends AppCompatActivity {

    ImageView arrowBack;
    WebView vid_1, vid_2, vid_3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zikir_vids_list);

        // Inisialisasi tombol kembali
        arrowBack = findViewById(R.id.imageView2);

        // Mengatur tindakan ketika tombol kembali diklik
        arrowBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Membuat Intent untuk kembali ke MainActivity
                Intent intent= new Intent(zikir_vids_list.this, MainActivity.class);
                startActivity(intent) ;
            }
        });

        // Memuat video zikir pertama
        vid_1 = findViewById(R.id.vid_1);
        String vid1 = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/ynT6Zwgsz-M?si=kZeCQXPzdXj2iwJJ\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        vid_1.loadData(vid1, "text/html", "utf-8");
        vid_1.getSettings().setJavaScriptEnabled(true);
        vid_1.setWebChromeClient(new WebChromeClient());

        // Memuat video zikir kedua
        vid_2 = findViewById(R.id.vid_2);
        String vid2 = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/aHkoP9qnnYU?si=iXgxYYJwvrnruTxY\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        vid_2.loadData(vid2, "text/html", "utf-8");
        vid_2.getSettings().setJavaScriptEnabled(true);
        vid_2.setWebChromeClient(new WebChromeClient());

        // Memuat video zikir ketiga
        vid_3 = findViewById(R.id.vid_3);
        String vid3 = "<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/m9dsG-egacM?si=0G6dQyyXdd9Px8Ob\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        vid_3.loadData(vid3, "text/html", "utf-8");
        vid_3.getSettings().setJavaScriptEnabled(true);
        vid_3.setWebChromeClient(new WebChromeClient());
    }

    // Membuat menu "Tentang"
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_action_menu2, menu);
        return true;
    }

    // Mengatur tindakan ketika item menu dipilih
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_item_1: // Tentang
                openAboutDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Membuka dialog "Tentang"
    public void openAboutDialog() {
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.show(getSupportFragmentManager(), "about dialog");
    }
}
