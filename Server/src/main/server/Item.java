public class Item {
    private String description;
    private double minPrice;
    private double endTime;

    public Item(String description, double minPrice, double endTime) {
        this.description = description;
        this.minPrice = minPrice;
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Item{" +
                "description='" + description + '\'' +
                ", minPrice=" + minPrice +
                ", endTime=" + endTime +
                '}';
    }
}
