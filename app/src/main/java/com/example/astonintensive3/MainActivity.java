package com.example.astonintensive3;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.astonintensive3.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imputText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    String url = Objects.requireNonNull(binding.imputText.getText()).toString();
                    getImageFromPicasso(url);
                    hideKeyboard(textView);
                } catch (Exception e) {
                    Toast.makeText(
                            getApplicationContext(),
                            "Oops, something wrong. Please enter a link.",
                            Toast.LENGTH_SHORT
                    ).show();
                }
                return true;
            }
            return false;
        });

    }

    private void hideKeyboard(@NonNull TextView view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void getImageFromPicasso(String url) {
        Picasso.Builder builder = new Picasso.Builder(getApplicationContext());
        builder.listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Toast.makeText(
                        getApplicationContext(),
                        "Oops, something wrong. Please enter another link.",
                        Toast.LENGTH_SHORT
                ).show();
                binding.img.setImageResource(R.drawable.error);
            }
        });
        Picasso picasso = builder.build();
        picasso.load(url)
                .centerCrop()
                .resize(720, 1080)
                .placeholder(R.drawable.placeholder)
                .into(binding.img);
    }
}
