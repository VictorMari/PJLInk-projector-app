package controller;

/**
 * This class represents a projector with the necessary properties to communicate using the PJlink protocol.
 * @author Victor
 */

 import java.util.*;
 import java.net.Socket;
 import java.io.*;
 import java.security.*;

 public final class Projector{
    private String ip;
    private String password;
    private int port = 4352;
    private Socket projectorSocket;

    //disable defaul constructor
    private Projector(){}

    public Projector(String ip){
        this.ip = ip;
    }

    public void setIp(String ip){
        this.ip = ip;
    }

    public void setPort(int port){
        this.port = port;
    }

    public String turnProjectorOff(){
        String answer =  executeCommand("%POWR1");
        return getFeedBack(answer);
    }

    public String turnProjectorOn(){
        String answer =  executeCommand("%POWR0");
        return getFeedBack(answer);
    }

    public String getPowerStatus(){
        String answer = executeCommand("%1POWR ?");
        return getFeedBack(answer);
    }

    public String switchInputRGB(){
        return null;
    }

    public String switchInputVideo(){
        return null;
    }

    public String switchInputStorage(){
        return null;
    }

    public String switchInputDigital(){
        return null;
    }

    public String switchInputNetwork(){
        return null;
    }

    public String currentInput(){
        String answer = executeCommand("%INPT ?");
        return getParam(answer);

    }

    public String muteVideo(){
        String answer = executeCommand("%1AVMT 11");
        return getFeedBack(answer);
    }

    public String unMuteVideo(){
        String answer = executeCommand("%1AVMT 10");
        return getParam(answer);
    }

    public String muteAudio(){
        String answer = executeCommand("%1AVMT 21");
        return getParam(answer);
    }

    public String unMuteAudio(){
        String answer = executeCommand("%1AVMT 20");
        return getParam(answer);
    }

    public String muteVideoAndAudio(){
        String answer = executeCommand("%1AVMT 31");
        return getParam(answer);
    }

    public String unMuteVideoAndAudio(){
        String answer = executeCommand("%1AVMT 30");
        return getParam(answer);
    }

    public String muteStatus(){
        String answer = executeCommand("%1AVMT ?");
        return getParam(answer);
    }

    public String errorReport(){
        String answer = executeCommand("%1ERST ?");
        String feedback = getFeedBack(answer);
        if (answer.equals(feedback)) {
            String status = answer.substring(7, answer.length());
            String[] items = {"Fan", "Lamp", "Temperature", "Cover open", "Filter", "Other"};
            String[] errors = {"No error detected or error detecting function found", "Warning", "Error found"};
            StringBuilder report = new StringBuilder();
            for (int i = 0; i < status.length(); i++) {
                int errorNumber = status.charAt(i);
                report.append(items[i] + ": " + errors[errorNumber]);
            }
            return report.toString();  
        }
        return feedback;
    }

    public String lampInformation(){
        String answer = executeCommand("%1LAMP ?");
        String feedBack = getFeedBack(answer);
        if (answer.equals(feedBack)) {
            StringBuilder report = new StringBuilder();
            String[] status = answer.substring(6, answer.length()).split(" ");
            for (int i = 0; i < status.length-1; i += 2) {
                int lampNumber = i++;
                boolean isOff = Integer.parseInt(status[lampNumber]) == 0;
                int cumulativeLighting = Integer.parseInt(status[i]);
                report.append("Lamp number: " + lampNumber + " is " + ((isOff) ? "Off" : "On") + " Cumulative lightning " + cumulativeLighting);
            }

            return report.toString();
        }
        return feedBack;
    }

    public String getInputSources(){
        String answer = executeCommand("%1INST ?");
        String[] feedBack = getParams(answer);

        return Arrays.toString(feedBack);
    }

    public String requestProjectorName(){
        String answer = executeCommand("%1NAME ?");
        return getParam(answer);
    }

    public String manufacturerInformation(){
        String answer = executeCommand("%1INF ?");
        return getParam(answer);
    } 

    public String productNameInformation(){
        String answer = executeCommand("%1INF2 ?");
        return getParam(answer);
    }

    public String otherInformationQuery(){
        String answer = executeCommand("%1INFO ?");
        return getParam(answer);
    }

    public String getProjectorClass(){
        String answer = executeCommand("%1CLSS ?");
        return getParam(answer);
    }

    private String getFeedBack(String response){
        if (response.contains("OK")) {
            return "success";
        }

        if (response.contains("ERR1")) {
            return "Undefine Command";
        }

        if (response.contains("ERR2")) {
            return "Out of parameter";
        }

        if (response.contains("ERR3")) {
            return "Un available time";
        }

        if (response.contains("ERR4")) {
            return "Projector/Display failure";
        }

        return "";
    }

    private String executeCommand(String command){
        try {
            projectorSocket = new Socket(ip, port);
            OutputStream sender = projectorSocket.getOutputStream();
            InputStream receiver = projectorSocket.getInputStream();
            
            String answer = new String(receiver.readAllBytes());
            String[] parameters = getParams(answer);
            
            if (parameters[0].equals("1")) {
                byte[] hash = getMD5(parameters[1]);
                byte[] commandBytes = command.getBytes();
                byte[] authCommand = new byte[hash.length +commandBytes.length];
                
                for (int i = 0; i < authCommand.length; i++) {
                    int commandBytesIndex = i + commandBytes.length;
                    authCommand[i] = hash[i];
                    authCommand[commandBytesIndex] = commandBytes[i];
                }

                sender.write(authCommand);
            }
            else {
                sender.write(command.getBytes());
            }
            String response = new String(receiver.readAllBytes());
            projectorSocket.close();
            sender.close();
            receiver.close();

            return response;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private byte[] getMD5(String number){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            String d = number.concat(password);
            return md5.digest(d.getBytes("UTF-8"));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new byte[0];
      }

    private String[] getParams(String command) {
        return command
                .substring(7, command.length())
                .trim()
                .split(" ");
    }

    private String getParam(String command){
        return command
                .substring(7, command.length())
                .trim();
    }
 }