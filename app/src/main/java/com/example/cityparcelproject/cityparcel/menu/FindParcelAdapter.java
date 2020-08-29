package com.example.cityparcelproject.cityparcel.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cityparcelproject.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FindParcelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<FindParcelNode> listData = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_find_parcel, parent, false);
        return new ViewHolderFindParcel(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolderFindParcel) holder).onBind(listData.get(position));
    }

    public ArrayList<FindParcelNode> getList() {
        return listData;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(FindParcelNode data) {
        listData.add(data);
    }

    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolderFindParcel extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewDestination;
        private TextView textViewPrice;

        ViewHolderFindParcel(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textView_itemFP_title);
            textViewDestination = itemView.findViewById(R.id.textView_itemFP_destination);
            textViewPrice = itemView.findViewById(R.id.textView_itemFP_price);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }

        public void onBind(FindParcelNode data) {
            String price = NumberFormat.getCurrencyInstance(Locale.KOREA).format(Integer.parseInt(data.getPrice())); //원화 표시
            textViewTitle.setText(data.getTitle());
            textViewDestination.setText("목적지" + data.getDestination());
            textViewPrice.setText("운송비용: " + price);
        }
    }
}