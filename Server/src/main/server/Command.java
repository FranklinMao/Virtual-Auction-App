//class for sending commands such as new user, bids, sold
public class Command {
    private final String command;
    private final String username;
    private final String itemName;
    private final Double price;

    public Command(String command, String username, String itemName, Double price) {
        this.command = command;
        this.username = username;
        this.itemName = itemName;
        this.price = price;
    }

    public String getCommand() {
        return command;
    }

    public String getUsername() {
        return username;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Command{" +
                "command='" + command + '\'' +
                ", username='" + username + '\'' +
                ", price=" + price +
                '}';
    }

    public String getItemName() {
        return itemName;
    }
}
