package com.example.cephproject;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.cephproject.databinding.FragmentFirstBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class FirstFragment extends Fragment {

    private FragmentFirstBinding binding;
    SharedPreferences sharedPref;

    private static class Startup extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            // this method is executed in a background thread
            // no problem calling su here
            Log.d("DEBUG", "doInBackground: ");

            String out = Command.executeCommand(new String[]{"su", "-c", "id"});
            out += Command.executeCommand(new String[]{"su", "-c", "umount /mnt/runtime/write/emulated/0/CephMount"});
            out += Command.executeCommand(new String[]{"su", "-c", "umount /mnt/CephMount"});
            out += Command.executeCommand(new String[]{"su", "-c", "rm -r /mnt/runtime/write/emulated/0/CephMount"});
            out += Command.executeCommand(new String[]{"su", "-c", "rm -r /mnt/CephMount"});
            out += Command.executeCommand(new String[]{"su", "-c", "mkdir -p /mnt/runtime/write/emulated/0/CephMount"});
            out += Command.executeCommand(new String[]{"su", "-c", "mkdir -p /mnt/CephMount"});
            out += Command.executeCommand(new String[]{"su", "-c", "busybox mount -t ceph 34.121.173.162:/ /mnt/CephMount -o name=oksi,secret=AQDm3+Zg71Q8CxAANUonceGAD0JwizEINQRQ8A==,context=u:object_r:sdcardfs:s0"});
            out += Command.executeCommand(new String[]{"su", "-c", "mount -t sdcardfs -o nosuid,nodev,noexec,noatime,mask=7,gid=9997 /mnt/CephMount /mnt/runtime/write/emulated/0/CephMount"});
            Log.d("DEBUG", out);
            return null;
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPref = getActivity().getSharedPreferences(getString(R.string.CephFSuser), Context.MODE_PRIVATE);
        Log.d("DEBUG", sharedPref.getString("user","null"));
        Context ctx = this.getContext();

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(ctx);
                EditText txt = requireView().findViewById(R.id.IPaddr);
                String cephURL = "http://" + txt.getText().toString();

                txt = requireView().findViewById(R.id.Username);
                String url = cephURL + ":2480/user/" + txt.getText().toString();
                Log.d("DEBUG", "URL: " + url);

                if (ContextCompat.checkSelfPermission(
                        ctx, Manifest.permission.INTERNET) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    // Request a string response from the provided URL.
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    // Display the first 20 characters of the response string.
                                    Log.d("DEBUG", response.substring(0,20));
                                    Snackbar.make(view, "Target: " + cephURL + " is ready to mount!", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("DEBUG", error.getMessage());
                            Snackbar.make(view, "Didn't work!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    });
                    queue.add(stringRequest);
                } else {
                    Log.d("DEBUG", "Requesting Permission");
                    requestPermissions(new String[] {Manifest.permission.INTERNET},200);
                }

            }
        });

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Mounting...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if (ContextCompat.checkSelfPermission(
                        ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    // You can use the API that requires the permission.
                    String folder_main = "CephMount";
                    new Startup().execute();
//                    File f = new File(Environment.getExternalStorageDirectory(), folder_main);
//                    Log.d("DEBUG", f.getAbsolutePath());
//                    Log.d("DEBUG", String.valueOf(f.exists()));
//                    try {
//                        if (!f.exists()) {
//                            boolean made = f.mkdirs();
//                            Log.d("DEBUG", String.valueOf(made));
//                        }
//
//                    } catch (Exception e) {
//                        Log.e("FATAL", e.toString());
//                    }
                    NavHostFragment.findNavController(FirstFragment.this)
                            .navigate(R.id.action_FirstFragment_to_SecondFragment);
                } else {
                    Log.d("DEBUG", "Requesting Permission");
                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},200);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}