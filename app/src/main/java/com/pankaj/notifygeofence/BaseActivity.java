package com.pankaj.notifygeofence;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by Pankaj Kumar on 8/20/2017.
 * pankaj.arrah@gmail.com
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private boolean shouldSendToSettings = false;
    private SharedPreferences mPermissionCache;
    private String mRequestedPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPermissionCache = getSharedPreferences("PermissionCache", MODE_PRIVATE);
    }

    /**
     * Continue task after permission granted.
     *
     * @param permission
     */
    public abstract void proceedAfterPermission(String permission);

    protected void requestPermission(final String permission) {
        mRequestedPermission = permission;
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                // Inform user about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission Required");
                builder.setMessage("This app needs " + permission + " permission");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(BaseActivity.this, new String[]{permission}, LOCATION_PERMISSION_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (mPermissionCache.getBoolean(permission, false)) {
                // Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permission Required");
                builder.setMessage("This app needs " + permission + " permission");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        shouldSendToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant " + permission, Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                // Just request the permission
                ActivityCompat.requestPermissions(this, new String[]{permission}, LOCATION_PERMISSION_CONSTANT);
            }

            // Cache the status of permission
            SharedPreferences.Editor editor = mPermissionCache.edit();
            editor.putBoolean(permission, true);
            editor.commit();
        } else {
            // We already have the permission, just go ahead.
            proceedAfterPermission(permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CONSTANT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted...
                proceedAfterPermission(mRequestedPermission);
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, mRequestedPermission)) {
                    //Show Information about why you need the permission
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Permission Required");
                    builder.setMessage("This app needs " + mRequestedPermission + " permission");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            ActivityCompat.requestPermissions(BaseActivity.this, new String[]{mRequestedPermission}, LOCATION_PERMISSION_CONSTANT);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                } else {
                    Toast.makeText(getBaseContext(), "Unable to get Permission", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(this, mRequestedPermission) == PackageManager.PERMISSION_GRANTED) {
                // Got Permission
                proceedAfterPermission(mRequestedPermission);
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (shouldSendToSettings) {
            if (ActivityCompat.checkSelfPermission(this, mRequestedPermission) == PackageManager.PERMISSION_GRANTED) {
                //Got Permission
                proceedAfterPermission(mRequestedPermission);
            }
        }
    }
}