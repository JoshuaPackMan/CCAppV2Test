package e.android.mysqldemo;

public class ListData {
    private String card;
    private String reward;

    public ListData(String card, String reward) {
        this.card = card;
        this.reward = reward;
    }

    public String getCard() {
        return card;
    }

    public String getReward() {
        return reward;
    }
}
