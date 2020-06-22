import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemsDB {
    Connection connection = null;
    Statement statement = null;


    public ItemsDB() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("JDBC:sqlite:ItemsDB.sqlite");
            System.out.println("Connected to ItemsDB!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public Item getItem(String name) {
        try {
            this.statement = connection.createStatement();
            String query = "select * from items where Name='" + name + "'";
            ResultSet result = statement.executeQuery(query);
            if(result.next()) {
                Item item = new Item(result.getString("Name"),result.getString("Description"),result.getDouble("minPrice"),
                        result.getDouble("endTime"), result.getDouble("maxPrice"));
                item.setCurrPrice(result.getDouble("currPrice"));
                return (item);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void insert(Item item) {
        if(isInDB(item)) {
            updateItem(item.getName(), item.getCurrPrice());
            return;
        }

        try {
            this.statement = connection.createStatement();
            String query = "insert into items (Name, Description, minPrice, endTime, maxPrice, currPrice) ";
            query += "values (" + "'" + item.getName() + "', '" + item.getDescription() + "', " + item.getMinPrice() + ", " + item.getEndTime() +
                ", " + item.getMaxPrice() + ", " + item.getCurrPrice() + ")";
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private boolean isInDB(Item item) {
        try {
            this.statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from items");
            while (result.next()) {
                String name = result.getString("Name");
                if(item.getName().equals(name)) return true;
            }
            return false;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void updateItem(String name, double newPrice) {
        try {

            String query = "update items set currPrice = " + newPrice + " where Name = " + "'" + name + "'";
            PreparedStatement pstatement = connection.prepareStatement(query);
            pstatement.executeUpdate();
            pstatement.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void removeItem(String name) {
        try {
            this.statement = connection.createStatement();
            String query = "delete from items where Name = '" + name + "'";
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public List<Item> listItems() {
        List<Item> itemList = new ArrayList<>(5);
        try {
            this.statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select * from items");

            while (result.next()) {
                String name = result.getString("Name");
                Item entry = new Item(result.getString("Name"),result.getString("Description"),result.getDouble("minPrice"),
                        result.getDouble("endTime"), result.getDouble("maxPrice"));
                entry.setCurrPrice(result.getDouble("currPrice"));
                itemList.add(entry);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return itemList;

    }

    public boolean hasUser(String username) {
        try {
            this.statement = connection.createStatement();
            String query = "select username from credentials where exists(select username from credentials where credentials.username = ''" +username + "')";
            ResultSet result = statement.executeQuery("select username from credentials");
            while (result.next()) {
                String name = result.getString("username");
                if(username.equals(name)) return true;
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public void storeID(String username, byte[] passHash, byte[] salt) {
        try {
            this.statement = connection.createStatement();
            String query = "insert into credentials (username, hash, salt) ";
            query += "values (" + "'" + username + "', '" + passHash + "', '" + salt + "')";
            statement.executeUpdate(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public void closeConnection() throws SQLException {
        connection.close();
    }

    public boolean passwordMatch(String username, String password) {
        try {
            this.statement = connection.createStatement();
            String query = "select username from credentials where exists(select username from credentials where credentials.username = " + username + ")";
            ResultSet result = statement.executeQuery("select * from credentials");
            while (result.next()) {
                String name = result.getString("username");
                if(username.equals(name)) {
                    byte[] passHash = result.getBytes("hash");
                    byte[] salt = result.getString("salt").getBytes();
                    byte[] pass = password.getBytes();
                    MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                    byte[] saltedPassword = new byte[salt.length + pass.length];
                    System.arraycopy(pass, 0, saltedPassword, 0, pass.length);
                    System.arraycopy(salt, 0, saltedPassword, pass.length, salt.length);
                    byte[] encryptedPass = messageDigest.digest(saltedPassword);
                    System.out.println("Check if password match...");
                    return Arrays.equals(passHash, encryptedPass);

                }
            }
        } catch (SQLException | NoSuchAlgorithmException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}
