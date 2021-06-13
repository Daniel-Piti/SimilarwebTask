package automationTask;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.net.URL;
import java.net.URLConnection;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ChromiumNotifier {
    // Week frame time
    private final static int MAX_DAYS_DIFF = 7;
    // Amount of versions to fetch
    private final static int AMOUNT_OF_VERSIONS = 1;
    private final static String[] versionTypes = {"Stable", "Beta", "Dev"};
    private static final Set<String> validPlatforms = new HashSet<>(Arrays.asList("Windows", "Linux"));
    private static final Set<String> versionTypesForMailCheck = new HashSet<>(Arrays.asList("Stable", "Beta"));
    private final Calendar currentTime;

    public ChromiumNotifier(){
        this.currentTime = Calendar.getInstance();
        this.currentTime.set(Calendar.HOUR_OF_DAY, 5); // Set HOURS for more accurate calculate later
    }

    public void run(ArrayList<String> platforms, MailSender sender) {
        ArrayList<String> mailsMessages = new ArrayList<>();

        ArrayList<ChromeVersion> versions = getAllVersionsWithFetch(platforms);

        if (versions == null){
            System.out.println("Something went wrong with fetching the data");
            return;
        }
        System.out.println("Latest versions:");
        for (ChromeVersion version:versions){
            System.out.println(version.toString());

            //printPlatformTitle(platform);
            if(shouldSendMail(version))
                mailsMessages.add(createMessageForMailMessages(version));
        }
        if(!mailsMessages.isEmpty())         // Check if any version was added
            sender.sendMail(mailsMessages);
    }

    public boolean shouldSendMail(ChromeVersion version){
        return versionTypesForMailCheck.contains(version.versionType) &&
                getDaysDiffFromNow(version.versionTime) <= MAX_DAYS_DIFF;
    }

    public String createMessageForMailMessages(ChromeVersion version){
        String message = String.format("%s %s version: %s was released ",
                version.versionPlatform,
                version.versionType.toLowerCase(Locale.ROOT),
                version.versionId);
        long days_diff = getDaysDiffFromNow(version.versionTime);
        if(days_diff == 0) {
            message += "today.";
        } else {
            message = String.format("%s%s days ago.", message, days_diff);
        }
        return message;
    }

    public long getDaysDiffFromNow(Calendar versionDate){
        return Math.abs(ChronoUnit.DAYS.between(versionDate.toInstant(), currentTime.toInstant()));
    }

    private ArrayList<ChromeVersion> getAllVersionsWithFetch(ArrayList<String> platforms) {
        JSONParser jsonParser = new JSONParser();
        ArrayList<ChromeVersion> versions = new ArrayList<>();

        for(String platform: platforms){            //windows -> Linux
            if (!validPlatforms.contains(platform)) {
                System.out.println("Invalid platform name, try Windows or Linux.");
                continue;
            }
            for (String versionType : versionTypes){ // Stable -> Beta -> Dev
                try {
                    String url = String.format(
                            "https://chromiumdash.appspot.com/fetch_releases?channel=%s&platform=%s&num=%d&offset=0",
                            versionType, platform, AMOUNT_OF_VERSIONS);
                    URLConnection connection =  new URL(url).openConnection();
                    Scanner scanner = new Scanner(connection.getInputStream());
                    String content = scanner.useDelimiter("\\Z").next();         // "\\Z" End of string
                    scanner.close();
                    JSONArray jsonArray = (JSONArray) jsonParser.parse(content); // save as json array
                    if (jsonArray == null) {
                        System.out.printf("Could not fetch data for %s skipping version%n", versionType);
                        continue;
                    }
                    for (int j = 0; j < AMOUNT_OF_VERSIONS; j++) {
                        versions.add(new ChromeVersion(jsonArray.get(j).toString(), platform));
                    }
                }catch ( Exception ex ) {
                    ex.printStackTrace();
                    return null;
                }
            }
        }
        return versions;
    }

}