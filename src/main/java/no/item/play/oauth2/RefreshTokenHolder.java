package no.item.play.oauth2;

public interface RefreshTokenHolder {
    void persist(String token);
    String get();
}