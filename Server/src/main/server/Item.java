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
    private double currPrice;

    public Item(String name, String description, double minPrice, double endTime) {
        this.name = name;
        this.description = description;
        this.minPrice = minPrice;
        this.endTime = endTime;
        currPrice = minPrice;
    }

    @Override
    public String toString() {
        return "Item{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", minPrice=" + minPrice +
                ", endTime=" + endTime +
                '}';
    }
}
