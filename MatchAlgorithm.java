package com.netconnect.Applications.UserApp.Helpers;

import com.netconnect.Applications.UserApp.User;
import java.util.*;

public class MatchAlgorithm {

    private static final Map<String, List<String>> complementaryInterests = new HashMap<>();

    static {
        complementaryInterests.put("Looking for help", List.of("Offering help", "Looking to educate myself"));
        complementaryInterests.put("Looking for a job", List.of("Offering a job"));
        complementaryInterests.put("Offering help", List.of("Looking for help", "Looking to educate myself"));
        complementaryInterests.put("Offering a job", List.of("Looking for a job"));
        complementaryInterests.put("Looking to chat", List.of("Looking to chat", "Looking to educate myself"));
        complementaryInterests.put("Interested in mentoring", List.of("Seeking mentorship"));
        complementaryInterests.put("Seeking mentorship", List.of("Interested in mentoring"));
        complementaryInterests.put("Volunteering for tech events", List.of("Volunteering for tech events"));
        complementaryInterests.put("Contributing to open-source projects", List.of("Contributing to open-source projects"));
        complementaryInterests.put("Looking to educate myself", List.of("Offering help", "Looking to chat"));
        complementaryInterests.put("Coordonating Bachelor Thesis", List.of("Coordonator for my Bachelor Thesis"));
        complementaryInterests.put("Coordonating Mastert Disertation", List.of("Coordonator for my Master Disertation"));
        complementaryInterests.put("Coordonating PHD Thesis", List.of("Coordonator for my PHD Thesis"));
    }

    /**
     * Calculates the match score between two users.
     * @param currentUser The user initiating the match.
     * @param otherUser The user being evaluated for a match.
     * @return The match score.
     */
    public static int calculateMatchScore(User currentUser, User otherUser) {
        int score = 0;

        score += calculateInterestScore(currentUser.getInterests(), otherUser.getInterests());

        score += calculateExactMatchScore(currentUser.getAbilities(), otherUser.getAbilities(), 5);

        score += calculateExactMatchScore(currentUser.getLanguages(), otherUser.getLanguages(), 3);

        score += calculateExactMatchScore(currentUser.getExpertise(), otherUser.getExpertise(), 7);

        score += calculateExactMatchScore(currentUser.getEducation(), otherUser.getEducation(), 2);

        score += calculateCompatibilityScore(currentUser, otherUser);

        return score;
    }

    private static int calculateInterestScore(List<String> userInterests, List<String> otherInterests) {
        int score = 0;

        if (userInterests == null || otherInterests == null) return score;

        for (String interest : userInterests) {
            List<String> complementary = complementaryInterests.get(interest);
            if (complementary != null) {
                for (String complementaryInterest : complementary) {
                    if (otherInterests.contains(complementaryInterest)) {
                        score += 30;
                        break;
                    }
                }
            }
        }
        return score;
    }

    private static int calculateExactMatchScore(List<String> currentList, List<String> otherList, int weight) {
        if (currentList == null || otherList == null) return 0;

        int score = 0;
        for (String item : currentList) {
            if (otherList.contains(item)) {
                score += weight;
            }
        }
        return score;
    }

    private static int calculateCompatibilityScore(User currentUser, User otherUser) {
        Set<String> currentAttributes = new HashSet<>();
        Set<String> otherAttributes = new HashSet<>();

        currentAttributes.addAll(Optional.ofNullable(currentUser.getInterests()).orElse(List.of()));
        currentAttributes.addAll(Optional.ofNullable(currentUser.getAbilities()).orElse(List.of()));
        currentAttributes.addAll(Optional.ofNullable(currentUser.getLanguages()).orElse(List.of()));
        currentAttributes.addAll(Optional.ofNullable(currentUser.getExpertise()).orElse(List.of()));
        currentAttributes.addAll(Optional.ofNullable(currentUser.getEducation()).orElse(List.of()));

        otherAttributes.addAll(Optional.ofNullable(otherUser.getInterests()).orElse(List.of()));
        otherAttributes.addAll(Optional.ofNullable(otherUser.getAbilities()).orElse(List.of()));
        otherAttributes.addAll(Optional.ofNullable(otherUser.getLanguages()).orElse(List.of()));
        otherAttributes.addAll(Optional.ofNullable(otherUser.getExpertise()).orElse(List.of()));
        otherAttributes.addAll(Optional.ofNullable(otherUser.getEducation()).orElse(List.of()));

        Set<String> intersection = new HashSet<>(currentAttributes);
        intersection.retainAll(otherAttributes);

        Set<String> union = new HashSet<>(currentAttributes);
        union.addAll(otherAttributes);

        double similarity = (double) intersection.size() / union.size();

        return (int) (similarity * 20);
    }
}
