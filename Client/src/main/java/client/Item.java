package client;

/*
 *  EE422C Final Project submission by
 *  Replace <...> with your actual data.
 *  <Franklin Mao>
 *  <fm8487>
 *  <16295>
 *  Spring 2020
 */
public class Item {
    private String name;

    private String description;
    private double minPrice;
    private double endTime;

    private double maxPrice;    //once bidding reaches this price, item is sold

    private double currPrice;


    public Item(String name, String description, double minPrice, double endTime, double maxPrice) {
        this.name = name;
        this.description = description;
        this.minPrice = minPrice;
        this.endTime = endTime;
        currPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public double getMaxPrice() {
        return maxPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getEndTime() {
        return endTime;
    }

    public double getCurrPrice() {
        return currPrice;
    }

    public void setCurrPrice(double currPrice) {
        this.currPrice = currPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", minPrice=" + minPrice +
                ", endTime=" + endTime +
                ", maxPrice=" + maxPrice +
                ", currPrice=" + currPrice +
                '}';
    }
}
