package no.item.play.oauth2.builder;

import no.item.play.oauth2.OAuthClient;
import no.item.play.oauth2.RefreshTokenHolder;
import no.item.play.oauth2.SimpleRefreshTokenHolder;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class OAuthClientBuilder {
    private String authURL;
    private String tokenURL;
    private String clientId;
    private String clientSecret;
    private String redirectURI;
    private Optional<RefreshTokenHolder> refreshTokenHolder;

    public OAuthClientBuilder(String authURL, String tokenURL){
        this.authURL = authURL;
        this.tokenURL = tokenURL;
    }
    public OAuthClientBuilder refreshTokenHolder(RefreshTokenHolder refreshTokenHolder){
        this.refreshTokenHolder = Optional.of(refreshTokenHolder);
        return this;
    }

    public OAuthClientBuilder clientId(String clientId){
        this.clientId = clientId;
        return this;
    }

    public OAuthClientBuilder clientSecret(String clientSecret){
        this.clientSecret = clientSecret;
        return this;
    }

    public OAuthClientBuilder redirectUri(String redirectURI){
        this.redirectURI = redirectURI;
        return this;
    }

    public OAuthClient build(){
        String[] fields = {authURL, tokenURL, clientId, clientSecret, redirectURI};
        boolean missingField = Arrays.stream(fields).anyMatch(Objects::isNull);

        if(missingField){
            throw new MissingRequirementException();
        } else {
            return new OAuthClient(authURL, tokenURL, clientId, clientSecret, redirectURI, refreshTokenHolder.orElse(new SimpleRefreshTokenHolder()));
        }
    }
}
