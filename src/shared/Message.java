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
    private final String commandMessage;
    private final String authToken;
    
    public Message (CommonMessages inMessage, String inAuthToken)
        {
        commandMessage = inMessage.msg();
        authToken = inAuthToken;
        }
    
    public CommonMessages getCommonMessage()
        {
        return CommonMessages.fromString(commandMessage);
        }
    
    public String getAuthToken()
        {
        return authToken;
        }
    }
