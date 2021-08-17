package com.example.cephproject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.cephproject.databinding.FragmentSecondBinding;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;

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
            Log.d("DEBUG", out);
            return null;
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Startup().execute();
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}