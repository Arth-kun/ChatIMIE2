package com.chatimie.arthurcouge.chatimie;


import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import static android.media.CamcorderProfile.get;

/**
 * Created by Dylan on 28/03/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {
    private ArrayList<Message> listMessage = new ArrayList<>();
    private Cursor cursor;

    // 1 OnClick
    public interface ListItemClicklistener{
        void onListItemClick(String pseudo);
    }
    //2 Onclick
    final private ListItemClicklistener mOnClickListener;

    class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView listItemMessage;
        TextView listItemPseudo;
        TextView listItemDateTime;

        public MessageHolder(View itemView) {
            super(itemView);
            listItemMessage = (TextView) itemView.findViewById(R.id.textViewMessage);
            listItemPseudo = (TextView) itemView.findViewById(R.id.textViewPseudo);
            listItemDateTime = (TextView) itemView.findViewById(R.id.textViewHour);
            itemView.setOnClickListener(this);
        }

        void bind(String message, String pseudo, String DateTime) {
            listItemMessage.setText(String.valueOf(message));
            listItemPseudo.setText(String.valueOf(pseudo));
            listItemDateTime.setText(String.valueOf(DateTime));
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick("test");
        }
    }

    public MessageAdapter(ListItemClicklistener listener) {
        this.mOnClickListener = listener;
        listMessage = MessageActivity.getMessagesBdd().getAll();
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParent = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParent);
        MessageHolder viewHolder = new MessageHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MessageHolder holder, int position) {
        Message msg = listMessage.get(position);
        holder.bind(msg.getMessage(), msg.getPseudo(), msg.getHour());
    }

    @Override
    public int getItemCount() {
        return listMessage.size();
    }

    public void addToList(Message msg){
        listMessage.add(msg);
        MessageActivity.getMessagesBdd().open();
        MessageActivity.getMessagesBdd().insertMessage(msg);
        MessageActivity.getMessagesBdd().close();
        notifyDataSetChanged();
    }

    public void clearList(){
        //MessageActivity.getMessagesBdd().removeAllMessages();
        listMessage.clear();
        notifyDataSetChanged();
    }
}
