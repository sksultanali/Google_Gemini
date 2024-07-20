package com.developerali.google_gemini;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.developerali.google_gemini.databinding.ActivityChatBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    DatabaseHelper dbHelper;
    ChatAdapter chatAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dbHelper = new DatabaseHelper(this);
        chatAdapter = new ChatAdapter();

        binding.goBack.setOnClickListener(v->{
            onBackPressed();
        });
        binding.messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0){
                    binding.sendBtn.setVisibility(View.GONE);
                }else {
                    binding.sendBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash","AIzaSyDGe7MPSl0n-MmeWqyEfxynqd1mZ3of-XY");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        chatAdapter = new ChatAdapter();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        binding.recyclerView.setAdapter(chatAdapter);


        binding.sendBtn.setOnClickListener(v->{
            String messageTxt = binding.messageBox.getText().toString();
            if (messageTxt.isEmpty()){
                binding.messageBox.setError("*");
            } else {

                List<Content> history = getChatHistory();
                ChatFutures chat = model.startChat(history);

                Content.Builder userMessageBuilder = new Content.Builder();
                userMessageBuilder.setRole("user");
                userMessageBuilder.addText(messageTxt);
                Content userMessage = userMessageBuilder.build();

                Executor executor = Executors.newSingleThreadExecutor();
                ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessage);

                insertMessage("user", messageTxt);
                runOnUiThread(() -> chatAdapter.addMessage(userMessage));

                binding.messageBox.setText("");

                Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        String resultText = result.getText();

                        Content.Builder userMessageBuilder = new Content.Builder();
                        userMessageBuilder.setRole("model");
                        userMessageBuilder.addText(resultText);
                        Content modelMessage = userMessageBuilder.build();

                        insertMessage("model", resultText);
                        runOnUiThread(() -> chatAdapter.addMessage(modelMessage));
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                }, executor);
            }
        });






    }

    public List<Content> getChatHistory() {
        List<Content> history = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            @SuppressLint("Range") String role = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ROLE));
            @SuppressLint("Range") String text = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_TEXT));

            Content.Builder userContentBuilder = new Content.Builder();
            userContentBuilder.setRole(role);
            userContentBuilder.addText(text);
            Content userContent = userContentBuilder.build();

            history.add(userContent);
        }
        cursor.close();
        return history;
    }

    public void insertMessage(String role, String text) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_ROLE, role);
        values.put(DatabaseHelper.COLUMN_TEXT, text);

        db.insert(DatabaseHelper.TABLE_NAME, null, values);
    }
}