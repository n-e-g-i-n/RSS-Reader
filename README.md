# Java RSS Reader

This is a simple Java application that enables users to manage and view updates from their favorite RSS feeds.

## Features
- Add a new RSS feed URL.
- Delete an existing RSS feed URL.
- View updates from selected RSS feeds.

## Installing
1. Clone the repository to your local machine:
   `
   git clone https://github.com/MKmasterg/JavaRSSReader.git
   `
2. Download the jsoup library from [https://jsoup.org/](https://jsoup.org/). Download the latest version of the library.
3. Include the JSOUP library in your project:
   - If you're using IntelliJ IDEA:
     - Open your project in IntelliJ IDEA.
     - Go to File -> Project Structure -> Libraries.
     - Click on the + button and select Java.
     - Navigate to the location where you downloaded the JSOUP library, select the .jar file, and click OK.
     - Click Apply and then OK.
4. Open the project in your IDE.
5. Run the Main.java file to start the application.

## Usage
The application presents a menu with four options:
- Show updates: View updates from selected RSS feeds. Choose to view updates from all feeds or select a specific one.
- Add URL: Add a new RSS feed URL to your list.
- Delete URL: Delete an existing RSS feed URL from your list.
- Exit: Exit the application. Selecting this option will write the current state of your RSS feeds to a file (data.txt) for restoring previous settings on next run.
# RSS-Reader
