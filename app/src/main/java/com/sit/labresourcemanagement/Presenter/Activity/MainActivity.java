package com.sit.labresourcemanagement.Presenter.Activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sit.labresourcemanagement.Presenter.Fragment.PO.POCheckInOutAsset;
import com.sit.labresourcemanagement.Presenter.Fragment.PO.POHomeFragment;
import com.sit.labresourcemanagement.Presenter.Fragment.PO.POPendingRequest;
import com.sit.labresourcemanagement.Presenter.Fragment.PO.PORecentCheckout;
import com.sit.labresourcemanagement.Presenter.Fragment.Student.StudentAttendanceFragment;
import com.sit.labresourcemanagement.Presenter.Fragment.Student.StudentBookingFragment;
import com.sit.labresourcemanagement.Presenter.Fragment.Student.StudentHomeFragment;
import com.sit.labresourcemanagement.Presenter.Fragment.Student.StudentItemFragment;
import com.sit.labresourcemanagement.Presenter.Fragment.Student.StudentLearningGalleryFragment;
import com.sit.labresourcemanagement.Presenter.Fragment.Student.StudentLoanFragment;
import com.sit.labresourcemanagement.Model.SharedPrefManager;
import com.sit.labresourcemanagement.R;

import static android.Manifest.permission_group.STORAGE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String CURRENT_NAVID = "currentNavID";
    private static final String TAG_NAVID = "navid";


    private static String USER_ROLE = null;
    private static Fragment fragment = null;
    private static int currentNavID = 666;
    private static int newNavID = 999;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private View header;
    private TextView name, email;
    private boolean doubleBackToExitPressedOnce = false;


    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 3;

    /*All main fragments*/
    //Student
    Fragment studentHomeFragment = null;
    Fragment attendanceFragment = null;
    Fragment learningGalleryFragment = null;
    Fragment bookingFragment = null;
    Fragment loanAssetFragment = null;
    Fragment studentItemFragment = null;
    //PO
    Fragment poHomeFragment = null;
    Fragment checkoutAssetFragment = null;
    Fragment pendingRequestFragment = null;
//    Fragment recentCheckoutFragment = null;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
	Menu menu = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create drawer aka sidebar MenuModel (hidden affordance)
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Get the role of current user
        USER_ROLE = SharedPrefManager.getInstance(getApplicationContext()).getUser().getRole();


        /*Navigation drawer*/
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Navigation view header, set name and email
        header = navigationView.getHeaderView(0);
        menu = getNavigationView().getMenu();
        name = header.findViewById(R.id.main_name);
        email = header.findViewById(R.id.main_email);
        loadNavDrawer();

        //Load the last visited page
        if(savedInstanceState != null){
            newNavID = savedInstanceState.getInt(CURRENT_NAVID);
        } else {
            newNavID = 0;
        }

        // Load all fragments
        if (USER_ROLE.equals("student")){
            studentHomeFragment = new StudentHomeFragment();
            attendanceFragment = new StudentAttendanceFragment();
            bookingFragment = new StudentBookingFragment();
            learningGalleryFragment = new StudentLearningGalleryFragment();
            loanAssetFragment = new StudentLoanFragment();
            studentItemFragment = new StudentItemFragment();
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
            }
        } else if (USER_ROLE.equals("PO")){
            poHomeFragment = new POHomeFragment();
            checkoutAssetFragment = new POCheckInOutAsset();
            pendingRequestFragment = new POPendingRequest();
//            recentCheckoutFragment = new PORecentCheckout();
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }


        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(STORAGE)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{STORAGE},
                                                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_CAMERA:{
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);


            }

        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



    @Override
    protected void onStart() {
        //Load Home page
        onNavigationItemSelected(menu.getItem(newNavID));
        super.onStart();
    }


	@Override
	protected void onDestroy() {
    	currentNavID = 666;
		super.onDestroy();
	}

	@Override
    public void onBackPressed() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        // If drawer open then close, else exit app
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!getFragmentName().equals("Home")) {
            	//This section is to manage the navigation drawer selection and the title.
				//There are some exception where the code looks different because we want to prevent the user from going back to scanner or loan page.

				//How this works is the currentNavId will check to see if it has the same number as the newNavId.
				// Reason for this being if its the same, we would not want to create a duplicate fragment.

				//If user navigates from scan to learning gallery, this will bring the user back to the loan asset page instead of the new loan page.
				if(getFragmentName().equals("ScanToLG")){
					currentNavID = 666;
					super.onBackPressed();
				}

				//Default on back pressed
                super.onBackPressed();

                //This is to change the title as well as the selected navigation menu.
                String title = getFragmentName();
                switch (title){
					case "Universal":
						displaySelectedItem(0, "Home");
						currentNavID = 666;
						title = "Home";
                    case "Home":
                        newNavID = 0;
                        break;
                    //Student
                    case "Attendance":
                        newNavID = 1;
                        break;
                    case "Booking":
                        newNavID = 2;
                        break;
                    case "Learning Gallery":
                        newNavID = 3;
                        break;
                    case "Loan Asset":
                        newNavID = 4;
                        break;
                    case "VIew Items":
                        newNavID = 5;
                        break;
                    case "Scan":
                        this.onBackPressed();
                        break;

                    //PO
                    case "Asset Check In/Out":
                        newNavID = 1;
                        break;
                    case "Pending Request":
                        newNavID = 2;
                        break;
//                    case "Recent Checkout":
//                        newNavID = 3;
//                        break;
                }
                if (newNavID != currentNavID){
                    currentNavID = newNavID;
                    setToolbarTitle(title);
                    menu.getItem(newNavID).setChecked(true);
                }
            } else {
                //Press back again to exit
                if (doubleBackToExitPressedOnce) {
                	finish();
                    super.onBackPressed();
                    return;
                }

                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                //Set timer for user next click
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);

            }

        }
    }

    private void loadNavDrawer() {
    	//To prepare the navigation drawer menu depending on role.
        if (SharedPrefManager.getInstance(getApplicationContext()) != null){
            name.setText(SharedPrefManager.getInstance(getApplicationContext()).getUser().getId());
            email.setText(SharedPrefManager.getInstance(getApplicationContext()).getUser().getEmail());

            // Load MenuModel for navigation drawer
            if (USER_ROLE.equals("PO")){
                navigationView.inflateMenu(R.menu.activity_po_main_drawer);
            } else if (USER_ROLE.equals("student")){
                navigationView.inflateMenu(R.menu.activity_main_drawer);
            }

        } else {
            finish();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displaySelectedItem(item.getItemId(), item.getTitle().toString());

        return true;
    }

    public void displaySelectedItem(int itemId, String title){
    	//Handles all the fragment changes in the main menu.
		//If the currentNavId and newNavId don't match, it means the user is from a different page.
		//Therefore, we change the fragment for them. this is to avoid duplicated fragments.

        if ((itemId == R.id.nav_PO_Logout) || (itemId == R.id.nav_logout)) {
            currentNavID = 666;
            fragment = null;
            SharedPrefManager.getInstance(getApplicationContext()).logout();
            finish();
			startActivity(new Intent(this, LoginActivity.class));
        }

        if (USER_ROLE.equals("student")) {
            switch (itemId) {
                case R.id.nav_home:
                    fragment = studentHomeFragment;
                    newNavID = 0;
                    break;

                case R.id.nav_attendance:
                    fragment = attendanceFragment;
                    newNavID = 1;
                    break;

                case R.id.nav_bookings:
                    fragment = bookingFragment;
                    newNavID = 2;
                    break;

                case R.id.nav_learning_gallery:
                    fragment = learningGalleryFragment;
                    newNavID = 3;
                    break;

                case R.id.nav_loan_assets:
                    fragment = loanAssetFragment;
                    newNavID = 4;
                    break;
                case R.id.nav_items:
                    fragment = studentItemFragment;
                    newNavID = 5;
                    break;
            }
        } else if(USER_ROLE.equals("PO")) {
            switch (itemId) {
                case R.id.nav_po_home:
                    fragment = poHomeFragment;
                    newNavID = 0;
                    break;

                case R.id.nav_checkout_asset:
                    fragment = checkoutAssetFragment;
                    newNavID = 1;
                    break;

                case R.id.nav_pending_loan:
                    fragment = pendingRequestFragment;
                    newNavID = 2;
                    break;


            }
        }

        if ((fragment != null) && (newNavID != currentNavID)){
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment);
            fragmentTransaction.addToBackStack(title);
            currentNavID = newNavID;
            fragmentTransaction.commit();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        setToolbarTitle(title);
    }

    public void setToolbarTitle(String title) {
		//Change the title
        assert getSupportActionBar() != null;
        getSupportActionBar().setTitle(title);
    }

    private String getFragmentName() {
    	// When changing fragments, we added a tag of where the previous fragment to the transaction.
		// This function will help us check the name of that tag and bring the user back to the previous fragment.
		// Mainly for use with back pressed.
        int index = fragmentManager.getBackStackEntryCount() - 1;
        if (index < 0)
            return "Home";

        FragmentManager.BackStackEntry backStackEntry = getSupportFragmentManager().getBackStackEntryAt(index);
        if(backStackEntry.getName() == null)
        	return "default";

        return backStackEntry.getName();
    }

    public NavigationView getNavigationView() {
        return navigationView;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(CURRENT_NAVID, currentNavID);
        super.onSaveInstanceState(outState);
    }

    //For some fragments
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.options_menu, menu);
		return true;
	}

	//For some fragments, we removed the navigation drawer so that they have to press the back button which makes life easier for us since
	//we dont have to handle the back press.
	public void disableDrawer(){
    	toggle.setDrawerIndicatorEnabled(false);
    	drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	public void enableDrawer(){
		toggle.setDrawerIndicatorEnabled(true);
    	drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}
}
