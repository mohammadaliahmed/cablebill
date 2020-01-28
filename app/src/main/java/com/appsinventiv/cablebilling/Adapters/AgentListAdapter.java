package com.appsinventiv.cablebilling.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.appsinventiv.cablebilling.Activities.Admin.AddAgent;
import com.appsinventiv.cablebilling.Activities.EditCustomer;
import com.appsinventiv.cablebilling.Models.AgentModel;
import com.appsinventiv.cablebilling.Models.UserModel;
import com.appsinventiv.cablebilling.R;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AgentListAdapter extends RecyclerView.Adapter<AgentListAdapter.ViewHolder> {
    Context context;
    ArrayList<AgentModel> itemList;
    AgentListCallbacks callbacks;

    public AgentListAdapter(Context context, ArrayList<AgentModel> itemList, AgentListCallbacks callbacks) {
        this.context = context;
        this.itemList = itemList;
        this.callbacks = callbacks;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.agent_item_layout, parent, false);
        AgentListAdapter.ViewHolder viewHolder = new AgentListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final AgentModel model = itemList.get(position);
        holder.name.setText((position + 1) + ") Name: " + model.getName() + "\n    Phone: " + model.getPhone());
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, AddAgent.class);
                i.putExtra("agentId", model.getPhone());
                context.startActivity(i);
            }
        });
        holder.recovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbacks.onAgentClicked(model);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button edit, recovery;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            edit = itemView.findViewById(R.id.edit);
            name = itemView.findViewById(R.id.name);
            recovery = itemView.findViewById(R.id.recovery);


        }
    }

    public interface AgentListCallbacks {
        public void onAgentClicked(AgentModel model);
    }

}
