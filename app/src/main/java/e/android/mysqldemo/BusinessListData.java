package e.android.mysqldemo;

public class BusinessListData {
    private String business;
    public enum BusinessColor {WHITE, BLUE;}
    private BusinessColor color;

    public BusinessListData(String business, BusinessColor color) {
        this.business = business;
        this.color = color;
    }

    public String getBusiness() {
        return business;
    }

    public BusinessColor getColor(){
        return color;
    }

    public void cardClicked(){
        if(color == BusinessColor.BLUE){
            color = BusinessColor.WHITE;
        } else{
            color = BusinessColor.BLUE;
        }
    }
}
