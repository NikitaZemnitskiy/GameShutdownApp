# Steam Game Shutdown App

## Overview
The Steam Game Shutdown App is a Java application designed to monitor your Steam library and automatically shut down your computer if any game is running. This helps to prevent unintended computer usage while gaming and ensures that your system resources are not wasted.

## Features
- Monitors running Steam games on Windows.
- Displays a warning dialog if a game is detected.
- Provides a 30-second countdown to close the game.
- Shuts down the computer if the game is not closed within the time limit.
- Customizable and visually appealing warning dialog with a gradient background.

## Prerequisites
- Java Development Kit (JDK) 8 or higher.
- Apache Maven (for building the project).

## Installation
1. Clone this repository to your local machine.
   ```bash
   git clone https://github.com/yourusername/SteamGameShutdownApp.git
   ```
2. Navigate to the project directory.
   ```bash
   cd SteamGameShutdownApp
   ```
3. Compile and run the application.
   ```bash
   mvn clean install
   mvn exec:java -Dexec.mainClass="SteamGameShutdownApp"
   ```

## Usage
- Run the application.
- The app will continuously monitor for running games from your Steam library.
- If a game is detected, a warning dialog will appear, notifying you to close the game within 30 seconds.
- If the game is not closed within the time limit, the application will initiate a system shutdown.

## Customization
- Modify the `timeRemaining` variable in the code to change the countdown duration.
- Change the background colors in the `showWarningDialog` method to customize the appearance of the warning dialog.

## Contributing
Contributions are welcome! Feel free to open issues or submit pull requests.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
