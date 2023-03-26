package com.example.astonintensive3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    EditText inputText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.img);
        inputText = findViewById(R.id.imput_text);

        inputText.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                try {
                    String url = Objects.requireNonNull(inputText.getText()).toString();
                    getImageFromNet(url);
                    hideKeyboard(textView);
                } catch (Exception e) {
                    showError();
                }
                return true;
            }
            return false;
        });

    }

    private void showError() {
        showToast();
        showErrorPlaceholder();
    }

    private void showErrorOnUiThread() {
        Thread thread = new Thread() {
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        showError();
                    }
                });
            }
        };
        thread.start();
    }

    private void showToast() {
        Toast.makeText(
                getApplicationContext(),
                "Oops, something wrong. Please enter another link.",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void showErrorPlaceholder() {
        imageView.setImageResource(R.drawable.error);
    }

    private void hideKeyboard(@NonNull TextView view) {
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void getImageFromNet(String url) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(() -> {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setConnectTimeout(30000);
                connection.setReadTimeout(30000);
                connection.setInstanceFollowRedirects(true);
                InputStream inputStream = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                if (image.getByteCount() == 0) {
                    showErrorOnUiThread();
                }
                handler.post(() -> {
                    imageView.setImageBitmap(image);
                });
            } catch (Exception e) {
                showErrorOnUiThread();
            }
        });
    }
}
