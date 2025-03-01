package com.netconnect.Applications.UserApp;

import com.netconnect.Applications.UserApp.Details.*;


import java.util.List;

public class User
{
    private final Username username;
    private final Email email;
    private final Password password;
    private NameDetails nameDetails;
    private String genderDetails;
    private DateOfBirthDetails dateOfBirthDetails;
    private List<String> interests;
    private List<String> Abilities;
    private List<String> Languages;
    private List<String> Expertise;
    private List<String> Education;
    private Question question;
    private Boolean isUpdating = Boolean.FALSE;
    private List<User> bestMatches;
    private Boolean isFinished = Boolean.FALSE;
    private String imagePath ;



    public User(Username username,Email email, Password password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(Username username, Email email, Password password, NameDetails nameDetails, String genderDetails, DateOfBirthDetails dateOfBirthDetails, List<String> interests, List<String> Abilities, List<String> Languages, List<String> Expertise, List<String> Education, Question question, String imagePath) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.nameDetails = nameDetails;
        this.genderDetails = genderDetails;
        this.dateOfBirthDetails = dateOfBirthDetails;
        this.interests = interests != null ? interests : List.of();
        this.Abilities = Abilities != null ? Abilities : List.of();
        this.Languages = Languages != null ? Languages : List.of();
        this.Expertise = Expertise != null ? Expertise : List.of();
        this.Education = Education != null ? Education : List.of();
        this.question = question;
        this.imagePath = imagePath;
    }

    public void setDetails(NameDetails nameDetails, String genderDetails, DateOfBirthDetails dateOfBirthDetails) {
        this.nameDetails = nameDetails;
        this.genderDetails = genderDetails;
        this.dateOfBirthDetails = dateOfBirthDetails;
    }

    public void setGeneralDetails(List<String> interests, List<String> Abilities, List<String> Languages, List<String> Expertise, List<String> Education) {
        this.interests = interests;
        this.Abilities = Abilities;
        this.Languages = Languages;
        this.Expertise = Expertise;
        this.Education = Education;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public void setInterests(List<String> interests) {
        this.interests = interests;
    }

    public Username getUsername() {
        return username;
    }
    public Email getEmail() {
        return email;
    }
    public DateOfBirthDetails getDateOfBirthDetails() {
        return dateOfBirthDetails;
    }
    public void setDateOfBirthDetails(DateOfBirthDetails dateOfBirthDetails) {
        this.dateOfBirthDetails = dateOfBirthDetails;
    }

    public NameDetails getNameDetails() {
        return nameDetails;
    }
    public String getGenderDetails() {
        return genderDetails;
    }
    public void setGenderDetails(String genderDetails) {
        this.genderDetails = genderDetails;
    }
    public List<String> getInterests() {
        return interests;
    }
    public List<String> getAbilities() {return Abilities;}
    public List<String> getLanguages() {return Languages;}
    public List<String> getExpertise() {return Expertise;}
    public List<String> getEducation() {return Education;}
    public Question getQuestion() {return question;}

    public Boolean getUpdating() {
        return isUpdating;
    }

    public void setUpdating(Boolean updating) {
        isUpdating = updating;
    }

    public List<User> getBestMatches() {
        return bestMatches;
    }
    public void setBestMatches(List<User> bestMatches) {
        this.bestMatches = bestMatches;
    }

    public Boolean getFinished() {
        return isFinished;
    }

    public void setFinished(Boolean finished) {
        this.isFinished = Boolean.TRUE;
    }

    public void setimagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    public String getimagePath() {
        return imagePath;
    }
}
