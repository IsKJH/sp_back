package sp.spb.auth.google_authentication.service;

public interface GoogleAuthenticationService {
    String getLoginLink();

    public String processGoogleLogin(String code);
}
