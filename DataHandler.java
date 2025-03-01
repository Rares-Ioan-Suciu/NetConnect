package com.netconnect.ChatRoom;

import org.json.simple.JSONObject;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataHandler implements Runnable {

    private final ChatUser user;

    public DataHandler(ChatUser user) {
        this.user = user;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(user.getDataInStream()));
             InputStream rawInputStream = user.getDataInStream()) {

            while (!user.isDataSocketClosed()) {

                handleImageTransfer(rawInputStream);
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Data handler for user " + user.getUsername() + " encountered an issue: " + e.getMessage());
        }
    }


    private void handleImageTransfer(InputStream rawInputStream) {
        try {
            String filename = generateTimestampedFileName("png");
            File outputFile = new File("uploads", filename);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }


            BufferedImage image = ImageIO.read(rawInputStream);
            if (image != null) {
                ImageIO.write(image, "png", outputFile);
                notifyImageReceived(outputFile.getAbsolutePath());
            } else {
                System.err.println("[ERROR] Failed to decode image data.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void notifyImageReceived(String filename) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("type", "command");
        jsonResponse.put("message", "The image '" + filename + "' has been successfully received.");
        jsonResponse.put("filename", filename);
        jsonResponse.put("time", new SimpleDateFormat("h:mm a").format(new Date()));

        try {
            user.getControlOutStream().println(jsonResponse.toJSONString());
            user.getControlOutStream().flush();
        } catch (Exception e) {
        }
    }

    public static String generateTimestampedFileName(String extension) {
        String timestamp = new SimpleDateFormat("HHmmss").format(new Date());
        return "received_image_" + timestamp + "." + extension;
    }
}
