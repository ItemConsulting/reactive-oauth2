package no.item.play.oauth2;

public class SimpleRefreshTokenHolder implements RefreshTokenHolder {
    /**
     * Used for keeping a single refreshtokens in one-user-applications.
     */
    private static String refreshToken;

    /**
     * Can be overwritten to keep seperate tokens for different users
     * @param token Token to persist
     */
    @Override
    public void persist(String token){
        refreshToken = token;
    }

    /**
     * Can be overwritten to keep seperate tokens for different users
     * @return Latest refresh token
     */
    @Override
    public String get() {
        return refreshToken;
    }
}
