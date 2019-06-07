package e.android.mysqldemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //private Context context;
    private LayoutInflater inflater;
    List<BusinessListData> data;

    // create constructor to initialize context and data sent from MainActivity
    public MyAdapter(Context context, List<BusinessListData> data){
        //this.context=context;
        inflater = LayoutInflater.from(context);
        this.data = data;
        Log.v("mytag","data size: "+data.size());
    }

    // Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    // Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        BusinessListData current = data.get(position);
        myHolder.businessTV.setText(current.getBusiness());
        Log.v("mytag","businessTV: "+current.getBusiness());
        /*
        myHolder.cardTV.setText(current.getCard());
        myHolder.rewardTV.setText(current.getReward());
        */

    }

    // return total item from List
    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder{
        TextView businessTV;
        //TextView cardTV;
        //TextView rewardTV;

        public MyHolder(View itemView) {
            super(itemView);
            businessTV = itemView.findViewById(R.id.business);
            //cardTV = itemView.findViewById(R.id.card);
            //rewardTV = itemView.findViewById(R.id.reward);
        }
    }

}
