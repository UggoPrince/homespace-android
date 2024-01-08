package com.example.homespace

import android.Manifest.permission.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.homespace.databinding.ActivityMainBinding
import com.example.homespace.services.LocationService
import com.example.homespace.ui.signup.SignUpActivity
import com.example.homespace.utils.Network
import com.example.homespace.viewModels.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val locationService = LocationService.instance
    private lateinit var locationPermissionRequest: ActivityResultLauncher<Array<String>>
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: NavigationView
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout


    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        drawerLayout = binding.drawerLayout
        navView = binding.navView
        bottomNavView = findViewById(R.id.bottom_nav_view)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_agencies, R.id.nav_dashboard,
                // R.id.nav_signup, R.id.nav_login
            ), drawerLayout
        )
        drawerLayout.close()
        navView.setNavigationItemSelectedListener(this)
        setupActionBarWithNavController(navController, appBarConfiguration)
        // navView.setupWithNavController(navController)
        bottomNavView.setupWithNavController(navController)
        bottomNavView.menu.findItem(R.id.nav_dashboard).isVisible = false

        // setup location service
        // fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationService.setGeocoder(this)
        locationIsEnabled()
        networkIsEnabled()
        updatePermissionRequest()
        requestForLocationPermission()
        observeMainViewModel()
    }

    // check if location permission has been granted
    private fun updatePermissionRequest() {
            locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                val result = locationService.locationAccessIsGranted(permissions)
                mainViewModel.updateLocationAccessGranted(result)
                mainViewModel.updateCanGetLocation(locationService.canGetLocation(this))
            }
        }

    // request for location permission if absent
    private fun requestForLocationPermission() {
        if (!locationService.locationAccessGranted) {
            locationPermissionRequest.launch(
                arrayOf(
                    ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
            )
        }
        // Log.d("PERMISSION", mainViewModel.canGetLocation.toString())
    }

    /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
            // Inflate the menu; this adds items to the action bar if it is present.
            menuInflater.inflate(R.menu.main, menu)
            return true
        }*/

    override fun onResume() {
        super.onResume()
        uncheckAllNavViewItems()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationService.destroy()
    }

    // uncheck all navigation drawer items
    private fun uncheckAllNavViewItems() {
        for (i in 0 until navView.menu.size()) {
            navView.menu.getItem(i).isChecked = false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.nav_signup -> {
                drawerLayout.close()
                startActivity(Intent(this, SignUpActivity::class.java))
                true
            }
            else -> {
                drawerLayout.close()
                true
            }
        }
    }

    fun locationIsEnabled() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            locationService.locationEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun networkIsEnabled() {
        locationService.networkEnabled = Network.isOnline(this)
    }

    fun enableLocation() {
        AlertDialog.Builder(this)
            .setMessage("Turn On Location.")
            .setPositiveButton("Ok"
            ) { paramDialogInterface, _ ->
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                paramDialogInterface.dismiss()
            }
            .setNegativeButton("Not now.") {dialog, _ ->
                locationService.locationEnabled = false
                dialog.dismiss()
            }
            .show()
    }

    private fun observeMainViewModel() {
        //with(mainViewModel) {
            mainViewModel.address.observe(this) {
                Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
            }
        //}
    }
}