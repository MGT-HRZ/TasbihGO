package com.example.tasbihgo;

import static com.example.tasbihgo.NotificationBuilder.CHANNEL_ID_1;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private static final String S_MAIN_COUNT = "mainCount"; // untuk SharedPreference, kunci untuk nilai utama zikir
    private static final String S_PROG_COUNT = "progressCount"; // untuk SharedPreference, kunci untuk nilai kemajuan zikir
    private static final String S_CUMMU_COUNT = "cummulativeCount"; // untuk SharedPreference, kunci untuk nilai zikir kumulatif
    private static final String S_TARGET_ZIKR = "targetZikr"; // untuk SharedPreference, kunci untuk nilai sasaran zikir tepi bar kemajuan

    private static final String TAG = "MainActivity"; // TAG untuk log

    private TextView countText; // TextView untuk menunjukkan hitungan zikir saat ini
    private TextView cummulativeText; // TextView untuk menunjukkan hitungan zikir kumulatif
    private TextView targetText; // TextView untuk menunjukkan sasaran hitungan zikir
    private Button buttonCount; // Button untuk menambah hitungan zikir
    private Button resetButton; // Button untuk mereset hitungan zikir
    private ProgressBar progressBar; // ProgressBar untuk menunjukkan kemajuan hitungan zikir
    private int countZikr = 0; // Variabel untuk menyimpan hitungan zikir saat ini
    private int targetZikr = 10; // Variabel untuk menyimpan sasaran hitungan zikir
    private Button btnZikrharian, btnZikrvids; // Button untuk opsi zikir harian dan zikir video
    private int progressCounter = 0; // Variabel untuk menyimpan kemajuan hitungan zikir
    private int cummulativeRound; // Variabel untuk menyimpan hitungan zikir kumulatif
    private long backPressedTimer; // Variabel untuk mengatur waktu tekan kembali
    public View parentLayout; // Layout utama activity
    private NotificationManagerCompat notificationManager; // Manajer notifikasi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Mengatur layout activity
        parentLayout = findViewById(R.id.parent_layout); // Menemukan layout utama berdasarkan id

        notificationManager = NotificationManagerCompat.from(this); // Inisialisasi manajer notifikasi

        countText = findViewById(R.id.text_zikr); // Menemukan TextView untuk menunjukkan hitungan zikir saat ini
        btnZikrharian = findViewById(R.id.zikr_harian); // Menemukan Button untuk zikir harian
        btnZikrvids = findViewById(R.id.zikr_vids); // Menemukan Button untuk zikir video
        buttonCount = findViewById(R.id.button_count); // Menemukan Button untuk menambah hitungan zikir
        resetButton = findViewById(R.id.button_reset); // Menemukan Button untuk mereset hitungan zikir
        progressBar = findViewById(R.id.progressBar); // Menemukan ProgressBar untuk menunjukkan kemajuan hitungan zikir
        targetText = findViewById(R.id.textView_progress_target); // Menemukan TextView untuk menunjukkan sasaran hitungan zikir
        cummulativeText = findViewById(R.id.textView_cummulative_count); // Menemukan TextView untuk menunjukkan hitungan zikir kumulatif
        targetText.setText(String.valueOf(targetZikr)); // Mengatur teks sasaran hitungan zikir
        progressBar.setMax(targetZikr); // Mengatur nilai maksimum ProgressBar

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE); // Mendapatkan SharedPreferences
        boolean isFirstStart = prefs.getBoolean("firstStart", true); // Mendapatkan nilai isFirstStart dari SharedPreferences


        if (isFirstStart)
            showWelcomeDialog(); // Menampilkan dialog selamat datang jika aplikasi baru pertama kali dibuka

        btnZikrharian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Menginisialisasi intent untuk membuka activity daftar zikir harian
                Intent intent = new Intent("com.example.tasbihgo.zikir_harian_list");
                startActivity(intent); // Memulai activity dengan intent yang sudah dibuat
            }
        });

        btnZikrvids.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Menginisialisasi intent untuk membuka activity daftar zikir video
                Intent intent = new Intent("com.example.tasbihgo.zikir_vids_list");
                startActivity(intent); // Memulai activity dengan intent yang sudah dibuat
            }
        });

        countText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Menyalin teks dari countText saat tombol ditekan lama
                copyText(countText.getText());
                vibrateFeedback(55); // Memberikan umpan balik getar dengan durasi 55ms
                showSnackBar(parentLayout, "Copied!"); // Menampilkan snackbar dengan pesan "Copied!"
                return true;
            }
        });

        buttonCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCount(); // Memanggil metode untuk menambah hitungan zikir saat tombol ditekan
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countZikr != 0)
                    openResetDialog(); // Membuka dialog reset jika hitungan zikir tidak sama dengan 0
            }
        });

        targetText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTargetDialog(); // Membuka dialog sasaran saat teks sasaran ditekan
            }
        });

        cummulativeText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // Menyalin teks dari cummulativeText saat tombol ditekan lama
                copyText(cummulativeText.getText());
                vibrateFeedback(55); // Memberikan umpan balik getar dengan durasi 55ms
                showSnackBar(parentLayout, "Copied!"); // Menampilkan snackbar dengan pesan "Copied!"
                return true;
            }
        });
    }


    private void showWelcomeDialog() {
        // Menampilkan dialog selamat datang
        WelcomeDialog welcomeDialog = new WelcomeDialog(this);
        welcomeDialog.show(getSupportFragmentManager(), "welcome dialog");

        // Mengupdate status isFirstStart ke false setelah dialog ditampilkan
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    private void copyText(CharSequence text) {
        // Menyalin teks ke clipboard
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("simple text", text);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);

        // Referensi: https://developer.android.com/guide/topics/text/copy-paste#java
    }

    //region menu toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu toolbar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.more_action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { //add action2 kau kat sini
        switch (item.getItemId()) {
            /* case R.id.action_change_theme:
                changeThemeMode();
                return true; */
            case R.id.action_share:
                shareValueToOtherApp(); // Memanggil metode untuk berbagi nilai ke aplikasi lain
                return true;
            case R.id.action_item_1: //about
                openAboutDialog(); // Membuka dialog tentang aplikasi
                return true;
            case R.id.action_item_3: //setTargetValue
                openTargetDialog(); // Membuka dialog untuk mengatur nilai target
                return true;
            /* case R.id.action_item_4: //showNotifs

                showOnNotification();
                return true;
            case R.id.action_subitem_1: //email
                openCustomTabs("https://github.com/fareezMaple/Tasbih-Digital-Android/releases");
                return true;
            case R.id.action_subitem_2: //website
                openCustomTabs("https://sites.google.com/view/tasbihdigitalfareez/home");
                return true;
            case R.id.action_subitem_3: //playstore app

                openWebPage("https://play.google.com/store/apps/details?id=com.maplerr.tasbihdigitalandroid");
                return true; */
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void shareValueToOtherApp() {
        // Menginisialisasi intent untuk berbagi pesan
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);

        String message; // Pesan yang akan dibagikan
        message = "As of " + getCurrentDateTime() + ", ";
        if (countZikr == 0) {
            message = message + "I didn't make any progress yet";
        }
        else {
            message = message + "I made till " + countZikr + ".";
        }
        /* if (nameText.length() > 0) {
            message = message + " -" + nameText.getText();
        } */

        // Menambahkan pesan ke intent berbagi
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");

        // Membuat intent untuk memilih aplikasi untuk berbagi
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent); // Memulai intent berbagi
    }

    public void openWebPage(String url) {
        // Membuka halaman web dengan URL tertentu menggunakan intent
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void openCustomTabs(String url) {
        // Membuka halaman web dengan URL tertentu menggunakan Chrome Custom Tabs
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        builder.setToolbarColor(getResources().getColor(R.color.colorPrimaryDark)); // Mengatur warna toolbar
        builder.setShowTitle(true); // Menampilkan judul halaman
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(url)); // Memulai Chrome Custom Tabs dengan URL yang ditentukan
    }


    //endregion

    public void addCount() { // Memperbarui hitungan dan tampilan teks saat tombol ditambah
        buttonCount.setText("+1"); // Mengubah teks tombol menjadi "+1"
        countZikr++; // Menambah hitungan zikir
        countText.setText(String.valueOf(countZikr)); // Mengupdate teks hitungan zikir

        if (countZikr == targetZikr) {
            // Jika target zikir tercapai, tampilkan Snackbar
            Snackbar snackbar = Snackbar.make(parentLayout, "Alhamdulillah Anda Capai Hajat Anda!", Snackbar.LENGTH_SHORT);

            // Mengatur posisi Snackbar ke atas
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
            params.gravity = Gravity.TOP;
            snackbar.getView().setLayoutParams(params);

            snackbar.show(); // Menampilkan Snackbar
        }

        progressCounter++; // Menambah hitungan progress
        updateProgressBar(); // Memperbarui ProgressBar

        resetButton.setVisibility(View.VISIBLE); // Menampilkan tombol reset

        if (progressCounter == targetZikr) {
            // Jika hitungan progress mencapai target, reset hitungan progress dan tambahkan hitungan kumulatif
            progressCounter = 0;
            cummulativeRound += 1;
            cummulativeText.setText("Pusingan : " + cummulativeRound); // Menampilkan hitungan zikir kumulatif
            vibrateFeedback(170); // Memberikan umpan balik getar
        }
    }


    public void resetCount(Boolean proceed) { // Dipasangkan dengan tombol reset di bawah sana
        if (proceed) {
            // Jika proses reset dilanjutkan, atur ulang semua hitungan dan tampilan terkait
            countZikr = 0;
            progressCounter = 0;
            countText.setText("0");
            buttonCount.setText("MULA");
            cummulativeRound = 0;
            cummulativeText.setText("");
            resetButton.setVisibility(View.INVISIBLE);

            // Atur ulang nilai progress bar ke 0
            if (VERSION.SDK_INT >= VERSION_CODES.N) {
                progressBar.setProgress(0, true);
            } else {
                progressBar.setProgress(0); // Tanpa animasi
            }

            // Buat dan tampilkan Snackbar untuk memberi tahu pengguna bahwa reset berhasil
            Snackbar snackbar = Snackbar.make(parentLayout, "Set Semula Berjaya", Snackbar.LENGTH_SHORT);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
            params.gravity = Gravity.TOP;
            snackbar.getView().setLayoutParams(params);
            snackbar.show();
        } else {
            // Buat dan tampilkan Snackbar untuk memberi tahu pengguna bahwa proses reset dibatalkan
            Snackbar snackbar = Snackbar.make(parentLayout, "Batal. Tiada Perubahan", Snackbar.LENGTH_SHORT);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
            params.gravity = Gravity.TOP;
            snackbar.getView().setLayoutParams(params);
            snackbar.show();
        }
    }


    public void updateProgressBar() {
        // Memperbarui nilai progress bar dengan atau tanpa animasi tergantung pada versi Android
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            progressBar.setProgress(progressCounter, true);
        } else {
            progressBar.setProgress(progressCounter); // Tanpa animasi
        }
        // Coba perbedaan level API nanti
    }

    public void openResetDialog() {
        // Membuka dialog reset
        ResetDialog resetDialog = new ResetDialog(this);
        resetDialog.show(getSupportFragmentManager(), "reset dialog");
    }

    public void openAboutDialog() {
        // Membuka dialog tentang aplikasi
        AboutDialog aboutDialog = new AboutDialog(this);
        aboutDialog.show(getSupportFragmentManager(), "about dialog");
    }

    @Override
    protected void onStop() {
        super.onStop();

        // Menyimpan data penting ke SharedPreferences saat activity dihentikan
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(S_MAIN_COUNT, countZikr);
        editor.putInt(S_PROG_COUNT, progressCounter);
        editor.putInt(S_CUMMU_COUNT, cummulativeRound);
        editor.putInt(S_TARGET_ZIKR, targetZikr);
        //editor.putString(S_TEXT_NAME, nameText.getText().toString());

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Mengambil data dari SharedPreferences saat activity dimulai
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

        countZikr = prefs.getInt(S_MAIN_COUNT, 0);
        progressCounter = prefs.getInt(S_PROG_COUNT, 0);
        cummulativeRound = prefs.getInt(S_CUMMU_COUNT, 0);
        targetZikr = prefs.getInt(S_TARGET_ZIKR, 10);
        //nameText.setText(prefs.getString(S_TEXT_NAME, ""));

        progressBar.setMax(targetZikr);
        updateProgressBar(); // Memperbarui ProgressBar

        targetText.setText(String.valueOf(targetZikr)); // Mengatur teks sasaran hitungan zikir

        cummulativeText.setText("Pusingan : " + cummulativeRound); // Menampilkan hitungan zikir kumulatif

        if (countZikr > 0) {
            buttonCount.setText("+1");
            resetButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        countText.setText(String.valueOf(countZikr)); // Memperbarui teks hitungan zikir saat activity dilanjutkan
    }

    public void openTargetDialog() {
        // Membuka dialog untuk mengatur sasaran hitungan zikir
        SetTargetPicker newFragment = new SetTargetPicker();
        newFragment.setValueChangeListener(this);
        newFragment.show(getSupportFragmentManager(), "target picker");
    }

    public void showSnackBar(View view, String message) {
        // Menampilkan Snackbar dengan pesan tertentu
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private String getCurrentDateTime() {
        // Mendapatkan tanggal dan waktu saat ini dalam format tertentu
        String pattern = "dd/MM/yy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(new Date());
    }

    @Override
    public void onBackPressed() { // Double tap to exit
        long millisToExit = 2000;

        if (backPressedTimer + millisToExit > System.currentTimeMillis()) {
            super.onBackPressed(); // Finish activity
        } else {
            Toast.makeText(this, "Tap again to exit", Toast.LENGTH_SHORT).show(); // Menampilkan pesan untuk keluar
        }

        backPressedTimer = System.currentTimeMillis();
    }

    //DEBUG ONLY
    public void ViewAndroidBuildNum(View view) {
        // Untuk debug saja - terlampir dengan tombol debug
        Log.d(TAG, "ViewAndroidBuildNum: is" + VERSION.SDK_INT);
    }


    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

        if (oldVal != newVal) {
            // Jika nilai baru berbeda dari nilai lama, tampilkan Snackbar dan atur ulang sasaran serta tampilan terkait
            Snackbar snackbar = Snackbar.make(parentLayout, "Sasaran berubah kepada : " + newVal, Snackbar.LENGTH_SHORT);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
            params.gravity = Gravity.TOP;
            snackbar.getView().setLayoutParams(params);
            snackbar.show();

            targetZikr = newVal; // Atur ulang nilai sasaran
            targetText.setText(String.valueOf(targetZikr)); // Memperbarui teks sasaran
            progressBar.setMax(targetZikr); // Menetapkan nilai maksimum ProgressBar sesuai dengan sasaran
            cummulativeRound = progressCounter = 0; // Atur ulang hitungan kumulatif dan progress
            cummulativeText.setText("0"); // Menetapkan teks hitungan kumulatif ke "0"
        } else {
            // Jika tidak ada perubahan, tampilkan Snackbar yang menunjukkan nilai sasaran saat ini
            Snackbar snackbar = Snackbar.make(parentLayout, "Tiada Perubahan. Sasaran : " + oldVal, Snackbar.LENGTH_SHORT);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
            params.gravity = Gravity.TOP;
            snackbar.getView().setLayoutParams(params);
            snackbar.show();
        }
    }

    private void vibrateFeedback(long millis) {
        // Memberikan umpan balik getar
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (v != null) { // Untuk menghindari warning
            if (VERSION.SDK_INT >= VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                v.vibrate(millis);
            }
        }
        // Referensi: https://stackoverflow.com/questions/13950338/how-to-make-an-android-device-vibrate
        //            https://stackoverflow.com/questions/46957405/method-invocation-vibrate-may-produce-java-lang-nullpointerexception-warning-a
    }


    public void showOnNotification() {
        finish(); // Menutup activity saat ini
        String title = "Current counter";

        // Membuat intent untuk membuka MainActivity saat notifikasi diklik
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);

        // Membuat notifikasi dengan menggunakan NotificationCompat.Builder
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_notifs_icon) // Mengatur ikon kecil notifikasi
                .setContentTitle(title) // Mengatur judul notifikasi
                .setContentText(String.valueOf(countZikr)) // Mengatur teks konten notifikasi
                .setColor(Color.rgb(230, 28, 98)) // Mengatur warna notifikasi
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Mengatur prioritas notifikasi
                .setCategory(NotificationCompat.CATEGORY_MESSAGE) // Mengatur kategori notifikasi
                .setContentIntent(contentIntent) // Mengatur intent saat notifikasi diklik
                .setAutoCancel(true) // Mengatur notifikasi untuk dihapus otomatis saat diklik
                .setOnlyAlertOnce(true) // Hanya memberi peringatan suara sekali
                .build();

        // Menampilkan notifikasi dengan menggunakan NotificationManagerCompat
        notificationManager.notify(1, notification);
    }


}