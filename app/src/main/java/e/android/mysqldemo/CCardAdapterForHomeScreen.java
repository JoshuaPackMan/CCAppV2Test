package e.android.mysqldemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CCardAdapterForHomeScreen extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    List<CardListData> data;

    //create constructor to initialize context and data sent from MainActivity
    public CCardAdapterForHomeScreen(Context context, List<CardListData> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    //Inflate the layout when ViewHolder created
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.ccard_row_for_home_screen, parent,false);
        MyHolder holder = new MyHolder(view);
        return holder;
    }

    //Bind data
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //Get current position of item in RecyclerView to bind data and assign values from list
        MyHolder myHolder = (MyHolder) holder;
        CardListData current = data.get(position);
        myHolder.cCardTV.setText(current.getcCard());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    class MyHolder extends RecyclerView.ViewHolder {
        TextView cCardTV;
        //CardView cardView;

        public MyHolder(View itemView) {
            super(itemView);
            cCardTV = itemView.findViewById(R.id.cardTVFromcCard_row);
        }
    }

}
