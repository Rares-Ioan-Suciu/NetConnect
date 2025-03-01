package com.netconnect.Applications.UserApp.Details;

public interface VerifiableInfo {

    boolean isVerified();
    void verifyInfo() throws Exception;
    String getDetails();
}
