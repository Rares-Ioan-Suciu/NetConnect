package com.netconnect.Applications.UserApp.Details;

import com.netconnect.Applications.UserApp.Exception.InvalidFullNameException;

public class NameDetails extends UserDetails {
    private final String fullName;

    public NameDetails(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String getDetails() {
        return fullName != null ? fullName : "Full name not provided";
    }

    @Override
    public boolean isVerified() {

        return fullName != null && fullName.trim().matches("[a-zA-Z\\s]+");
    }

    @Override
    public void verifyInfo() throws InvalidFullNameException {
        if (!isVerified()) {
            throw new InvalidFullNameException("Full name must contain only letters and spaces.");
        }
        setVerified(true);
    }
}
