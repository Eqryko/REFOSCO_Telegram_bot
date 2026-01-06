package org.example.model;

public class MatchResult {

    private String homeTeam;
    private String awayTeam;
    private int homeScore;
    private int awayScore;

    public MatchResult(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
    }

    public String format() {
        return homeTeam + " " + homeScore + " - " + awayScore + " " + awayTeam;
    }
}
