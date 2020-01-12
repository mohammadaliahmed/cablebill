package com.appsinventiv.cablebilling.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appsinventiv.cablebilling.EditCustomer;
import com.appsinventiv.cablebilling.Models.UserModel;
import com.appsinventiv.cablebilling.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    Context context;
    ArrayList<UserModel> itemList;
    ArrayList<UserModel> arrayList;
    UserListCallbacks callbacks;

    public UserListAdapter(Context context, ArrayList<UserModel> itemList, UserListCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.arrayList = new ArrayList<>(itemList);
        this.callbacks = callbacks;
    }

    public void updateList(ArrayList<UserModel> list) {
        this.itemList = list;
        arrayList.clear();
        arrayList.addAll(list);
        notifyDataSetChanged();
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arrayList);
        } else {
            for (UserModel text : arrayList) {
                if (text.getName().toLowerCase().contains(charText.toLowerCase()) || text.getPhone().contains(charText) || text.getAddress().toLowerCase().contains(charText.toLowerCase())
                        ) {
                    itemList.add(text);
                }
            }


        }
        notifyDataSetChanged();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item_layout, parent, false);
        UserListAdapter.ViewHolder viewHolder = new UserListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserModel model = itemList.get(position);
        holder.name.setText((position + 1) + ") Name: " + model.getName() + "\n    Phone: " + model.getPhone() + "\n    Adr: " + model.getAddress());
        holder.bill.setText("Bill: Rs " + model.getBill() + "/-");
        holder.bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onGenerateBill(model);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditCustomer.class);
                i.putExtra("userid", model.getPhone());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button bill;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bill = itemView.findViewById(R.id.bill);
            name = itemView.findViewById(R.id.name);


        }
    }

    public interface UserListCallbacks {
        public void onGenerateBill(UserModel model);
    }
}
