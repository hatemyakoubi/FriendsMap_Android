package hatem.yakoubi.friendslocation.ui.gallery;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import hatem.yakoubi.friendslocation.Adapter.LocationAdapter;
import hatem.yakoubi.friendslocation.R;
import hatem.yakoubi.friendslocation.databinding.FragmentListBinding;
import hatem.yakoubi.friendslocation.services.LocationRestApiService;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private LocationAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_list, container, false);

        ListView listViewPositions = root.findViewById(R.id.listViewPositions);

        // Initialize the adapter with an empty list
        adapter = new LocationAdapter(requireContext(), new ArrayList<>());
        listViewPositions.setAdapter(adapter);

        // Fetch friend locations when the fragment is created
        getFriendsLocations();

        return root;
    }
    private void getFriendsLocations() {
        new RetrievePositionsTask().execute();
    }
    private class RetrievePositionsTask extends AsyncTask<Void, Void, List<LatLng>> {

        @Override
        protected List<LatLng> doInBackground(Void... voids) {
            // Call the method from LocationApiService to fetch friend locations
            return new LocationRestApiService().getFriendsLocations();
        }

        @Override
        protected void onPostExecute(List<LatLng> positions) {
            super.onPostExecute(positions);
            // Clear previous data
            adapter.clear();
            // Add fetched friend locations to the adapter
            adapter.addAll(positions);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}