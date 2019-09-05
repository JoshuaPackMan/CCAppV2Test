package e.android.mysqldemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    List<BusinessListData> data;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    //create constructor to initialize context and data sent from MainActivity
    public BusinessAdapter(Context context, List<BusinessListData> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    //Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.business_row, parent,false);
        MyHolder holder = new MyHolder(view, mListener);
        return holder;
    }

    //Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        BusinessListData current = data.get(position);
        myHolder.businessTV.setText(current.getBusiness());
        if(current.getColor() == BusinessListData.BusinessColor.BLUE){
            myHolder.checkIV.setBackgroundResource(R.drawable.ic_check_box_black_24dp);
        } else{
            myHolder.checkIV.setBackgroundResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        TextView businessTV;
        ImageView checkIV;

        public MyHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            businessTV = itemView.findViewById(R.id.business);
            checkIV = itemView.findViewById(R.id.checkIV);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

}
