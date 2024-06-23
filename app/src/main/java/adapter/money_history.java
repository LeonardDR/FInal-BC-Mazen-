package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yawa.R;

import java.util.ArrayList;

import model.money_history_model;

public class money_history extends RecyclerView.Adapter<money_history.ViewHolder> {

    private final ArrayList<money_history_model> emergencyList;
    private final Context context;

    public money_history(ArrayList<money_history_model> emergencyList, Context context) {
        this.emergencyList = emergencyList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_historydays, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        money_history_model model = emergencyList.get(position);
        holder.day.setText(model.getDay());
        holder.date.setText(model.getDate());
        holder.current_money.setText(model.getCurrent_money());
        holder.category.setText(model.getCategory());
        holder.mode_of_payment.setText(model.getMode_of_payment());
        holder.saved_money.setText(model.getSaved_money());
        holder.expenses_today.setText(model.getExpenses_today());
    }

    @Override
    public int getItemCount() {
        return emergencyList.size();
    }

    public money_history_model getItem(int position) {
        return emergencyList.get(position);
    }

    public void removeItem(int position) {
        emergencyList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, emergencyList.size());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView day, date, current_money, category, mode_of_payment, saved_money, expenses_today;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.day);
            date = itemView.findViewById(R.id.date);
            current_money = itemView.findViewById(R.id.current_money);
            category = itemView.findViewById(R.id.category);
            mode_of_payment = itemView.findViewById(R.id.mode_of_payment);
            saved_money = itemView.findViewById(R.id.saved_money);
            expenses_today = itemView.findViewById(R.id.expenses_today);
        }
    }
}
