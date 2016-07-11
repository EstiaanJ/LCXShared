/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

import java.io.Serializable;
/**
 *
 * @author Estiaan Janse Van Rensburg <https://github.com/EstiaanJ>
 */
public class Message implements Serializable
    {
    private final String messageHead;
    private final String protocolVersion;
    private final String[] data;
    private final String authToken;
    
    public Message (MessageHeaders inMessage, String protIn, String[] dataIn, String inAuthToken)
        {
        messageHead = inMessage.msg();
        protocolVersion = protIn;
        
        //Copy by VALUE. Bad compiler warning is bad. It's wrong. Its suggestion will copy by reference.
        data = new String[dataIn.length];
        for (int i=0; i < dataIn.length; i++)
            data[i] = dataIn[i];
        
        authToken = inAuthToken;
        }
    
    public MessageHeaders getHead()
        {
        return MessageHeaders.fromString(messageHead);
        }
    
    public String[] getData() {
        return data;
    }
    
    public String getProtocolVersion() {
        return protocolVersion;
    }
    
    public String getAuthToken()
        {
        return authToken;
        }
    }
