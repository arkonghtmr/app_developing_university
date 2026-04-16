package ru.mirea.fedorov.mireaproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import ru.mirea.fedorov.mireaproject.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        binding.navView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            binding.navView.setCheckedItem(R.id.nav_home);
            openFragment(new HomeFragment(), getString(R.string.menu_home));
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            openFragment(new HomeFragment(), getString(R.string.menu_home));
        } else if (itemId == R.id.nav_sensors) {
            openFragment(new SensorFragment(), getString(R.string.menu_sensors));
        } else if (itemId == R.id.nav_camera) {
            openFragment(new CameraFragment(), getString(R.string.menu_camera));
        } else if (itemId == R.id.nav_recorder) {
            openFragment(new RecorderFragment(), getString(R.string.menu_recorder));
        } else if (itemId == R.id.nav_profile) {
            openFragment(new ProfileFragment(), getString(R.string.menu_profile));
        } else if (itemId == R.id.nav_files) {
            openFragment(new FilesFragment(), getString(R.string.menu_files));
        } else if (itemId == R.id.nav_network) {
            openFragment(new NetworkFragment(), getString(R.string.menu_network));
        } else if (itemId == R.id.nav_establishments) {
            openFragment(new EstablishmentsFragment(), getString(R.string.menu_establishments));
        } else if (itemId == R.id.nav_logout) {
            signOutAndOpenLogin();
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        } else {
            return false;
        }

        item.setChecked(true);
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openFragment(Fragment fragment, String title) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        setTitle(title);
    }

    private void signOutAndOpenLogin() {
        try {
            if (!FirebaseApp.getApps(this).isEmpty()) {
                FirebaseAuth.getInstance().signOut();
            }
        } catch (IllegalStateException ignored) {
            // The app can still return to the login screen if Firebase is not configured.
        }

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }
}
