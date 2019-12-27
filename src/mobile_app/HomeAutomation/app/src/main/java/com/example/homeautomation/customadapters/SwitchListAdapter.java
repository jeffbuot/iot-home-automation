package com.example.homeautomation.customadapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.homeautomation.R;
import com.example.homeautomation.classes.ChannelData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SwitchListAdapter extends ArrayAdapter {

    private final Activity context;
    private final ArrayList<ChannelData> channels;


    public SwitchListAdapter(Activity context, ArrayList<ChannelData> channels) {

        super(context, R.layout.list_item);
        this.context = context;
        this.channels = channels;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_item, null, true);
        final String channelName = channels.get(position).getName();
        ToggleButton toggleButton = rowView.findViewById(R.id.toggle);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.d("alarmCheck", channelName +" SET TO TRUE");
                    channels.get(position).setValue(true);
                    sendWorkPostRequest(channels.get(position).getId(),true);
                } else {
                    Log.d("alarmCheck", channelName+" SET TO FALSE");
                    channels.get(position).setValue(false);
                    sendWorkPostRequest(channels.get(position).getId(),false);
                }

            }
        });
        TextView textView = rowView.findViewById(R.id.txtName);
        textView.setText(channelName);
        return rowView;

    }

    private void sendWorkPostRequest(String id,boolean value) {

        RequestQueue queue = Volley.newRequestQueue(context);
        try {
            String URL = "http://c9566cad.ngrok.io/api/values";
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("id", id);
            jsonBody.put("value", value);

            JsonObjectRequest jsonOblect = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Toast.makeText(context, "Response:  " + response.toString(), Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {


                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    final Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Basic " + "c2FnYXJAa2FydHBheS5jb206cnMwM2UxQUp5RnQzNkQ5NDBxbjNmUDgzNVE3STAyNzI=");//put your token here
                    return headers;
                }
            };
            queue.add(jsonOblect);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_LONG).show();

    }
}

