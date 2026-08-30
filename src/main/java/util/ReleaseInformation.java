package util;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ReleaseInformation {
        public String versionId;
        public String releasePath;

    /**
     * Does a very basic HTTP GET request to the Github API to check the most recent version.
     * https://docs.github.com/en/rest/releases/releases?apiVersion=2022-11-28#get-the-latest-release
     */
    public static ReleaseInformation get() {
        final String apiPath = "https://api.github.com/repos/lushen124/Universal-FE-randomizer/releases/latest";
        try {
            URL url = new URL(apiPath);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer content = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();
            Map response = new Gson().fromJson(content.toString(), Map.class);
            ReleaseInformation releaseInformation = new ReleaseInformation();
            releaseInformation.releasePath = String.valueOf(response.get("html_url"));
            releaseInformation.versionId = String.valueOf(response.get("tag_name"));
            return releaseInformation;
        } catch (Exception e) {
            System.out.println("failed to get most recent release");
        }
        return null;
    }
}
