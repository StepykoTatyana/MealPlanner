package org.example;


import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class MealService {
    static Scanner scanner = new Scanner(System.in);
    static ArrayList<Meal> mealArrayList = new ArrayList<>();

    static Integer id = 0;


    void show() throws SQLException, SQLException, ClassNotFoundException {
        String url = "jdbc:postgresql://127.0.0.1:5432/food_db";

        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "1111");
        props.setProperty("ssl", "true");
        Connection conn = DriverManager.getConnection(url, props);
        Class.forName("org.postgresql.Driver");
       // String url = "jdbc:postgresql://localhost/food_db?user=postgres&password=1111&ssl=true";
//        Connection conn = DriverManager.getConnection(url);

        Connection connection = DriverManager.getConnection(url);

        Statement statement = connection.createStatement();
        connection.setAutoCommit(true);
        ResultSet rs = statement.executeQuery("select * from food");
        if (mealArrayList.size() == 0) {
            System.out.println("No meals saved. Add a meal first.");
        } else {
            while (rs.next()) {
                String[] in = rs.getString("ingredients").split(",");
                System.out.println("Category: " + rs.getString("category"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Ingredients: ");
                Arrays.stream(in).toList().forEach(System.out::println);
                System.out.println();
            }
//            for (Meal m : mealArrayList) {
//                System.out.println("Category: " + m.getCategory());
//                System.out.println("Name: " + m.getName());
//                System.out.println("Ingredients: ");
//                Arrays.stream(m.getIngredients()).toList().forEach(System.out::println);
//                System.out.println();
//
//            }
        }
        statement.close();
        connection.close();
    }

    public void add() throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        System.out.println("1");
        String url = "jdbc:postgresql://127.0.0.1:5432/food_db?user=postgres&password=1111&ssl=true";
//        Connection conn = DriverManager.getConnection(url);

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        System.out.println("PostgreSQL JDBC Driver successfully connected");
        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(url);

        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You successfully connected to database now");
        } else {
            System.out.println("Failed to make connection to database");
        }
        System.out.println("2");
        assert connection != null;
        Statement statement = connection.createStatement();
        System.out.println("3");
        connection.setAutoCommit(true);
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String choice = scanner.nextLine().trim();
        String[] ingredients = new String[0];
        String meal = null;
        if (mealArrayList.size() == 0) {
            statement.executeUpdate("create table food (" +
                    "id integer," +
                    "category varchar(1024) NOT NULL" +
                    "name varchar(1024) NOT NULL" +
                    "ingredients varchar(1024) NOT NULL" +
                    ")");
            statement.close();
            connection.close();
        }
        while (ingredients.length == 0) {
            System.out.println("1");
            switch (choice) {

                case "breakfast", "lunch", "dinner" -> {
                    System.out.println("Input the meal's name:");
                    meal = scanner.nextLine().trim();
                    while (!meal.matches("[a-zA-Z\\s]+")) {
                        System.out.println("Wrong format. Use letters only!");
                        meal = scanner.nextLine().trim();

                    }
                    System.out.println("Input the ingredients:");
                    ingredients = Arrays.stream(scanner.nextLine().split(",")).map(String::trim).toArray(String[]::new);

                    while (Arrays.stream(ingredients).toList().stream().filter(x -> !x.matches("[a-zA-Z\\s]+")).toList().size() != 0) {
                        System.out.println("Wrong format. Use letters only!");
                        ingredients = Arrays.stream(scanner.nextLine().split(",")).map(String::trim).toArray(String[]::new);

                    }
                }
                default -> {
                    System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                    choice = scanner.nextLine().trim();
                }
            }
        }

        Meal mealObj = new Meal(choice, meal, ingredients);
        mealArrayList.add(mealObj);
        id++;
        String ingredientsString = String.join(",", ingredients);
        String str = String.format("insert into food (id, category, name, ingredients) values (%d, '%s', '%s', '%s')",
                id, choice, meal, ingredientsString);
        statement.executeUpdate(str);

        connection.close();
        System.out.println("The meal has been added!");


    }

}
