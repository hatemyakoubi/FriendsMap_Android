package hatem.yakoubi.friendslocation.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import hatem.yakoubi.friendslocation.R;
import hatem.yakoubi.friendslocation.services.LocationRestApiService;

public class LocationAdapter extends ArrayAdapter <LatLng>{
    public LocationAdapter(@NonNull Context context, @NonNull List<LatLng> locations) {
        super(context, 0, locations);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LatLng location = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_lists, parent, false);
        }

        TextView textViewLatitude = convertView.findViewById(R.id.textViewLatitude);
        TextView textViewLongitude = convertView.findViewById(R.id.textViewLongitude);
        ImageButton buttonDelete = convertView.findViewById(R.id.buttonDelete);
        ImageButton buttonOpenMap = convertView.findViewById(R.id.buttonOpenMaps);

        if (location != null) {
            textViewLatitude.setText(String.valueOf(location.latitude));
            textViewLongitude.setText(String.valueOf(location.longitude));

            buttonDelete.setOnClickListener(v -> {
                // Build an alert dialog for confirmation
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Are you sure you want to delete this location?")
                        .setPositiveButton("Yes", (dialog, id) -> {
                            // User clicked Yes button
                            // Call the method from LocationApiService to delete the location
                           new LocationRestApiService().removeLocation(location);
                            // Remove the location from the adapter's list
                            remove(location);
                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            // User cancelled the dialog, do nothing
                        });
                // Create and show the alert dialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            });

            buttonOpenMap.setOnClickListener(v -> {
                // Create a Uri with the latitude and longitude
                Uri gmmIntentUri = Uri.parse("geo:" + location.latitude + "," + location.longitude + "?z=15&q=" + location.latitude + "," + location.longitude + "(This is your friend location)");
                // Create an intent with the Uri to open the map application
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                // Verify that there's a map application available
                if (mapIntent.resolveActivity(getContext().getPackageManager()) != null) {
                    // Start the map intent
                    getContext().startActivity(mapIntent);
                }
            });

        }

        return convertView;
    }
}
