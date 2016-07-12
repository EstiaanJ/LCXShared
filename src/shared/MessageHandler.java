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
 * @author Murdock Grewar <https://github.com/Myridium>
 */
public class MessageHandler {
    
    private final ObjectInputStream messageIn;
    private final ObjectOutputStream messageOut;
    
    public MessageHandler(InputStream inStream, OutputStream outStream) throws IOException {
        messageOut = new ObjectOutputStream(outStream);
        messageIn = new ObjectInputStream(inStream);
    }
    
    public void send(Message msg) throws IOException {
        messageOut.writeObject(msg);
        messageOut.reset();
        messageOut.flush();
    }
    
    public Message receive() throws IOException {
        try {
            return (Message) messageIn.readObject();
        } catch (ClassNotFoundException ex) {
            System.err.println("ClassNotFoundException when decoding an object from the socket stream.");
            assert false;
            return null;
        }
    }
    
}
