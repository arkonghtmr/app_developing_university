package ru.mirea.fedorov.mireaproject;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;

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
