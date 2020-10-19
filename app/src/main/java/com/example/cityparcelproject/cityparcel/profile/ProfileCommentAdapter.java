package com.example.cityparcelproject.cityparcel.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cityparcelproject.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class ProfileCommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<CommentModel> listData = new ArrayList<>();
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile_comment, parent, false);
        return new ProfileCommentAdapter.ViewHolderProfileComment(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ProfileCommentAdapter.ViewHolderProfileComment) holder).onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(CommentModel data) {
        listData.add(data);
    }

    public void deleteAllItem() {
        listData.clear();
    }

    public class ViewHolderProfileComment extends RecyclerView.ViewHolder {

        private TextView textViewWriter;
        private TextView textViewDate;
        private TextView textViewComment;

        ViewHolderProfileComment(View itemView) {
            super(itemView);
            textViewWriter = itemView.findViewById(R.id.textView_profile_comment_writer);
            textViewDate = itemView.findViewById(R.id.textView_profile_comment_date);
            textViewComment = itemView.findViewById(R.id.textView_profile_comment);
        }

        public void onBind(CommentModel data) {

            long unixTime = (long) data.timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);

            textViewWriter.setText(data.username);
            textViewDate.setText(time);
            textViewComment.setText(data.message);
        }
    }
}


