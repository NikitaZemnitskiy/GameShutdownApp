package com.zemnitskiy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SteamGameCloseApp {

    public static void main(String[] args) {
        while (true) {
            try {
                // Check if any Steam game is running
                if (isGameRunningOnSteamWindows()) {
                    // If a game is detected, close it
                    closeRunningGames();
                }

                // Delay before the next check
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // Method to check if a game is running via Steam (Windows)
    private static boolean isGameRunningOnSteamWindows() throws IOException {
        String steamPid = getSteamProcessPID();
        if (steamPid != null) {
            return hasChildProcesses(steamPid); // Check for Steam's child processes (games)
        }
        return false;
    }

    // Method to get Steam process PID
    private static String getSteamProcessPID() throws IOException {
        Process process = Runtime.getRuntime().exec("tasklist /fi \"imagename eq steam.exe\"");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.contains("steam.exe")) {
                String[] details = line.trim().split("\\s+");
                return details[1]; // Return Steam's PID
            }
        }

        return null;
    }

    // Method to check if Steam has child processes (games)
    private static boolean hasChildProcesses(String steamPid) throws IOException {
        Process process = Runtime.getRuntime().exec("wmic process where (ParentProcessId=" + steamPid + ") get ProcessId,Caption");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            if (!line.trim().isEmpty() && !line.contains("Caption") && !line.contains("wmic") && !line.contains("steam")) {
                return true; // Child process found, likely a game
            }
        }

        return false;
    }

    // Method to close all running Steam games
    private static void closeRunningGames() throws IOException {
        String steamPid = getSteamProcessPID();
        if (steamPid != null) {
            Process process = Runtime.getRuntime().exec("wmic process where (ParentProcessId=" + steamPid + ") get ProcessId,Caption");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && !line.contains("Caption") && !line.contains("wmic") && !line.contains("steam")) {
                    String gameName = line.trim().replace(".exe", ""); // Remove .exe from the game name
                    System.out.println("Closing game: " + gameName); // Output the game name
                    // Close the game process
                    Runtime.getRuntime().exec("taskkill /F /PID " + getProcessId(line));
                }
            }
        }
    }

    // Helper method to extract the process ID from the line
    private static String getProcessId(String line) {
        String[] details = line.trim().split("\\s+");
        return details[0]; // Return the first element as the PID
    }
}
