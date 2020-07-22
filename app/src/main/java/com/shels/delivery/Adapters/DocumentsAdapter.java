package com.shels.delivery.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shels.delivery.Data.DataUtils.DataUtils;
import com.shels.delivery.Data.DeliveryAct;
import com.shels.delivery.R;

import java.util.List;

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.DocumentsViewHolder> {

    private List<DeliveryAct> documents;
    public OnDocumentClickListener onDocumentClickListener;

    public interface OnDocumentClickListener{
        void onDocumentClick(int position);
    }

    public DocumentsAdapter(List<DeliveryAct> documents) {
        this.documents = documents;
    }

    @NonNull
    @Override
    public DocumentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.document, parent, false);

        return new DocumentsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentsViewHolder holder, int position) {
        DeliveryAct deliveryAct = documents.get(position);

        String documentType = DataUtils.getDocumentNameById(holder.itemView.getContext(), deliveryAct.getDocType());
        int documentStatus = deliveryAct.getDocStatus();

        holder.documentType.setText(documentType);
        holder.documentClient.setText(deliveryAct.getDocClient());
        holder.documentTime.setText(deliveryAct.getDocTime());

        switch (documentStatus){
            case 0:
                holder.documentStatusImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.ic_document));
                break;
            case 1:
                holder.documentStatusImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.ic_complete));
                break;
            case 2:
                holder.documentStatusImage.setImageDrawable(holder.itemView.getResources().getDrawable(R.drawable.ic_file_erorr));
                break;
        }
    }

    public void setDocuments(List<DeliveryAct> documents) {
         this.documents = documents;

         notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    public class DocumentsViewHolder extends RecyclerView.ViewHolder{
        TextView documentType;
        TextView documentClient;
        TextView documentTime;
        ImageView documentStatusImage;

        public DocumentsViewHolder(@NonNull View itemView) {
            super(itemView);

            documentType = itemView.findViewById(R.id.document_row_type);
            documentClient = itemView.findViewById(R.id.document_row_client);
            documentTime = itemView.findViewById(R.id.document_row_time);
            documentStatusImage = itemView.findViewById(R.id.document_row_status_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onDocumentClickListener != null){
                        onDocumentClickListener.onDocumentClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    public void setOnDocumentClickListener(OnDocumentClickListener onDocumentClickListener) {
        this.onDocumentClickListener = onDocumentClickListener;
    }

    public List<DeliveryAct> getDocuments() {
        return documents;
    }
}
