package com.example.product_notes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressTask extends AsyncTask<Location, Void, String> {
    private Context context;
    private OnTaskCompleted mListener;

    interface OnTaskCompleted {
        void onTaskCompleted(String result);
    }

    public FetchAddressTask(Context applicationContext, OnTaskCompleted listener) {
        context = applicationContext;
        mListener = listener;
    }

    @Override
    protected String doInBackground(Location... locations) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        Location location = locations[0];
        List<Address> addresses = null;
        String resultMessage = "";

        try{
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address
                    1);
        } catch (IOException e) {
            resultMessage = "Service not available";
            e.printStackTrace();
        } catch (IllegalArgumentException illegalArgumentException) {
            // Catch invalid latitude or longitude values
            resultMessage = "Invalid coordinates used";
        }

        if (addresses == null || addresses.size() == 0) {
            // the case where Geocoder is not able to find the address
            if (resultMessage.isEmpty()) {
                resultMessage = "No address found";
            }
        }
        else {
            // If an address is found, read it into resultMessage
            Address address = addresses.get(0);
            ArrayList<String> addressParts = new ArrayList<>();

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressParts.add(address.getAddressLine(i));
            }

            resultMessage = TextUtils.join("\n", addressParts);
        }

        return resultMessage;
    }

    @Override
    protected void onPostExecute(String s) {
        mListener.onTaskCompleted(s);
        super.onPostExecute(s);
    }
}