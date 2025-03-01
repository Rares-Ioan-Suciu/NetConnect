package com.netconnect.Applications.UserApp.Details;

public abstract class UserDetails implements VerifiableInfo {
    private boolean verified;

    public UserDetails() {
        this.verified = false;
    }

    @Override
    public boolean isVerified() {
        return verified;
    }

    protected void setVerified(boolean verified) {
        this.verified = verified;
    }

    @Override
    public abstract void verifyInfo() throws Exception;
}
