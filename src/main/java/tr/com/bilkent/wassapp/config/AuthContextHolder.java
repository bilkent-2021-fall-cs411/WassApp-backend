package tr.com.bilkent.wassapp.config;

public class AuthContextHolder {

    private static final ThreadLocal<String> emailHolder = new ThreadLocal<>();

    private AuthContextHolder() {
    }

    public static void setEmail(String email) {
        emailHolder.remove();
        emailHolder.set(email);
    }

    public static String getEmail() {
        return emailHolder.get();
    }
}
