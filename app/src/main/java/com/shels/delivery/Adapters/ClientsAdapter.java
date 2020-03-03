package com.shels.delivery.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.R;

import java.util.List;

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.ClientsViewHolder>{

    private List<DeliveryAct> deliveryActs;
    private OnClientClickListener onClientClickListener;

    public ClientsAdapter(List<DeliveryAct> deliveryActs) {
        this.deliveryActs = deliveryActs;
    }

    public interface OnClientClickListener{
      void onClientClick(int position);
    }

    @NonNull
    @Override
    public ClientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.client, parent, false);

        return new ClientsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientsViewHolder holder, int position) {
        DeliveryAct deliveryAct = deliveryActs.get(position);

        holder.name.setText(deliveryAct.getClient());
    }

    @Override
    public int getItemCount() {
        return deliveryActs.size();
    }

    public class ClientsViewHolder extends RecyclerView.ViewHolder{
        TextView name;

        public ClientsViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.client_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClientClickListener != null){
                        onClientClickListener.onClientClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public void setDeliveryActs(List<DeliveryAct> deliveryActs) {
        this.deliveryActs = deliveryActs;

        notifyDataSetChanged();
    }

    public void setOnClientClickListener(OnClientClickListener onClientClickListener) {
        this.onClientClickListener = onClientClickListener;
    }
}
