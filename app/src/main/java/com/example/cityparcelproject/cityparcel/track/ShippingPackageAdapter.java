package com.example.cityparcelproject.cityparcel.track;

import android.content.Context;
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

public class ShippingPackageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ShippingPackageNode> listData = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private Context context;
    public ShippingPackageAdapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shipping_package, parent, false);
        return new ShippingPackageAdapter.ViewHolderShippingPackage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((ShippingPackageAdapter.ViewHolderShippingPackage) holder).onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(ShippingPackageNode data) {
        listData.add(data);
    }

    private ShippingPackageAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(ShippingPackageAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolderShippingPackage extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewDestination;
        private TextView textViewPrice;
        private TextView textViewDeliveryman;
        ViewHolderShippingPackage(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textView_itemShipping_title);
            textViewDestination = itemView.findViewById(R.id.textView_itemShipping_destination);
            textViewPrice = itemView.findViewById(R.id.textView_itemShipping_price);
            textViewDeliveryman = itemView.findViewById(R.id.textView_itemShipping_deliveryman);

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

        public void onBind(ShippingPackageNode data) {
            String price = NumberFormat.getCurrencyInstance(Locale.KOREA).format(Integer.parseInt(data.getPrice())); //원화 표시
            final String index = Integer.toString(data.getIndex());
            textViewTitle.setText(data.getTitle());
            textViewDestination.setText("목적지: " + data.getDestination());
            textViewPrice.setText("운송비용: " + price);
            textViewDeliveryman.setText("운송인: " + data.getDeliveryman());
        }
    }
}
