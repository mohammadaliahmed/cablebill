package com.appsinventiv.cablebilling.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.appsinventiv.cablebilling.Activities.EditCustomer;
import com.appsinventiv.cablebilling.Models.UserModel;
import com.appsinventiv.cablebilling.R;
import com.appsinventiv.cablebilling.Utils.CommonUtils;
import com.appsinventiv.cablebilling.Utils.SharedPrefs;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {
    Context context;
    ArrayList<UserModel> itemList;
    ArrayList<UserModel> arrayList;
    UserListCallbacks callbacks;
    boolean canClick = false;
    ArrayList<String> sendToList = new ArrayList<>();

    public void setCanClick(boolean canClick) {
        this.canClick = canClick;
        notifyDataSetChanged();
    }

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

    public void setSendToList(ArrayList<String> sendToList) {
        this.sendToList = sendToList;
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

    public void filterDate(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        itemList.clear();
        if (charText.length() == 0) {
            itemList.addAll(arrayList);
        } else {
            for (UserModel text : arrayList) {
                if (text.getDueDate() != null) {
                    if (text.getDueDate().toLowerCase().contains(charText.toLowerCase())
                    ) {
                        itemList.add(text);
                    }
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

        boolean canBill = true;
        if (sendToList != null && sendToList.size() > 0)
            if (sendToList.contains(model.getPhone())) {
                canBill = false;
                holder.bill.setBackground(context.getResources().getDrawable(R.drawable.btn_bg_red));
            } else {
                holder.bill.setBackground(context.getResources().getDrawable(R.drawable.btn_bg));
                canBill = true;
            }
        else {
            holder.bill.setBackground(context.getResources().getDrawable(R.drawable.btn_bg));
            canBill = true;
        }

        if (model.getDueDate() != null) {
            holder.dueDate.setText("Due Date: " + model.getDueDate() + "-" + CommonUtils.getMonthY(System.currentTimeMillis()));
        } else {
            holder.dueDate.setText("");
        }

        holder.name.setText((position + 1) + ") Name: " + model.getName() + "\n    Phone: " + model.getPhone() + "\n    Adr: " + model.getAddress());
        holder.bill.setText("Bill: Rs " + model.getBill() + "/-");
        final boolean finalCanBill = canBill;
        holder.bill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalCanBill) {
                    callbacks.onGenerateBill(model);
                } else {
                    if (SharedPrefs.getAgent() != null) {
                        CommonUtils.showToast("Already billed");
                    } else {
                        CommonUtils.showToast("Only agent can bill");
                    }
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canClick) {
                    Intent i = new Intent(context, EditCustomer.class);
                    i.putExtra("userid", model.getPhone());
                    context.startActivity(i);
                }
            }
        });

        holder.phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:" + model.getPhone()));
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
        TextView name, dueDate;
        ImageView phone;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bill = itemView.findViewById(R.id.bill);
            dueDate = itemView.findViewById(R.id.dueDate);
            name = itemView.findViewById(R.id.name);
            phone = itemView.findViewById(R.id.phone);


        }
    }

    public interface UserListCallbacks {
        public void onGenerateBill(UserModel model);
    }
}
