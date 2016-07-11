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
    private final String data;
    private final String authToken;
    
    public Message (MessageHeaders inMessage, String dataIn, String inAuthToken)
        {
        messageHead = inMessage.msg();
        data = dataIn;
        authToken = inAuthToken;
        }
    
    public MessageHeaders getHead()
        {
        return MessageHeaders.fromString(messageHead);
        }
    
    public String getData() {
        return data;
    }
    
    public String getAuthToken()
        {
        return authToken;
        }
    }
