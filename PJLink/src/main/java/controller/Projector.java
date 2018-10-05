package controller;

/**
 * This class represents a projector with the necessary properties to communicate using the PJlink protocol.
 * @author Victor
 */

 import java.util.*;
 import java.net.Socket;
import java.net.SocketException;
import java.io.*;
import java.math.BigInteger;
import java.security.*;

 public final class Projector{
    private String ip;
    private String password = "JBMIAProjectorLink";
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
        return executeCommand("%1POWR 0\r");
        
    }

    public String turnProjectorOn(){
        return  executeCommand("%1POWR 1\r");
    }

    public String getPowerStatus(){
        return executeCommand("%1POWR ?\r");
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
        return executeCommand("%1INPT ?\r");

    }

    public String muteVideo(){
        return executeCommand("%1AVMT 11\r");
    }

    public String unMuteVideo(){
        return executeCommand("%1AVMT 10\r");
    }

    public String muteAudio(){
        return executeCommand("%1AVMT 21\r");
    }

    public String unMuteAudio(){
        return executeCommand("%1AVMT 20\r");
    }

    public String muteVideoAndAudio(){
        return executeCommand("%1AVMT 31\r");
    }

    public String unMuteVideoAndAudio(){
        return executeCommand("%1AVMT 30\r");
    }

    public String muteStatus(){
        return executeCommand("%1AVMT ?\r");
    }

    public String errorReport(){
        return executeCommand("%1ERST ?\r");
    }

    public String lampInformation(){
        return executeCommand("%1LAMP ?\r");
    }

    public String getInputSources(){
        return executeCommand("%1INST ?\r");
    }

    public String requestProjectorName(){
        return executeCommand("%1NAME ?\r");
    }

    public String manufacturerInformation(){
        return executeCommand("%1INF1 ?\r");
    } 

    public String productNameInformation(){
        return executeCommand("%1INF2 ?\r");
    }

    public String otherInformationQuery(){
        return executeCommand("%1INFO ?\r");
    }

    public String getProjectorClass(){
        return executeCommand("%1CLSS ?\r");
    }

    private String executeCommand(String command){
        try {
            Socket projectorSocket = new Socket(ip, port);
            OutputStream sender = projectorSocket.getOutputStream();
            InputStream receiver = projectorSocket.getInputStream();
            
            byte[] bytes = new byte[32];//stores the response from the projector
            receiver.read(bytes);//reads the initial response
            String response =  new String(bytes);
            String[] responseParameters = getParams(response);
            Arrays.fill(bytes, (byte)0);
            if (responseParameters[0].equals("1")) {
                byte[] md5Hash = getMD5(responseParameters[1]);
                byte[] commandBytes = command.getBytes();
                byte[] combinedBytes = new byte[md5Hash.length + commandBytes.length];
                System.arraycopy(md5Hash, 0, combinedBytes, 0, md5Hash.length);
                System.arraycopy(commandBytes, 0, combinedBytes, md5Hash.length, commandBytes.length);

                sender.write(combinedBytes);
                receiver.read(bytes);
                response =  new String(bytes);
            }
            else {
                sender.write(command.getBytes());
                receiver.read(bytes);
                response = new String(bytes);
            }

            projectorSocket.close();
            sender.close();
            receiver.close();

            return response;
        }
        catch(SocketException e){
            return executeCommand(command);
        }
        catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        
    }

    private byte[] getMD5(String number){
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(number.getBytes());
            md5.update(password.getBytes());
            byte[] digest = md5.digest();
            BigInteger digitHash = new BigInteger(1, digest);
            return digitHash.toString(16).getBytes();
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