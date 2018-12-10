package com.example.giangtm.baiby.views.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.giangtm.baiby.Constants;
import com.example.giangtm.baiby.R;
import com.example.giangtm.baiby.models.Message;

import java.util.ArrayList;

import static com.example.giangtm.baiby.Constants.INCOMMINGMESSAGE;
import static com.example.giangtm.baiby.Constants.OUTCOMMINGMESSAGE;

public class MessageAdapter extends RecyclerView.Adapter {

    ArrayList<Message> messages;

    public MessageAdapter(ArrayList<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewtype) {
        switch (viewtype) {
            case INCOMMINGMESSAGE:
                View incommingView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.incomming_message_layout, viewGroup, false);
                return new IncommingMessageViewHolder(incommingView);
            default:
                View outcommingView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.outcomming_message_layout, viewGroup, false);
                return new OutcommingMessageViewHolder(outcommingView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        switch (viewHolder.getItemViewType()) {
            case INCOMMINGMESSAGE:
                IncommingMessageViewHolder incommingMessageViewHolder =  (IncommingMessageViewHolder) viewHolder;
                incommingMessageViewHolder.tvMessage.setText(messages.get(i).getMessage());
                break;
            default:
                OutcommingMessageViewHolder outcommingMessageViewHolder = (OutcommingMessageViewHolder) viewHolder;
                outcommingMessageViewHolder.tvMessage.setText(messages.get(i).getMessage());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    class IncommingMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        public IncommingMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvIncommingMessage);
        }
    }

    class OutcommingMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        public OutcommingMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvOutcommingMessage);
        }
    }
}
