package com.example.cephproject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Command {

    public static  String executeCommand(String[] command){

        StringBuilder output = new StringBuilder();
        StringBuilder err = new StringBuilder();

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line).append("\n");
            }

            BufferedReader erreader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String eline = "";
            while ((eline = erreader.readLine())!= null) {
                err.append(eline).append("\n");
            }
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response + "\n;err;\n" + err;
    }
}
