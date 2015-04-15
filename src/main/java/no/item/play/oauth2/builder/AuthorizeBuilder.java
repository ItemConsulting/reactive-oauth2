package no.item.play.oauth2.builder;

import org.apache.http.client.utils.URIBuilder;
import java.net.URISyntaxException;

public class AuthorizeBuilder {
    public final URIBuilder builder;

    public AuthorizeBuilder(String url) {
        try {
            builder = new URIBuilder(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static AuthorizeBuilder url(String url) {
        return new AuthorizeBuilder(url);
    }

    public AuthorizeBuilder clientId(String clientId){
        builder.setParameter("client_id", clientId);
        return this;
    }

    public AuthorizeBuilder redirectURI(String redirectURI){
        builder.setParameter("redirect_uri", redirectURI);
        return this;
    }

    public AuthorizeBuilder responseType(String responseType){
        builder.setParameter("response_type", responseType);
        return this;
    }

    public String build() {
        try{
            return builder.build().toASCIIString();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
