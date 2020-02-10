package com.gusev.fx;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class AgentScanner {
    private Integer upType;
    private Integer downType;
    private Integer leftType;
    private Integer rightType;

    public Integer getDownType() {
        return downType;
    }

    public Integer getLeftType() {
        return leftType;
    }

    public Integer getRightType() {
        return rightType;
    }

    public Integer getUpType() {
        return upType;
    }

    public AgentScanner(String toparse){
        this.upType = getUp(toparse);
        this.downType = getDown(toparse);
        this.leftType = getLeft(toparse);
        this.rightType = getRight(toparse);
    }

    public AgentScanner(Integer upType, Integer downType, Integer leftType, Integer rightType){
        this.upType = upType;
        this.downType = downType;
        this.leftType = leftType;
        this.rightType = rightType;
    }

    public static AgentScanner getCurrentScan(String id, Integer port){
        try {
            URL obj = new URL("http://localhost:" + port + "/agents/" + id + "/scan");
            System.out.println("http://localhost:" + port + "/agents/" + id + "/scan");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) { // success
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                System.out.println(response.toString());
                return new AgentScanner(response.toString());
            } else {
                System.out.println("GET request not worked");
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(
                    ControlCenterController.class.getName()
            ).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(
                    ControlCenterController.class.getName()
            ).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static Integer getUp(String str){
        Pattern pattern = Pattern.compile(
                "(?<=\\<UP\\>)(\\s*.*\\s*)(?=\\<\\/UP\\>)");
        java.util.regex.Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String s = matcher.group();
            return Integer.parseInt(s.trim());
        }
        return 0;
    }

    private static Integer getDown(String str){
        Pattern pattern = Pattern.compile(
                "(?<=\\<DOWN\\>)(\\s*.*\\s*)(?=\\<\\/DOWN\\>)");
        java.util.regex.Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String s = matcher.group();
            return Integer.parseInt(s.trim());
        }
        return 0;
    }

    private static Integer getLeft(String str){
        Pattern pattern = Pattern.compile(
                "(?<=\\<LEFT\\>)(\\s*.*\\s*)(?=\\<\\/LEFT\\>)");
        java.util.regex.Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String s = matcher.group();
            return Integer.parseInt(s.trim());
        }
        return 0;
    }

    private static Integer getRight(String str){
        Pattern pattern = Pattern.compile(
                "(?<=\\<RIGHT\\>)(\\s*.*\\s*)(?=\\<\\/RIGHT\\>)");
        java.util.regex.Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String s = matcher.group();
            return Integer.parseInt(s.trim());
        }
        return 0;
    }
}
