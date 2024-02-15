package com.example.tasbihgo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.snackbar.Snackbar;

public class SetTargetPicker extends AppCompatDialogFragment {
    private static final String TAG = "SetTargetPicker";
    private EditText inputField;
    private int currentVal;
    private NumberPicker.OnValueChangeListener valueChangeListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tetapkan Sasaran antara 1 hingga 1000"); // Judul dialog
        builder.setMessage("Kiraan pusingan akan diatur semula!"); // Pesan dialog

        inputField = new EditText(getActivity());
        inputField.setHint("Masukkan Sasaran Anda"); // Hint untuk input field
        inputField.setInputType(InputType.TYPE_CLASS_NUMBER); // Jenis input field
        builder.setView(inputField); // Menambahkan input field ke dialog

        // Ketika tombol "BAIK" diklik
        builder.setPositiveButton("BAIK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newValue = inputField.getText().toString();
                if (!newValue.isEmpty()) {
                    int newValueInt = Integer.parseInt(newValue);
                    if (newValueInt >= 1 && newValueInt <= 1000) {
                        // Memanggil listener jika nilai valid
                        valueChangeListener.onValueChange(null, currentVal, newValueInt);
                    } else {
                        // Menghandle input di luar rentang yang valid
                    }
                } else {
                    // Menghandle input kosong
                }
            }
        });

        // Ketika tombol "BATAL" diklik
        builder.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Trigger Snackbar on button click
                Snackbar snackbar = Snackbar.make(((MainActivity) getActivity()).parentLayout, "Tiada Perubahan", Snackbar.LENGTH_SHORT);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbar.getView().getLayoutParams();
                params.gravity = Gravity.TOP;
                snackbar.getView().setLayoutParams(params);
                snackbar.show(); // Menampilkan Snackbar
            }
        });

        return builder.create(); // Membuat dan mengembalikan dialog yang telah dibuat
    }

    // Metode untuk menetapkan nilai saat ini ke input field
    public void setCurrentValue(int value) {
        currentVal = value;
        if (inputField != null) {
            inputField.setText(String.valueOf(value));
        }
    }

    // Metode untuk menetapkan listener untuk perubahan nilai
    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}
