package ru.mirea.fedorov.mireaproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.Arrays;
import java.util.List;

import ru.mirea.fedorov.mireaproject.databinding.FragmentEstablishmentsBinding;

public class EstablishmentsFragment extends Fragment {

    private FragmentEstablishmentsBinding binding;
    private MyLocationNewOverlay locationOverlay;
    private CompassOverlay compassOverlay;

    private final List<Establishment> establishments = Arrays.asList(
            new Establishment(
                    R.string.establishment_mirea_name,
                    R.string.establishment_mirea_address,
                    R.string.establishment_mirea_description,
                    new GeoPoint(55.669986, 37.480409)
            ),
            new Establishment(
                    R.string.establishment_pushkin_name,
                    R.string.establishment_pushkin_address,
                    R.string.establishment_pushkin_description,
                    new GeoPoint(55.765491, 37.604503)
            ),
            new Establishment(
                    R.string.establishment_gum_name,
                    R.string.establishment_gum_address,
                    R.string.establishment_gum_description,
                    new GeoPoint(55.754646, 37.621564)
            ),
            new Establishment(
                    R.string.establishment_zaryadye_name,
                    R.string.establishment_zaryadye_address,
                    R.string.establishment_zaryadye_description,
                    new GeoPoint(55.751737, 37.628765)
            ),
            new Establishment(
                    R.string.establishment_vdnh_name,
                    R.string.establishment_vdnh_address,
                    R.string.establishment_vdnh_description,
                    new GeoPoint(55.829808, 37.633651)
            )
    );

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                Boolean coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);
                if (Boolean.TRUE.equals(fineLocationGranted) || Boolean.TRUE.equals(coarseLocationGranted)) {
                    enableUserLocation();
                } else if (binding != null) {
                    binding.textMapStatus.setText(R.string.establishments_location_permission_denied);
                }
            });

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        Configuration.getInstance().load(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentEstablishmentsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupMap(binding.mapView);
        binding.buttonShowAllEstablishments.setOnClickListener(v -> showAllEstablishments());
        binding.buttonMyLocation.setOnClickListener(v -> requestLocationOrEnableLayer());
        binding.textMapStatus.setText(getString(
                R.string.establishments_status_template,
                establishments.size()
        ));
        showAllEstablishments();
    }

    private void setupMap(MapView mapView) {
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setZoomRounding(true);
        mapView.setTilesScaledToDpi(true);
        mapView.setMinZoomLevel(4.0);
        mapView.setMaxZoomLevel(20.0);

        IMapController controller = mapView.getController();
        controller.setZoom(11.0);
        controller.setCenter(new GeoPoint(55.755864, 37.617698));

        addMapOverlays(mapView);
        addEstablishmentMarkers(mapView);
    }

    private void addMapOverlays(MapView mapView) {
        compassOverlay = new CompassOverlay(
                requireContext(),
                new InternalCompassOrientationProvider(requireContext()),
                mapView
        );
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);

        DisplayMetrics displayMetrics = requireContext().getResources().getDisplayMetrics();
        ScaleBarOverlay scaleBarOverlay = new ScaleBarOverlay(mapView);
        scaleBarOverlay.setCentred(true);
        scaleBarOverlay.setScaleBarOffset(displayMetrics.widthPixels / 2, 10);
        mapView.getOverlays().add(scaleBarOverlay);
    }

    private void addEstablishmentMarkers(MapView mapView) {
        for (Establishment establishment : establishments) {
            Marker marker = new Marker(mapView);
            marker.setPosition(establishment.point);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
            marker.setTitle(getString(establishment.nameResId));
            marker.setInfoWindow(null);
            marker.setOnMarkerClickListener((clickedMarker, clickedMapView) -> {
                showEstablishmentInfo(establishment);
                clickedMapView.getController().animateTo(establishment.point);
                return true;
            });
            mapView.getOverlays().add(marker);
        }
    }

    private void showAllEstablishments() {
        if (binding == null) {
            return;
        }
        binding.mapView.post(() -> {
            if (binding != null) {
                binding.mapView.zoomToBoundingBox(buildBoundingBox(), true, 96);
            }
        });
    }

    private BoundingBox buildBoundingBox() {
        double north = -90.0;
        double south = 90.0;
        double east = -180.0;
        double west = 180.0;

        for (Establishment establishment : establishments) {
            double latitude = establishment.point.getLatitude();
            double longitude = establishment.point.getLongitude();
            north = Math.max(north, latitude);
            south = Math.min(south, latitude);
            east = Math.max(east, longitude);
            west = Math.min(west, longitude);
        }

        return new BoundingBox(north, east, south, west);
    }

    private void requestLocationOrEnableLayer() {
        if (hasLocationPermission()) {
            enableUserLocation();
            return;
        }

        locationPermissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void enableUserLocation() {
        if (binding == null) {
            return;
        }

        binding.textMapStatus.setText(R.string.establishments_location_loading);
        if (locationOverlay == null) {
            locationOverlay = new MyLocationNewOverlay(
                    new GpsMyLocationProvider(requireContext()),
                    binding.mapView
            );
            locationOverlay.enableMyLocation();
            locationOverlay.enableFollowLocation();
            binding.mapView.getOverlays().add(locationOverlay);
        } else {
            locationOverlay.enableMyLocation();
            locationOverlay.enableFollowLocation();
        }

        binding.mapView.invalidate();
        binding.textMapStatus.setText(R.string.establishments_location_enabled);
        locationOverlay.runOnFirstFix(() -> {
            GeoPoint currentLocation = locationOverlay == null
                    ? null
                    : locationOverlay.getMyLocation();
            if (currentLocation == null || !isAdded()) {
                return;
            }

            requireActivity().runOnUiThread(() -> {
                if (binding == null) {
                    return;
                }
                binding.mapView.getController().animateTo(currentLocation);
                binding.mapView.getController().setZoom(16.0);
            });
        });

        Location lastFix = locationOverlay.getLastFix();
        if (lastFix == null) {
            binding.textMapStatus.postDelayed(() -> {
                if (binding != null && locationOverlay != null && locationOverlay.getLastFix() == null) {
                    binding.textMapStatus.setText(R.string.establishments_location_unavailable);
                }
            }, 5_000);
        }
    }

    private void showEstablishmentInfo(Establishment establishment) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(establishment.nameResId)
                .setMessage(getString(
                        R.string.establishments_dialog_message_template,
                        getString(establishment.addressResId),
                        getString(establishment.descriptionResId)
                ))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Configuration.getInstance().load(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
        );
        if (binding != null) {
            binding.mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (binding != null) {
            binding.mapView.onPause();
        }
        Configuration.getInstance().save(
                requireContext(),
                PreferenceManager.getDefaultSharedPreferences(requireContext())
        );
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        if (locationOverlay != null) {
            locationOverlay.disableFollowLocation();
            locationOverlay.disableMyLocation();
        }
        if (compassOverlay != null) {
            compassOverlay.disableCompass();
        }
        if (binding != null) {
            binding.mapView.onDetach();
        }
        locationOverlay = null;
        compassOverlay = null;
        binding = null;
        super.onDestroyView();
    }

    private static class Establishment {
        @StringRes
        private final int nameResId;
        @StringRes
        private final int addressResId;
        @StringRes
        private final int descriptionResId;
        private final GeoPoint point;

        private Establishment(@StringRes int nameResId, @StringRes int addressResId,
                              @StringRes int descriptionResId, GeoPoint point) {
            this.nameResId = nameResId;
            this.addressResId = addressResId;
            this.descriptionResId = descriptionResId;
            this.point = point;
        }
    }
}
