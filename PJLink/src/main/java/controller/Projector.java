package controller;

/**
 * This class represents a projector with the necessary properties to communicate using the PJlink protocol.
 * @author Victor
 */

 import java.util.*;
 import java.net.Socket;
 import java.io.*;

 public final class Projector{
    private String ip;
    private char[] password;
    private int port = 4352;

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

    }

    public String switchInputVideo(){

    }

    public String switchInputStorage(){

    }

    public String switchInputDigital(){

    }

    public String switchInputNetwork(){

    }

    public String currentInput(){
        String answer = executeCommand("%INPT ?");
        return getFeedBack(answer);
    }

    public String muteVideo(){
        String answer = executeCommand("%1AVMT 11");
        return getFeedBack(answer);
    }

    public String unMuteVideo(){
        String answer = executeCommand("%1AVMT 10");
        return getFeedBack(answer);
    }

    public String muteAudio(){
        String answer = executeCommand("%1AVMT 21");
        return getFeedBack(answer);
    }

    public String unMuteAudio(){
        String answer = executeCommand("%1AVMT 20");
        return getFeedBack(answer);
    }

    public String muteVideoAndAudio(){
        String answer = executeCommand("%1AVMT 31");
        return getFeedBack(answer);
    }

    public String unMuteVideoAndAudio(){
        String answer = executeCommand("%1AVMT 30");
        return getFeedBack(answer);
    }

    public String muteStatus(){
        String answer = executeCommand("%1AVMT ?");
        return getFeedBack(answer);
    }

    public String errorReport(){
        String answer = executeCommand("%1ERST ?");
        String feedback = getFeedBack(answer);
        if (answer.equals(feedback)) {
            String status = answer.substring(6, answer.length());
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
        String feedBack = getFeedBack(answer);

        if (answer.equals(feedBack)){
            String[] status = answer.substring(6, answer.length()).split(" ");
            return Arrays.toString(status);
        }

        return feedBack;
    }

    public String requestProjectorName(){
        String answer = executeCommand("%1NAME ?");
        String feedBack = getFeedBack(answer);

        if (answer.equals(feedBack)){
            return answer.substring(6, answer.length());
        }

        return feedBack;
    }

    public String manufacturerInformation(){
        String answer = executeCommand("%1INF ?");
        String feedBack = getFeedBack(answer);

        if (answer.equals(feedBack)){
            return answer.substring(6, answer.length());
        }

        return feedBack;
    } 

    public String productNameInformation(){
        String answer = executeCommand("%1INF2 ?");
        String info = answer.substring(6, answer.length());
        return getFeedBack(info);
    }

    public String otherInformationQuery(){
        String answer = executeCommand("%1INFO ?");
        String info = answer.substring(6, answer.length());
        return getFeedBack(info);
    }

    public String getProjectorClass(){
        String answer = executeCommand("%1CLSS ?");
        String info = answer.substring(6, answer.length());
        return getFeedBack(info);
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

        return response;
    }

    private String executeCommand(String command){
        try {
            Socket clientSocket = new Socket(ip, port);
            OutputStream sender = clientSocket.getOutputStream();
            InputStream receiver = clientSocket.getInputStream();
            String projectorAnswer;
            System.out.println(new String(receiver.readAllBytes()));
            sender.write(command.getBytes());
            projectorAnswer = new String(receiver.readAllBytes());

            clientSocket.close();
            sender.close();
            receiver.close();

            return projectorAnswer;
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }





 }