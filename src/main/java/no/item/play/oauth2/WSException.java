package no.item.play.oauth2;

import play.libs.ws.WSResponse;

public class WSException extends RuntimeException {
    public final Integer code;
    public final String message;

    public WSException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;

    }

    public WSException(WSResponse response){
        this.code = response.getStatus();
        this.message = response.getBody();
    }
}
