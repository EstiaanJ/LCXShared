/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Murdock Grewar
 */
public class LCXDelegate {
    
    private static final String PROTOCOL_VERSION = "0.2";
    
    public static String hostName = "localhost";
    public static int portNumber = 2388;
    private Socket sock;
    private MessageHandler mailer;
    
    private String authToken;
    
    public LCXDelegate() {
        
        try {
            sock = new Socket();
            sock.connect(new InetSocketAddress(hostName,portNumber), 5000);
            mailer = new MessageHandler(sock.getInputStream(),sock.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(LCXDelegate.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //We start out without an authentication token.
        authToken = "";
    }
    
    public boolean login(String AccountID, String password) throws IOException {
        
        if (isLoggedIn())
            return false;
        
        mailer.send(new Message(MessageHeaders.LOGIN_REQUEST,PROTOCOL_VERSION,new String[]{AccountID, password},""));
        Message reply = mailer.receive();
        switch (reply.getHead()) {
            case AUTH_TOKEN_ISSUE:
                //Successfully logged in.
                authToken = reply.getData()[0];
                return true;
            case LOGIN_FAIL_RECEIPT:
                //Login failed somehow.
                return false;
        }
        authToken = reply.getData()[0];
        return true;
    }
    
    public boolean logout() throws IOException {
        
        if (!isLoggedIn())
            return false;
        
        mailer.send(new Message(MessageHeaders.LOGOUT_REQUEST,PROTOCOL_VERSION,new String[0],authToken));
        Message reply = mailer.receive();
        switch (reply.getHead()) {
            case LOGOUT_CONFIRMED:
                authToken = "";
                return true;
            default:
                return false;
        }
    }
    
    public double balance() throws IOException {
        
        if (!isLoggedIn())
            return -1;
        
        mailer.send(new Message(MessageHeaders.BALANCE_INQUIRY,PROTOCOL_VERSION,new String[0],authToken));
        Message reply = mailer.receive();
        
        switch (reply.getHead()) {
            case BALANCE_STATEMENT:
                return Double.parseDouble(reply.getData()[0]);
            default:
                return -1;
        }
    }
    
    /*
    public boolean transfer(String recipientAccountID, double amount) {
        
    }
    */
    
    private boolean isLoggedIn() {
        return (!authToken.equals(""));
    }
}
