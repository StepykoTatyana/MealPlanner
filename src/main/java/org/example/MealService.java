package org.example;

import java.sql.*;
import java.util.*;


public class MealService {
    static Scanner scanner = new Scanner(System.in);
    static List<Meal> mealArrayList = new LinkedList<>();
    static List<Meal> mealArrayListFromBd = new LinkedList<>();
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/meals_db";
    static final String USER = "postgres";
    static final String PASS = "1111";

    static Integer id = 0;
    static Integer ingredientId = 0;

    static Connection connection;

    static {
        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);
            connection.setAutoCommit(true);
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
    }

    void show() {
        if (mealArrayListFromBd.size() == 0) {
            System.out.println("No meals saved. Add a meal first.");
        } else {
            for (Meal m : mealArrayListFromBd) {
                System.out.println("Category: " + m.getCategory());
                System.out.println("Name: " + m.getName());
                System.out.println("Ingredients: ");
                Arrays.stream(m.getIngredients()).toList().forEach(System.out::println);
                System.out.println();
            }
        }

    }

    public void add() throws SQLException {
        Statement statement = connection.createStatement();
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String choice = scanner.nextLine().trim();
        String[] ingredients = new String[0];
        String meal = null;
        if (mealArrayListFromBd.size() == 0) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals " +
                    "(" +
                    "    category character varying(200) NOT NULL," +
                    "    meal character varying(200) NOT NULL," +
                    "    meal_id serial PRIMARY KEY" +
                    ");");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients " +
                    "(" +
                    "    ingredient character varying(200) NOT NULL," +
                    "    ingredient_id serial PRIMARY KEY," +
                    "    meal_id bigint NOT NULL" +
                    ");");

        }
        while (ingredients.length == 0) {
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
        statement.executeUpdate(
                String.format("insert into meals(category, meal)" +
                                " values('%s', '%s');",
                        choice, meal));
        Statement statement4 = connection.createStatement();
        ResultSet rsMealsId = statement4.executeQuery("select meal_id from meals order by meal_id desc LIMIT 1;");
        int lastMealId = 0;
        if (rsMealsId.next()) {
            lastMealId = Integer.parseInt(rsMealsId.getString("meal_id"));
        }
        for (String ingredient : ingredients) {
            ingredientId++;
            statement.executeUpdate(
                    String.format("insert into ingredients(ingredient, meal_id)" +
                                    " values('%s', '%d');",
                            ingredient, lastMealId));
        }
        System.out.println("The meal has been added!");
        getDataFromBD();
        statement.close();
        statement4.close();
        connection.close();
    }


   public void getDataFromBD() throws SQLException {
        Statement statement2 = connection.createStatement();
        Statement statement3 = connection.createStatement();
        ResultSet rsMeals = statement2.executeQuery("select * from meals;");
        mealArrayListFromBd = new ArrayList<>();

        while (rsMeals.next()) {
            Meal meal1 = new Meal();
            meal1.setCategory(rsMeals.getString("category"));
            meal1.setName(rsMeals.getString("meal"));
            int meal_id = rsMeals.getInt("meal_id");
            ResultSet rsIngredients = statement3.executeQuery(String.format("select * from ingredients where meal_id=%d;", meal_id));
            ArrayList<String> strings = new ArrayList<>();
            while (rsIngredients.next()) {
                strings.add(rsIngredients.getString("ingredient"));
            }
            meal1.setIngredients(strings.toArray(String[]::new));
            mealArrayListFromBd.add(meal1);
        }

        statement2.close();
        statement3.close();

    }
}
