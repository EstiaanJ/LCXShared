/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shared;

/**
 *
 * @author Estiaan Janse Van Rensburg <https://github.com/EstiaanJ>
 */
public enum MessageHeaders
    {
    VERSION_REQUEST("VERSION"), //Find a better enum for this...
    NEW_SESSION_REQUEST("New Session"),
    NEW_SESSION_ACKNOWLEDGE("New Session Granted"),

    NEW_USID_REQUEST("New USID"),
    NEW_ACCOUNT_REQUEST("I'd like a new account"),
    NEW_ACCOUNT_RECEIPT("Account created. Here is your account number."),

    UPDATE_REQUEST("Update"),

    NEW_USER_REQUEST("New User"),
    NEW_USER_ACKNOWLEDGE("New User Ready"),

    RECEIPT_ACCOUNT_NUMBER("Account Number Recieved"),
    RECEIPT_ACCOUNT_NAME("Name Recieved"),
    RECEIPT_ACCOUNT_PASSWORD("Password Recieved"),

    TRANSFER_REQUEST("Transfer"),
    TRANSFER_RECEIPT_SUCCESS("Transfer success"),
    TRANSFER_RECEIPT_FAIL("Transfer fail"),

    CONNECTION_CLOSE("Closing socket now"),

    LOGIN_REQUEST("Login Request"),
    LOGOUT_REQUEST("Logout Request"),
    LOGOUT_CONFIRMED("Logout has taken place."),
    LOGIN_AWAITING_ACCOUNT_NUMBER("Login Ready"),
    LOGIN_AWAITING_PASSWORD("Ready for password"),
    LOGIN_PREPARE_FOR_DETAILS("Login Succesful"),
    LOGIN_FAIL_RECEIPT("Login Unsuccesful"),
    LOGIN_COMPLETE_RECEIPT("Login Done"),

    //ERROR_GENERIC("SERVER ERROR"),
    NO_MESSAGE("NO_MESSAGE"),
    
    VERSION_MESSAGE_START("VERSION:"),
    
    AUTH_TOKEN_ISSUE("Here is your new authentication token."),
    SESSION_EXPIRED_NOTIFY("Your authentication token is not valid. The session probably expired."),
    BALANCE_STATEMENT("Here is your balance statement."),
    BALANCE_INQUIRY("What is my balance, Mr. Wolf?"),
    
    PING("I'm pinging you."),
    PONG("I'm replying to your ping.");
    
    private String msg;
    
    private MessageHeaders(String msg) {
            this.msg = msg;
        }
    
    public static MessageHeaders fromString(String text) {
            if (text != null) {
                for (MessageHeaders b : MessageHeaders.values()) {
                    if (text.equals(b.msg())) {
                        return b;
                    }
                }
            }
            return null;
        }

        public String msg() {
            return msg;
        }
        
    public boolean equals(String inString)
        {
        boolean isEqual = false;
        if(inString.equals(msg))
            {
            isEqual = true;
            }
        return isEqual;
        }
    }
