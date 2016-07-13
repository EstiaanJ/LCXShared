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
    private static final int DEFAULT_SOCKET_CONNECT_TIMEOUT = 300;
    private static final int DEFAULT_SOCKET_GENERAL_TIMEOUT = 200;
    
    private final int SOCKET_CONNECT_TIMEOUT;
    private final int SOCKET_GENERAL_TIMEOUT;
    
    public static String hostName = "lcx.ddns.net";
    public static int portNumber = 2388;
    private Socket sock;
    private MessageHandler mailer;
    
    private String authToken;

    public void dispose() throws CommunicationException, UnexpectedResponseException, NotLoggedInException {
        logout();
        endSession();
    }
    
    private void endSession() {
        try {
            if(!sock.isClosed()) {
                //sock.getInputStream().close();
                //sock.getOutputStream().close();
                mailer.send(new Message(MessageHeaders.CONNECTION_CLOSE,PROTOCOL_VERSION, new String[0],authToken));
                sock.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(LCXDelegate.class.getName()).log(Level.SEVERE, "Was unable to close a socket communicating with the Latinum server. Will continue without closing it.", ex);
            ex.printStackTrace();
        }
    }
    
    //Errors that can happen:
    public class UnexpectedResponseException extends Exception {

        public UnexpectedResponseException(String message) {
            super(message);
        }

        public UnexpectedResponseException(String message, Throwable throwable) {
            super(message, throwable);
        }
    }
    public class NotLoggedInException extends Exception {

        public NotLoggedInException(String message) {
            super(message);
        }

        public NotLoggedInException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }
    public class CommunicationException extends Exception {

        public CommunicationException(String message) {
            super(message);
        }

        public CommunicationException(String message, Throwable throwable) {
            super(message, throwable);
        }

    }
    

    public LCXDelegate(int timeout) throws CommunicationException {
        SOCKET_GENERAL_TIMEOUT = timeout;
        SOCKET_CONNECT_TIMEOUT = timeout;
        resetConnection();
    }
    
    public LCXDelegate() throws CommunicationException {
        SOCKET_GENERAL_TIMEOUT = DEFAULT_SOCKET_GENERAL_TIMEOUT;
        SOCKET_CONNECT_TIMEOUT = DEFAULT_SOCKET_CONNECT_TIMEOUT;
        resetConnection();
    }
    
    public boolean login(String AccountID, String password) throws CommunicationException, UnexpectedResponseException {
        
        if (isLoggedIn())
            return true;
        
        try {
            mailer.send(new Message(MessageHeaders.LOGIN_REQUEST,PROTOCOL_VERSION,new String[]{AccountID, password},""));
            Message reply = mailer.receive();
            switch (reply.getHead()) {
                case AUTH_TOKEN_ISSUE:
                    //Successfully logged in.
                    authToken = reply.getData()[0];
                    return true;
                case LOGIN_FAIL_RECEIPT:
                    //Login failed somehow. Probably invalid credentials.
                    return false;
                default:
                    throw new UnexpectedResponseException("Unable to interpret server's response.");
            }
        } catch (IOException e) {
            throw new CommunicationException("A problem occured when trying to communicate with the server.");
        }
    }
    
    public void logout() throws CommunicationException, UnexpectedResponseException, NotLoggedInException {
        
        if (!isLoggedIn())
            return;
        try {
            mailer.send(new Message(MessageHeaders.LOGOUT_REQUEST,PROTOCOL_VERSION,new String[0],authToken));
            Message reply = mailer.receive();
            switch (reply.getHead()) {
                case LOGOUT_CONFIRMED:
                    authToken = "";
                    return;
                    
                //THIS CODE SHOULD BE UNREACHABLE.
                //isLoggedIn() method should clear the token if we are not logged in, and so the server should not give this reply.
                case SESSION_EXPIRED_NOTIFY:
                    assert(false);
                    throw new NotLoggedInException("The server says I am not logged in.");
                ///////////////////
                    
                default:
                    throw new UnexpectedResponseException("Could not interpret server's response to the logout request.");
            }
        }
        catch (IOException e) {
            throw new CommunicationException("A problem arose when communicating with the server.",e);
        }
    }
    
    public String balance() throws CommunicationException, NotLoggedInException, UnexpectedResponseException {
        
        if (!isLoggedIn())
            throw new NotLoggedInException("The client is not logged in to the server.");
        
        try {
            mailer.send(new Message(MessageHeaders.BALANCE_INQUIRY,PROTOCOL_VERSION,new String[0],authToken));
            Message reply = mailer.receive();

            switch (reply.getHead()) {
                case BALANCE_STATEMENT:
                    return reply.getData()[0];
                    
                //THIS CODE SHOULD BE UNREACHABLE.
                //isLoggedIn() method should clear the token if we are not logged in, and so the server should not give this reply.
                case SESSION_EXPIRED_NOTIFY:
                    assert(false);
                    throw new NotLoggedInException("The server says I am not logged in.");
                ///////////////////
                    
                default:
                    throw new UnexpectedResponseException("Unable to interpret the server's response to the balance inquiry.");
            }
        } catch (IOException e) {
            throw new CommunicationException("A problem occured when trying to communicate with the server.",e);
        }
    }
    
    public boolean transfer(String recipientAccountID, String amount) throws CommunicationException, UnexpectedResponseException, NotLoggedInException {
        if (!isLoggedIn())
            throw new NotLoggedInException("The client is not logged in to the server.");
        
        try {
            mailer.send(new Message(MessageHeaders.TRANSFER_REQUEST,PROTOCOL_VERSION,new String[]{recipientAccountID,amount},authToken));
            Message reply = mailer.receive();

            switch (reply.getHead()) {
                case TRANSFER_RECEIPT_FAIL:
                    return false;
                case TRANSFER_RECEIPT_SUCCESS:
                    return true;
                    
                //THIS CODE SHOULD BE UNREACHABLE.
                //isLoggedIn() method should clear the token if we are not logged in, and so the server should not give this reply.
                case SESSION_EXPIRED_NOTIFY:
                    assert(false);
                    throw new NotLoggedInException("The server says I am not logged in.");
                ///////////////////
                    
                default:
                    throw new UnexpectedResponseException("Unable to interpret the server's response to the balance inquiry.");
            }
        } catch (IOException e) {
            throw new CommunicationException("A problem occured when trying to communicate with the server.",e);
        }
    }
    
    //Note that this method will not only check if we are logged in, but also contact the server again if we have lost connection.
    public boolean isLoggedIn() throws CommunicationException, UnexpectedResponseException {
        if(authToken.equals("")) {
            return false;
        } else {
            try {
                mailer.send(new Message(MessageHeaders.PING,PROTOCOL_VERSION,new String[0],authToken));
                Message reply = mailer.receive();
                switch (reply.getHead()) {
                    case SESSION_EXPIRED_NOTIFY:
                    System.out.println(
                            "Server indicated that it does recognise this LCXDelegate's authentication token. "
                                    + "The authentication token has hence been reset to none.");
                    authToken = "";
                    return false;

                    case PONG:
                        return true;
                    default:
                        //This should not happen.
                        throw new UnexpectedResponseException(
                        "Unexpected response from PING to server."
                        );

                }
            } catch (IOException e) {
                resetConnection();
                return false;
            }
        }
    }
    
    private void resetConnection() throws CommunicationException {
        try {
            sock = new Socket();
            sock.connect(new InetSocketAddress(hostName,portNumber), SOCKET_CONNECT_TIMEOUT);
            sock.setSoTimeout(SOCKET_GENERAL_TIMEOUT);
            mailer = new MessageHandler(sock.getInputStream(),sock.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(LCXDelegate.class.getName()).log(Level.SEVERE, null, ex);
            throw new CommunicationException("Could not initiae contact with the server.");
        }
        
        //We start out without an authentication token.
        authToken = "";
    }
    public String newAccount(String name, String password) throws UnexpectedResponseException, CommunicationException {
        
        try {
            mailer.send(new Message(MessageHeaders.NEW_ACCOUNT_REQUEST,PROTOCOL_VERSION,new String[]{name,password},""));
            Message reply = mailer.receive();

            switch (reply.getHead()) {
                case NEW_ACCOUNT_RECEIPT:
                    return reply.getData()[0];
                default:
                    throw new UnexpectedResponseException("Unable to interpret the server's response to the balance inquiry.");
            }
        } catch (IOException e) {
            throw new CommunicationException("A problem occured when trying to communicate with the server.",e);
        }
    }
}
