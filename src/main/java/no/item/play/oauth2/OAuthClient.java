package no.item.play.oauth2;

import com.fasterxml.jackson.databind.JsonNode;
import play.Logger;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import play.mvc.Http;
import no.item.play.oauth2.builder.AuthorizeBuilder;
import no.item.play.oauth2.builder.OAuthClientBuilder;

public class OAuthClient implements Http.HeaderNames {
    private final String authURL;
    private final String tokenURL;
    private final String clientId;
    private final String clientSecret;
    private final String redirectURI;
    private final RefreshTokenHolder refreshTokenHolder;

    public OAuthClient(String authURL, String tokenURL, String clientId, String clientSecret, String redirectURI, RefreshTokenHolder refreshTokenHolder){
        this.authURL = authURL;
        this.tokenURL = tokenURL;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectURI = redirectURI;
        this.refreshTokenHolder = refreshTokenHolder;
    }

    public static OAuthClientBuilder create(String authURL, String tokenURL){
        return new OAuthClientBuilder(authURL, tokenURL);
    }

    public OAuthRequest url(final String url){
        return new OAuthRequest(wsUrl(url));
    }

    public Promise<WSRequest> wsUrl(final String url){
        return getToken().map(token -> {
            Logger.debug("url={} token={}", url, token);
            return WS.url(url).setHeader(AUTHORIZATION, "Bearer " + token);
        });
    }

    public Promise<String> getToken(){
        Logger.debug("getToken saved code={}", refreshTokenHolder.get());

        return WS.url(tokenURL)
                .setQueryParameter("redirect_uri", redirectURI)
                .setQueryParameter("code", refreshTokenHolder.get())
                .setQueryParameter("client_id", clientId)
                .setQueryParameter("client_secret", clientSecret)
                .setQueryParameter("grant_type", "authorization_code")
                .post("") //getTokenRequestBody(refreshTokenHolder.get(), "authorization_code")
                .map(this::validateJson)
                .map(this::persistRefreshToken)
                .map(this::getAcccessToken);
    }

    public Promise<JsonNode> getTokenByAuthorizationCode(String authToken){
        Logger.debug("getTokenByAuthorizationCode={}", authToken);

        return WS.url(tokenURL)
                .setQueryParameter("redirect_uri", redirectURI)
                .setQueryParameter("code", authToken)
                .setQueryParameter("client_id", clientId)
                .setQueryParameter("client_secret", clientSecret)
                .setQueryParameter("grant_type", "authorization_code")
                .post("") // getTokenRequestBody(authToken, "authorization_code")
                .map(this::validateJson)
                //.map(this::persistRefreshToken)
                ;
    }

    private String getTokenRequestBody(String code, String grantType){
        StringBuilder builder = new StringBuilder();
        builder.append("redirect_uri=").append(redirectURI)
                .append("&code=").append(code)
                .append("&client_id=").append(clientId)
                .append("&client_secret=").append(clientSecret)
                .append("&grant_type=").append(grantType);
        return builder.toString();
    }

    public String authorizeURL() {
        return AuthorizeBuilder.url(authURL)
                .clientId(clientId)
                .redirectURI(redirectURI)
                .responseType("code")
                .build();
    }

    private JsonNode validateJson(WSResponse response){
        Logger.debug("validateJson {} {}", response.getStatus(), response.getBody());

        if (response.getStatus() == 200) {
            return response.asJson();
        } else {
            throw new WSException(response);
        }
    }

    private JsonNode persistRefreshToken(JsonNode json){
        Logger.debug("persistRefreshToken={}", json.path("refresh_token").asText());
        refreshTokenHolder.persist(json.path("refresh_token").asText());
        return json;
    }

    private String getAcccessToken(JsonNode json){
        Logger.debug("getAcccessToken={}", json.path("access_token").asText());
        return json.path("access_token").asText();
    }
}