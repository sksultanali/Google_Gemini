package com.developerali.google_gemini;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.type.BlobPart;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.FileDataPart;
import com.google.ai.client.generativeai.type.ImagePart;
import com.google.ai.client.generativeai.type.Part;
import com.google.ai.client.generativeai.type.TextPart;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder>{

    private List<Content> chatMessages = new ArrayList<>();


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Content message = chatMessages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    public void addMessage(Content message) {
        chatMessages.add(message);
        notifyItemInserted(chatMessages.size() - 1);
    }

    public void setMessages(List<Content> messages) {
        chatMessages.clear();
        chatMessages.addAll(messages);
        notifyDataSetChanged();
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        private TextView roleTextView;
        private TextView messageTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            roleTextView = itemView.findViewById(R.id.role_text_view);
            messageTextView = itemView.findViewById(R.id.message_text_view);
        }

        public void bind(Content message) {
            roleTextView.setText(message.getRole());
            if (message.getParts() != null && !message.getParts().isEmpty()) {
                StringBuilder sb = new StringBuilder();

                for (Part part : message.getParts()) {
                    if (part instanceof TextPart) {
                        sb.append(((TextPart) part).getText()).append(" ");
                    } else if (part instanceof BlobPart) {
                        // Handle BlobPart if needed
                    } else if (part instanceof ImagePart) {
                        // Handle ImagePart if needed
                    } else if (part instanceof FileDataPart) {
                        // Handle FileDataPart if needed
                    }
                }

                messageTextView.setText(sb.toString().trim());
            } else {
                messageTextView.setText("");
            }
        }
    }
}
