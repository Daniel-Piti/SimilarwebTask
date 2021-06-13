package automationTask;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.Calendar;

public class ChromeVersion {
    public String versionId;
    public String versionType;
    public String versionPlatform;
    public Calendar versionTime;

    public ChromeVersion(String json, String platform){
        JSONParser jsonParser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) jsonParser.parse(json);
            this.versionId       = jsonObject.get("version").toString();
            this.versionType     = jsonObject.get("channel").toString();
            this.versionPlatform = platform;
            this.versionTime     = Calendar.getInstance();
            this.versionTime.setTimeInMillis(Long.parseLong(jsonObject.get("time").toString()));
            this.versionTime.set(Calendar.HOUR_OF_DAY, 0); // For more accurate calculation later
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return String.format("%s: %s - %s.", versionPlatform, versionType, versionId);
    }
}
