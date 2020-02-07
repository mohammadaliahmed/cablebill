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

public class ContactUserListAdapter extends RecyclerView.Adapter<ContactUserListAdapter.ViewHolder> {
    Context context;
    ArrayList<UserModel> itemList;
    ArrayList<String> contactList;
    ContactListCallbacks callbacks;


    public ContactUserListAdapter(Context context, ArrayList<UserModel> itemList, ArrayList<String> contactList) {
        this.context = context;
        this.itemList = itemList;
        this.contactList = contactList;
    }

    public void setContactList(ArrayList<String> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public void setItemList(ArrayList<UserModel> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    public void setCallbacks(ContactListCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_user_item_layout, parent, false);
        ContactUserListAdapter.ViewHolder viewHolder = new ContactUserListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserModel model = itemList.get(position);


        holder.name.setText((position + 1) + ") Name: " + model.getName() + "\n    Phone: " + model.getPhone());

        boolean canSaveContact = true;
        if (contactList.contains(model.getPhone())) {
            canSaveContact = false;
            holder.saveContact.setBackground(context.getResources().getDrawable(R.drawable.btn_bg));
            holder.saveContact.setText("In Contacts");
        } else {
            canSaveContact = true;
            holder.saveContact.setBackground(context.getResources().getDrawable(R.drawable.btn_bg_red));
            holder.saveContact.setText("Save Contact");
        }
        final boolean finalCanSaveContact = canSaveContact;
        holder.saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalCanSaveContact) {
                    callbacks.onSaveContact(model);

                } else {
                    callbacks.onDeleteContact(model);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button saveContact;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            saveContact = itemView.findViewById(R.id.saveContact);
            name = itemView.findViewById(R.id.name);


        }
    }

    public interface ContactListCallbacks {
        public void onSaveContact(UserModel model);
        public void onDeleteContact(UserModel model);
    }
}
