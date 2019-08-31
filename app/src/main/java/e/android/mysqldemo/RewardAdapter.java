package e.android.mysqldemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RewardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    List<RewardListData> data;

    //create constructor to initialize context and data sent from MainActivity
    public RewardAdapter(Context context, List<RewardListData> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    //Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.reward_row, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    //Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        RewardListData current = data.get(position);
        myHolder.cardTV.setText(current.getCard());
        myHolder.rewardTV.setText(current.getReward());
        myHolder.businessTV.setText(current.getBusiness());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        TextView cardTV;
        TextView rewardTV;
        TextView businessTV;

        public MyHolder(View itemView) {
            super(itemView);
            cardTV = itemView.findViewById(R.id.card);
            rewardTV = itemView.findViewById(R.id.reward);
            businessTV = itemView.findViewById(R.id.business);
        }
    }
}
