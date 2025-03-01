package com.netconnect;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import java.util.Random;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class GoogleLoginHelper {

    private static final String CLIENT_SECRET_FILE = "src/main/java/resources/client_secret.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = List.of(
            "https://www.googleapis.com/auth/user.birthday.read",
            "https://www.googleapis.com/auth/user.gender.read",
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile"
    );

    private static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";
    private static final List<String> SCOPES_LOGIN = List.of(
            "https://www.googleapis.com/auth/userinfo.email",
            "https://www.googleapis.com/auth/userinfo.profile"
    );

    private static GoogleClientSecrets loadClientSecrets() throws Exception {
        Path path = Paths.get(CLIENT_SECRET_FILE);
        System.out.println("Loading client secrets from file: " + path.toAbsolutePath());
        return GoogleClientSecrets.load(JSON_FACTORY, new FileReader(path.toFile()));
    }

    private static GoogleUser fetchGoogleUser(String accessToken) throws Exception {
        System.out.println("Fetching Google user info with access token...");

        String url = "https://people.googleapis.com/v1/people/me?personFields=names,emailAddresses,genders,birthdays,photos";
        com.google.api.client.http.HttpRequest request = new NetHttpTransport()
                .createRequestFactory()
                .buildGetRequest(new com.google.api.client.http.GenericUrl(url))
                .setHeaders(new com.google.api.client.http.HttpHeaders().setAuthorization("Bearer " + accessToken));

        com.google.api.client.http.HttpResponse response = request.execute();

        System.out.println("User info response received.");
        String jsonResponse = response.parseAsString();
        System.out.println("Raw response: " + jsonResponse);

        com.google.gson.JsonObject jsonObject = new com.google.gson.JsonParser().parse(jsonResponse).getAsJsonObject();

        GoogleUser user = new GoogleUser();

        if (jsonObject.has("names")) {
            com.google.gson.JsonArray names = jsonObject.getAsJsonArray("names");
            if (names.size() > 0) {
                user.setName(names.get(0).getAsJsonObject().get("displayName").getAsString());
            }
        }

        Random random = new Random();
        user.setUsername(user.getName().split("\\s+")[0]+"_"+user.getName().split("\\s+")[1]+random.nextInt(10000));

        if (jsonObject.has("emailAddresses")) {
            com.google.gson.JsonArray emailAddresses = jsonObject.getAsJsonArray("emailAddresses");
            if (emailAddresses.size() > 0) {
                user.setEmail(emailAddresses.get(0).getAsJsonObject().get("value").getAsString());
            }
        }

        if (jsonObject.has("genders")) {
            com.google.gson.JsonArray genders = jsonObject.getAsJsonArray("genders");
            if (genders.size() > 0) {
                user.setGender(genders.get(0).getAsJsonObject().get("value").getAsString());
            }
        }

        if (jsonObject.has("birthdays")) {
            com.google.gson.JsonArray birthdaysArray = jsonObject.getAsJsonArray("birthdays");
            if (birthdaysArray.size() > 0) {
                com.google.gson.JsonObject birthdayObject = birthdaysArray.get(0).getAsJsonObject();
                if (birthdayObject.has("date")) {
                    com.google.gson.JsonObject dateObject = birthdayObject.getAsJsonObject("date");

                    int year = dateObject.has("year") ? dateObject.get("year").getAsInt() : 0;
                    int month = dateObject.has("month") ? dateObject.get("month").getAsInt() : 0;
                    int day = dateObject.has("day") ? dateObject.get("day").getAsInt() : 0;

                    if (year > 0 && month > 0 && day > 0) {
                        user.setBirthday(String.format("%04d-%02d-%02d", year, month, day));
                    }
                }
            }
        }

        if (jsonObject.has("photos")) {
            com.google.gson.JsonArray photos = jsonObject.getAsJsonArray("photos");
            if (photos.size() > 0) {
                String profilePictureUrl = photos.get(0).getAsJsonObject().get("url").getAsString();
                String photoPath = "/home/rares655/Sem_3/P3/NetConnect/Users/"+user.getUsername()+"_profile_picture.jpg";
                user.setProfilePicture(photoPath);
                downloadPhoto(profilePictureUrl, photoPath);
            }
        }

        return user;
    }

    private static GoogleUser fetchMinimalGoogleUser(String accessToken) throws Exception {
        System.out.println("Fetching Google user info with access token...");

        String url = "https://people.googleapis.com/v1/people/me?personFields=names,emailAddresses";
        com.google.api.client.http.HttpRequest request = new NetHttpTransport()
                .createRequestFactory()
                .buildGetRequest(new com.google.api.client.http.GenericUrl(url))
                .setHeaders(new com.google.api.client.http.HttpHeaders().setAuthorization("Bearer " + accessToken));

        com.google.api.client.http.HttpResponse response = request.execute();

        System.out.println("User info response received.");
        String jsonResponse = response.parseAsString();
        System.out.println("Raw response: " + jsonResponse);

        com.google.gson.JsonObject jsonObject = new com.google.gson.JsonParser().parse(jsonResponse).getAsJsonObject();

        GoogleUser user = new GoogleUser();

        if (jsonObject.has("names")) {
            com.google.gson.JsonArray names = jsonObject.getAsJsonArray("names");
            if (names.size() > 0) {
                user.setName(names.get(0).getAsJsonObject().get("displayName").getAsString());
            }
        }


        if (jsonObject.has("emailAddresses")) {
            com.google.gson.JsonArray emailAddresses = jsonObject.getAsJsonArray("emailAddresses");
            if (emailAddresses.size() > 0) {
                user.setEmail(emailAddresses.get(0).getAsJsonObject().get("value").getAsString());
            }
        }
        return user;
    }




    public static class GoogleUser {
        private String name;
        private String email;
        private String gender;
        private String birthday;
        private String profilePicture;
        private String username;
        private int banned;

        public int getBanned()
        {
            return banned;
        }

        public void setBanned(int banned)
        {
            this.banned = banned;
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getGender() {
            return gender;
        }
        public void setGender(String gender) {
            if(gender.equalsIgnoreCase("male"))
                this.gender = "man";
            else if(gender.equalsIgnoreCase("female"))
                this.gender = "woman";
            else if(gender.equalsIgnoreCase("non-binary"))
                this.gender = "non-binary";
            else
                this.gender = "other";
        }

        public String getBirthday() {
            return birthday;
        }
        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }
        public String getProfilePicture() {
            return profilePicture;
        }
        public void setProfilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
        }

        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }

        @Override
        public String toString() {
            return "GoogleUser{name='" + name + "', email='" + email + "', gender='" + gender + "', birthday='" + birthday + "', profilePicture='" + profilePicture + "'}";
        }
    }

    public static void downloadPhoto(String photoUrl, String destinationPath) throws Exception {
        System.out.println("Downloading photo from: " + photoUrl);
        URL url = new URL(photoUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true);

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            String contentType = connection.getContentType();

            if (contentType.startsWith("image")) {
                try (InputStream inputStream = connection.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(destinationPath)) {

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }

                    System.out.println("Photo downloaded successfully to: " + destinationPath);
                }
            } else {
                throw new Exception("Invalid content type. Expected an image but received: " + contentType);
            }
        } else {
            throw new Exception("Failed to download photo. HTTP response code: " + connection.getResponseCode());
        }
    }

    public static String getAuthorizationUrl() throws Exception {
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    }
    public static String getMinimalAuthorizationUrl() throws Exception {
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY, clientSecrets, SCOPES_LOGIN)
                .setAccessType("offline")
                .build();

        return flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
    }

    public static GoogleUser authenticateWithCode(String authorizationCode) throws Exception {
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setAccessType("offline")
                .build();

        GoogleTokenResponse tokenResponse = flow.newTokenRequest(authorizationCode).setRedirectUri(REDIRECT_URI).execute();
        Credential credential = flow.createAndStoreCredential(tokenResponse, null);

        return fetchGoogleUser(credential.getAccessToken());
    }
    public static GoogleUser authenticateWithMinimalCode(String authorizationCode) throws Exception {
        GoogleClientSecrets clientSecrets = loadClientSecrets();

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), JSON_FACTORY, clientSecrets, SCOPES_LOGIN)
                .setAccessType("offline")
                .build();

        GoogleTokenResponse tokenResponse = flow.newTokenRequest(authorizationCode).setRedirectUri(REDIRECT_URI).execute();
        Credential credential = flow.createAndStoreCredential(tokenResponse, null);

        return fetchMinimalGoogleUser(credential.getAccessToken());
    }


}