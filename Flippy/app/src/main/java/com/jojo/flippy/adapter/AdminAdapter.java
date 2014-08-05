package com.jojo.flippy.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.johnpersano.supertoasts.SuperToast;
import com.google.gson.JsonObject;
import com.jojo.flippy.app.R;
import com.jojo.flippy.core.CommunityCenterActivity;
import com.jojo.flippy.profile.ManageChannelActivity;
import com.jojo.flippy.util.Flippy;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.List;

public class AdminAdapter extends ArrayAdapter<AdminPerson> {
    Context context;
    private ProgressDialog progressDialog;
    private SuperToast superToast;

    public AdminAdapter(Context context, int resourceId,
                        List<AdminPerson> items) {
        super(context, resourceId, items);
        this.context = context;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final AdminPerson rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.channel_admis_listview, null);
            holder = new ViewHolder();
            holder.imageViewAdminOne = (ImageView) convertView.findViewById(R.id.imageViewAdminOne);
            holder.buttonDemoteAdmin = (Button) convertView.findViewById(R.id.buttonDemoteAdmin);
            holder.textViewAdminEmail = (TextView) convertView.findViewById(R.id.textViewAdminEmail);
            holder.textViewAdminFullName = (TextView) convertView.findViewById(R.id.textViewAdminFullName);
            holder.textViewAdminId = (TextView) convertView.findViewById(R.id.textViewAdminId);
            convertView.setTag(holder);
        } else

            holder = (ViewHolder) convertView.getTag();

        holder.textViewAdminEmail.setText(rowItem.getProfileEmail());
        holder.textViewAdminId.setText(rowItem.getAdminId());
        holder.textViewAdminFullName.setText(rowItem.getProfileFullName());


        Ion.with(holder.imageViewAdminOne)
                .placeholder(R.drawable.user_place_small)
                .error(R.drawable.user_error_small)
                .animateIn(R.anim.fade_in)
                .load(String.valueOf(rowItem.getAdminProfileItem()));

        String creator = ManageChannelActivity.creatorId;
        if (!creator.equals(rowItem.getAdminId())) {
            Log.e("Admin adapter", creator + " " + rowItem.getAdminId());
            holder.buttonDemoteAdmin.setVisibility(View.VISIBLE);
        }

        holder.buttonDemoteAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String adminFullName = rowItem.getProfileFullName();
                String adminId = rowItem.getAdminId();
                confirmDemotion(adminFullName, adminId);
            }
        });

        return convertView;
    }



    private void confirmDemotion(String adminName, final String memberId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirm your action");
        builder.setIcon(R.drawable.icon_light_info);
        builder.setMessage("Are you sure you want to demote  " + adminName);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                demoteUser(memberId);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void demoteUser(final String memberId) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Demoting an admin ...");
        progressDialog.show();
        String URL = Flippy.CHANNELS_URL + ManageChannelActivity.channelId + "/demote_user/";
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", memberId);
        Ion.with(context)
                .load(URL)
                .setHeader("Authorization", "Token " + CommunityCenterActivity.userAuthToken)
                .setJsonObjectBody(jsonObject)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        progressDialog.dismiss();
                        try {

                            if (result != null) {
                                if (result.has("detail")) {
                                    showSuperToast("Sorry unable to demote user ", false);
                                    Log.e("Error removing user", result.toString());
                                    return;
                                } else {
                                    showSuperToast("successfully demoted admin", true);
                                    Intent intent = new Intent(context, CommunityCenterActivity.class);
                                    context.startActivity(intent);
                                    return;
                                }
                            } else if (e != null) {
                                showSuperToast(context.getResources().getString(R.string.internet_connection_error_dialog_title), true);
                                return;
                            } else {
                                showSuperToast("sorry something went wrong", false);
                            }

                        } catch (Exception exception) {
                            Log.e("Admin adapter", "Error demoting the user " + memberId + " " + exception.toString());
                        }
                    }
                });

    }

    private void showSuperToast(String message, boolean isSuccess) {
        superToast = new SuperToast(context);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
        superToast.setDuration(SuperToast.Duration.SHORT);
        if (isSuccess) {
            superToast.setBackground(SuperToast.Background.BLUE);
        } else {
            superToast.setBackground(SuperToast.Background.RED);
        }
        superToast.setTextSize(SuperToast.TextSize.MEDIUM);
        superToast.setIcon(R.drawable.ic_action_warning_light, SuperToast.IconPosition.LEFT);
        superToast.setText(message);
        superToast.show();
    }

    private class ViewHolder {
        ImageView imageViewAdminOne;
        Button buttonDemoteAdmin;
        TextView textViewAdminEmail,
                textViewAdminFullName,
                textViewAdminId;

    }

}