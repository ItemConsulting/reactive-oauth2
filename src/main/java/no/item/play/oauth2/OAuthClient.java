package no.item.play.oauth2;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F.Promise;
import play.libs.ws.WS;
import play.libs.ws.WSRequestHolder;
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

    public OAuthRequestHolder url(final String url){
        return new OAuthRequestHolder(wsUrl(url));
    }

    public Promise<WSRequestHolder> wsUrl(final String url){
        return getToken().map(token -> WS.url(url).setHeader(AUTHORIZATION, "Bearer " + token));
    }

    public Promise<String> getToken(){
        return WS.url(tokenURL)
                .setQueryParameter("redirect_uri", redirectURI)
                .setQueryParameter("refresh_token", refreshTokenHolder.get())
                .setQueryParameter("client_id", clientId)
                .setQueryParameter("client_secret", clientSecret)
                .setQueryParameter("grant_type", "refresh_token")
                .post("")
                .map(this::validateJson)
                .map(this::persistRefreshToken)
                .map(this::getAcccessToken);
    }

    public Promise<JsonNode> getTokenByAuthorizationCode(String authToken){
        return WS.url(tokenURL)
                .setQueryParameter("redirect_uri", redirectURI)
                .setQueryParameter("code", authToken)
                .setQueryParameter("client_id", clientId)
                .setQueryParameter("client_secret", clientSecret)
                .setQueryParameter("grant_type", "authorization_code")
                .post("")
                .map(this::validateJson)
                .map(this::persistRefreshToken);
    }

    public String authorizeURL() {
        return AuthorizeBuilder.url(authURL)
                .clientId(clientId)
                .redirectURI(redirectURI)
                .responseType("code")
                .build();
    }

    private JsonNode validateJson(WSResponse response){
        if (response.getStatus() == 200) {
            return response.asJson();
        } else {
            throw new WSException(response);
        }
    }

    private JsonNode persistRefreshToken(JsonNode json){
        refreshTokenHolder.persist(json.path("refresh_token").asText());
        return json;
    }

    private String getAcccessToken(JsonNode json){
        return json.path("access_token").asText();
    }
}