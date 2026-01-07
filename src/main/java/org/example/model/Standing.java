package org.example.model;

public class Standing {

    private String team;
    private int wins;
    private int losses;

    public Standing(String team, int wins, int losses) {
        this.team = team;
        this.wins = wins;
        this.losses = losses;
    }

    public String format(int position) {
        return position + ". " + team + " (" + wins + "-" + losses + ")";
    }
}
