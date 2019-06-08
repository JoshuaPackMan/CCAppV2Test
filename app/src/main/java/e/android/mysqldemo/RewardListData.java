package e.android.mysqldemo;

public class RewardListData {
    private String card;
    private String reward;
    private String business;

    public RewardListData(String card, String reward, String business) {
        this.card = card;
        this.reward = reward;
        this.business = business;
    }

    public String getReward() {
        return reward;
    }

    public String getCard() {
        return card;
    }

    public String getBusiness() {
        return business;
    }
}
