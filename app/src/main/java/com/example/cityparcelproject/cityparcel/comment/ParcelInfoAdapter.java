package com.example.cityparcelproject.cityparcel.comment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cityparcelproject.R;

import java.util.ArrayList;


public class ParcelInfoAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ParcelInfoNode> listData = new ArrayList<>();

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parcel_info, parent, false);
        return new ParcelInfoAdapter.ViewHolderParcelInfo(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ParcelInfoAdapter.ViewHolderParcelInfo) holder).onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void addItem(ParcelInfoNode data) {
        listData.add(data);
    }

    public class ViewHolderParcelInfo extends RecyclerView.ViewHolder {

        private TextView textViewWriter;
        private TextView textViewComment;

        ViewHolderParcelInfo(View itemView) {
            super(itemView);
            textViewWriter = itemView.findViewById(R.id.textView_itemPI_writer);
            textViewComment = itemView.findViewById(R.id.textView_itemPI_comment);
        }

        public void onBind(ParcelInfoNode data) {
            textViewWriter.setText(data.getWriter());
            textViewComment.setText(data.getComment());
        }
    }
}
