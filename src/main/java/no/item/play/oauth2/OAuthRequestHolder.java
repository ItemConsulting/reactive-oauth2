package no.item.play.oauth2;

import com.fasterxml.jackson.databind.JsonNode;
import play.libs.F.Promise;
import play.libs.ws.WSRequestHolder;
import play.libs.ws.WSResponse;

import java.io.File;
import java.io.InputStream;

public class OAuthRequestHolder {
    public Promise<WSRequestHolder> requestHolder;

    public OAuthRequestHolder(Promise<WSRequestHolder> requestHolder){
        this.requestHolder = requestHolder;
    }

    public OAuthRequestHolder setQueryParameter(String name, String value){
        requestHolder.map(r -> r.setQueryParameter(name, value));
        return this;
    }

    public OAuthRequestHolder setQueryParameter(String name, Object value){
        requestHolder.map(r -> r.setQueryParameter(name, String.valueOf(value)));
        return this;
    }

    public OAuthRequestHolder setHeader(String name, String value){
        requestHolder.map(r -> r.setHeader(name, value));
        return this;
    }

    public OAuthRequestHolder setQueryString(String query){
        requestHolder.map(r -> r.setQueryString(query));
        return this;
    }

    public Promise<JsonNode> get(){
        return requestHolder
                .flatMap(WSRequestHolder::get)
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> post(String body){
        return requestHolder
                .flatMap(request -> request.post(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> post(JsonNode body){
        return requestHolder
                .flatMap(request -> request.post(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> post(InputStream body){
        return requestHolder
                .flatMap(request -> request.post(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> post(File body){
        return requestHolder
                .flatMap(request -> request.post(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> put(String body){
        return requestHolder
                .flatMap(request -> request.put(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> put(JsonNode body){
        return requestHolder
                .flatMap(request -> request.put(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> put(InputStream body){
        return requestHolder
                .flatMap(request -> request.put(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> put(File body){
        return requestHolder
                .flatMap(request -> request.put(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> patch(String body){
        return requestHolder
                .flatMap(request -> request.patch(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> patch(JsonNode body){
        return requestHolder
                .flatMap(request -> request.patch(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> patch(InputStream body){
        return requestHolder
                .flatMap(request -> request.patch(body))
                .map(WSResponse::asJson);
    }

    public Promise<JsonNode> delete(){
        return requestHolder
                .flatMap(WSRequestHolder::delete)
                .map(WSResponse::asJson);
    }
}
