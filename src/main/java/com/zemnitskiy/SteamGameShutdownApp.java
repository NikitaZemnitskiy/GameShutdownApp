package com.zemnitskiy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SteamGameShutdownApp {

    private static Timer countdownTimer;
    private static int timeRemaining = 30; // 30 seconds timer
    private static JDialog dialog = null;
    private static String gameName = "Unknown game"; // Default game name

    public static void main(String[] args) {
        while (true) {
            try {
                // Check if any Steam game is running
                if (isGameRunningOnSteamWindows()) {
                    // If a game is running, show warning window
                    if (dialog == null || !dialog.isVisible()) {
                        showWarningDialog();
                    }
                } else {
                    // If game is closed, hide warning window and reset timer
                    if (dialog != null && dialog.isVisible()) {
                        closeWarningDialog();
                    }
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
                gameName = line.trim();
                gameName = gameName.substring(0, gameName.indexOf(".exe"));
                return true; // Child process found, likely a game
            }
        }

        return false;
    }

    // Method to show the warning dialog
    private static void showWarningDialog() {
        dialog = new JDialog();
        dialog.setTitle("Warning! Game Detected");
        dialog.setSize(400, 250);
        dialog.setAlwaysOnTop(true);
        dialog.setModal(true); // Modal window
        dialog.setUndecorated(true); // No frame

        // Create a custom panel with background and text
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Gradient background
                Graphics2D g2d = (Graphics2D) g;
                Color startColor = new Color(255, 140, 0); // Orange
                Color endColor = new Color(255, 69, 0);   // Red
                GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setLayout(new GridLayout(3, 1));

        // Warning label
        JLabel warningLabel = new JLabel("Game \"" + gameName + "\" is running! Close it within 30 seconds.");
        warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
        warningLabel.setFont(new Font("Arial", Font.BOLD, 16));
        warningLabel.setForeground(Color.WHITE);

        // Timer label
        JLabel timerLabel = new JLabel("Time left: 30 seconds", SwingConstants.CENTER);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        timerLabel.setForeground(Color.WHITE);

        // Add components to the panel
        panel.add(warningLabel);
        panel.add(timerLabel);

        // Countdown timer
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                timerLabel.setText("Time left: " + timeRemaining + " seconds");
                if (timeRemaining <= 0) {
                    countdownTimer.stop();
                    dialog.dispose();
                    try {
                        // Shutdown the computer if timer runs out and game is still running
                        if (isGameRunningOnSteamWindows()) {
                            shutdownComputer();
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
        countdownTimer.start();

        // Add panel to the dialog and display the window
        dialog.add(panel);
        dialog.setLocationRelativeTo(null); // Center window
        dialog.setVisible(true);
    }

    // Method to close the warning dialog if game is closed
    private static void closeWarningDialog() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }
        if (dialog != null) {
            dialog.dispose();
        }
        timeRemaining = 30; // Reset timer
    }

    // Method to shutdown the computer
    private static void shutdownComputer() throws IOException {
        Runtime.getRuntime().exec("shutdown -s -t 0");
    }
}
