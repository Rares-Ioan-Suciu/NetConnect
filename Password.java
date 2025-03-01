package com.netconnect.Applications.UserApp.Details;
import com.netconnect.Applications.UserApp.Exception.PasswordException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.mindrot.jbcrypt.BCrypt;

public class Password extends UserDetails {
    private final String password;
    private final String hashedPassword;

    public Password(String password) {
        this.password = password;
        this.hashedPassword = hashPassword(password);
    }

    @Override
    public String getDetails() {
        return hashedPassword;
    }

    @Override
    public void verifyInfo() throws PasswordException {
        if (!isValidPassword(password)) {
            throw new PasswordException("Password should contain at least one digit, one uppercase letter, one lowercase letter, one special symbol, and be at least 8 characters long, without spaces or commas.");
        }
        setVerified(true);
    }


    public boolean isValidPassword(String password) {
        Pattern digits = Pattern.compile("[0-9]");
        Pattern lowercase = Pattern.compile("[a-z]");
        Pattern uppercase = Pattern.compile("[A-Z]");
        Pattern specialSymbols = Pattern.compile("[~!@#$%^&*()_{|:;'/.?><}+=]");

        Matcher digitMatch = digits.matcher(password);
        Matcher letterMatch = lowercase.matcher(password);
        Matcher uppercaseMatch = uppercase.matcher(password);
        Matcher specialSymbolsMatch = specialSymbols.matcher(password);

        return digitMatch.find() && letterMatch.find() && uppercaseMatch.find() && specialSymbolsMatch.find()
                && password.length() >= 8 && !password.contains(" ");
    }


    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

}

