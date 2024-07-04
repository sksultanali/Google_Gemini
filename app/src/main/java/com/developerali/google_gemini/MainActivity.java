package com.developerali.google_gemini;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.developerali.google_gemini.databinding.ActivityMainBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", "AIzaSyDGe7MPSl0n-MmeWqyEfxynqd1mZ3of-XY");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Executor executor = Executors.newSingleThreadExecutor(); // Create a single-threaded executor



        binding.btn.setOnClickListener(v->{
            String msg = binding.edTxt.getText().toString().trim(); // Trim leading/trailing whitespaces
            if (msg.isEmpty()) {
                binding.edTxt.setError("*");
            } else {
                Content content = new Content.Builder()
                        .addText(msg)
                        .build();

                ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText();
                        runOnUiThread(() -> { // Schedule UI update on main thread
                            binding.resultTxt.setText(resultText);
                        });
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, executor);
            }
        });






    }
}