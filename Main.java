import java.util.*;
import java.net.*;
import org.jsoup.Jsoup;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;

public class RSS {
    private static FileWriter       fileWriter;
    private static final File       FILE      = new File("data.txt");
    private static final int        MAX_ITEMS = 5;
    private static Scanner          s;
    private static Scanner          fileScanner;


    public static void main(String[] args) {
        start();
    }

    private static int getActionMenuInput() {
        //Prompt
        printActionMenuPrompt();
        //Get input
        int input;
        do {
            try {
                input = Integer.parseInt(s.next());

                if (!isInRangeStart(input)){
                    throw new IllegalArgumentException();
                }
                else {
                    return input;
                }

            } catch (IllegalArgumentException | InputMismatchException ex) {
                System.out.println("Enter number Between 1 to 4!");
            }
        } while (true);
    }

    private static void printActionMenuPrompt() {
        System.out.print("\nType a valid number for your desired action:\n" +
                "[1] Show updates\n" +
                "[2] Add URL\n" +
                "[3] Remove URL\n" +
                "[4] Exit\n");
    }

    private static int getShowUpdateInput() {
        //Prompt
        printShowUpdatesPrompt();
        //Get input
        int input;
        do {
            try {
                input = Integer.parseInt(s.next());

                if (!isInRangeUpdate(input)){
                    throw new IllegalArgumentException();
                }
                else {
                    return input;
                }

            } catch (IllegalArgumentException | InputMismatchException ex) {
                System.out.println("Enter number Between -1 to "+getSavedTitles().size()+"!");
            }
        } while (true);
    }

    private static void printShowUpdatesPrompt() {
        System.out.println("Show Updates For:");
        System.out.println("[0] All websites" );
        for (int i = 0; i < getSavedTitles().size(); i++) {
            System.out.println("[" + (i+1) + "] " + getSavedTitles().get(i));
        }
        System.out.println("Enter -1 to return");
    }

    private static boolean isInRangeUpdate(int input) {
        return input >= -1 && input <= getSavedTitles().size();
    }

    private static boolean isInRangeStart(int inp) {
        return 1 <= inp && inp <= 4;
    }

    public static void start() {
        try {
            fileWriter = new FileWriter(FILE,true);
        } catch (IOException e) {
            dataError();
        }
        s = new Scanner(System.in);

        switch (getActionMenuInput()) {
            case 1:
                showUpdates();
                break;
            case 2:
                try {
                    addURL();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case 3:
                removeURL();
                break;
            case 4:
                System.out.println("See you later!");
                return;
        }
    }

    public static void showUpdates() {
        int input = getShowUpdateInput();

        if      (input == -1){
            start();
        }
        else if (input == 0) {
            for (String rssUrl : getSavedRss()){
                retrieveRssContent(rssUrl);
            }
        }
        else {
            showUpdatesOf(input-1);
        }
    }

    private static void showUpdatesOf(int input) {
        retrieveRssContent(getSavedRss().get(input));
    }

    public static void addURL() {
        String URL;

        System.out.println("Please enter website URL to add:");
        URL = s.next();

        if(getSavedUrls().contains(URL)){
            System.out.println(URL + " already exists.");
        }else {
            saveSummary (createWebSummary (URL));
            System.out.println("Added " + URL + " successfully.");
            start();
        }
    }
    public static void removeURL() {
        try {
            File data = new File("data.txt");
            System.out.println("Pleas enter website URL to remove.");
            Scanner scanner = new Scanner(System.in);
            String websiteURL = scanner.next();
            String validLine = extractPageTitle(fetchPageSource(websiteURL)) + ";" + websiteURL + "index.html" + ";" + extractRssUrl(websiteURL);
            int cnt = 0;
            int removeLine = -1;
            boolean check = false;
            BufferedReader reader = new BufferedReader(new FileReader(data));
            String[] line = new String[100];
            //check if doesn't exist
            while ((line[cnt] = reader.readLine()) != null) {
                if (line[cnt].equals(validLine)) {
                    check = true;
                    removeLine = cnt;
                }
                cnt++;
            }
            reader.close();
            if (check == false) {
                System.out.println("Couldn't find " + websiteURL);
                Home();
                return;
            }
            // remove address
            System.out.println(websiteURL + " Removed successfully");
            FileWriter writer = new FileWriter(data, false);
            writer.flush();
            writer.close();
            writer = new FileWriter(data, true);
            for (int i = 0; i < cnt; i++)
                if (i != removeLine)
                    writer.write(line[i] + "\n");
            writer.close();
        }
        catch (Exception ex) {
            System.out.println("failed to remove URL.\nMaybe there's problem with your connection or site address.");
        }
        Home();
    }
    public static String extractPageTitle(String html) {
        try {
            org.jsoup.nodes.Document doc = Jsoup.parse(html);
            return doc.select("title").first().text();
        }
        catch (Exception e)  {
            return "Error: no title tag found in page source!";
        }
    }
    public static String extractRssUrl(String url) throws IOException {
        org.jsoup.nodes.Document doc = Jsoup.connect(url).get();
        return doc.select("[type='application/rss+xml']").attr("abs:href");
    }
    public static String fetchPageSource(String urlString) throws Exception {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        URLConnection urlConnection = url.openConnection();
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML , like Gecko) Chrome/108.0.0.0 Safari/537.36");
        return toString(urlConnection.getInputStream());
    }
    private static String toString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream , "UTF-8"));
        String inputLine;
        StringBuilder stringBuilder = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null)
            stringBuilder.append(inputLine);
        return stringBuilder.toString();
    }
    public static void retrieveRssContent(String rssUrl, int MAX_ITEMS) {
        try {
            String rssXml = fetchPageSource(rssUrl);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            StringBuilder xmlStringBuilder = new StringBuilder();
            xmlStringBuilder.append(rssXml);
            ByteArrayInputStream input = new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(input);
            NodeList itemNodes = doc.getElementsByTagName("item");

            for (int i = 0; i < MAX_ITEMS; ++i) {
                Node itemNode = itemNodes.item(i);
                if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) itemNode;
                    System.out.println("Title: " + element.getElementsByTagName("title").item(0).getTextContent());
                    System.out.println("Link: " + element.getElementsByTagName("link").item(0).getTextContent());
                    System.out.println("Description: " + element.getElementsByTagName("description").item(0).
                            getTextContent());
                }
            }
        }
        catch (Exception e){
            System.out.println("Error in retrieving RSS content for " + rssUrl + ": " + e.getMessage());
        }
    }
}