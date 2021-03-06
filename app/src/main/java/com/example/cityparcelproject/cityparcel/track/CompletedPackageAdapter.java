package com.example.cityparcelproject.cityparcel.track;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cityparcelproject.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CompletedPackageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static String URL2 = "http://thecityparcel.com/deleteParcel.php";

    private ArrayList<ScheduledPackageNode> listData = new ArrayList<>();

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    private Context context;
    public CompletedPackageAdapter(Context context) {
        this.context = context;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_package, parent, false);
        return new CompletedPackageAdapter.ViewHolderCompletedPackage(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((CompletedPackageAdapter.ViewHolderCompletedPackage) holder).onBind(listData.get(position));

        ((CompletedPackageAdapter.ViewHolderCompletedPackage) holder).deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteParcel(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    void addItem(ScheduledPackageNode data) {
        listData.add(data);
    }

    private CompletedPackageAdapter.OnItemClickListener mListener = null;

    public void setOnItemClickListener(CompletedPackageAdapter.OnItemClickListener listener) {
        this.mListener = listener;
    }

    public class ViewHolderCompletedPackage extends RecyclerView.ViewHolder {

        private TextView textViewTitle;
        private TextView textViewDestination;
        private TextView textViewPrice;
        private Button deleteBtn;
        ViewHolderCompletedPackage(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textView_itemCP_title);
            textViewDestination = itemView.findViewById(R.id.textView_itemCP_destination);
            textViewPrice = itemView.findViewById(R.id.textView_itemCP_price);
            deleteBtn = itemView.findViewById(R.id.button_itemCP_delete);
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

        public void onBind(ScheduledPackageNode data) {
            String price = NumberFormat.getCurrencyInstance(Locale.KOREA).format(Integer.parseInt(data.getPrice())); //원화 표시
            final String index = Integer.toString(data.getIndex());
            textViewTitle.setText(data.getTitle());
            textViewDestination.setText("목적지: " + data.getDestination());
            textViewPrice.setText("운송비용: " + price);

        }
    }

    private void deleteParcel(final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setMessage("정말로 삭제하시겠어요?");
        alertDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteParcelPHP(Integer.toString(listData.get(position).getIndex()));
                listData.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, listData.size());
            }
        });
        alertDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.show();
    }

    private void deleteParcelPHP(final String index) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL2,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")) {
                                //nothing to do
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("index", index);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}
