package com.example.tasbihgo;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ResetDialog extends AppCompatDialogFragment {
    private Context context;

    public ResetDialog(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Set Semula my Tasbih") // Judul dialog
                //.setIcon(R.drawable.ic_fluent_erase_24_regular) // Ikonya bisa diatur di sini
                .setMessage("Ini tidak boleh dibuat asal. Kemajuan anda akan dipadamkan.") // Pesan konfirmasi
                .setPositiveButton("SET SEMULA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ketika tombol "SET SEMULA" diklik, panggil method resetCount dengan nilai true
                        ((MainActivity)getActivity()).resetCount(true);
                    }
                })
                .setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Ketika tombol "BATAL" diklik, panggil method resetCount dengan nilai false
                        ((MainActivity)getActivity()).resetCount(false);
                    }
                });

        return builder.create(); // Membuat dan mengembalikan dialog yang telah dibuat
    }
}

