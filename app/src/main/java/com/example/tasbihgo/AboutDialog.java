package com.example.tasbihgo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class AboutDialog extends AppCompatDialogFragment {

    private Context context;

    public AboutDialog(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Apa itu TasbihGO!! ?") // Judul dialog
                //.setIcon(R.drawable.ic_fluent_info_24_regular) // Ikonya bisa diatur di sini
                .setMessage("Aplikasi ini mendigitalkan tasbih untuk memudahkan sesiapa sahaja melakukan perkara sunat seperti berzikir kepada Allah S.W.T\n\n" +
                        "Versi " + BuildConfig.VERSION_NAME) // Menampilkan pesan tentang aplikasi dan versi
                .setPositiveButton("TUTUP", null); // Tombol untuk menutup dialog

        return builder.create(); // Membuat dan mengembalikan dialog yang telah dibuat
    }
}

