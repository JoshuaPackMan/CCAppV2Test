package e.android.mysqldemo;

public class CardListData {
    private String cCard;
    public enum CardColor {WHITE, BLUE}
    private CardColor color;

    public CardListData(String cCard, CardColor color) {
        this.cCard = cCard;
        this.color = color;
    }

    public String getcCard() {
        return cCard;
    }

    public CardColor getColor(){
        return color;
    }

    public void cardClicked(){
        if(color == CardColor.BLUE){
            color = CardColor.WHITE;
        } else{
            color = CardColor.BLUE;
        }
    }
}
