/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 *
 * @author Murdock
 */
public class MessageHandler {
    
    ObjectInputStream messageIn;
    ObjectOutputStream messageOut;
    
    public MessageHandler(InputStream inStream, OutputStream outStream) throws IOException {
        messageIn = new ObjectInputStream(inStream);
        messageOut = new ObjectOutputStream(outStream);
    }
    
    public void send(Message msg) throws IOException {
        messageOut.writeObject(msg);
        messageOut.reset();
        messageOut.flush();
    }
    
    public Message receive() throws IOException, ClassNotFoundException {
        return (Message) messageIn.readObject();
    }
    
}
