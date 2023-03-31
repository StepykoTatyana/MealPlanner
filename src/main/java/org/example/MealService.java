package org.example;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;


public class MealService {
    static Scanner scanner = new Scanner(System.in);
    static List<Meal> mealArrayList = new LinkedList<>();
    static List<Meal> mealArrayListFromBd = new LinkedList<>();
    static List<DayOfTheWeek> daysOfTheWeekArray = new LinkedList<>();
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/meals_db";
    static final String USER = "postgres";
    static final String PASS = "1111";
//
//    static Integer id = 0;
//    static Integer ingredientId = 0;

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
            System.out.println("Which category do you want to print (breakfast, lunch, dinner)?");

            while (true) {
                String categoryUser = scanner.nextLine();
                if (List.of("breakfast", "lunch", "dinner").contains(categoryUser)) {
                    List<Meal> arrayList = mealArrayListFromBd.stream().filter(x -> x.getCategory().equals(categoryUser)).toList();
                    if (arrayList.size() != 0) {
                        System.out.println("Category: " + categoryUser);
                        for (Meal m : arrayList) {
                            if (m.getCategory().equals(categoryUser)) {
                                System.out.println("Name: " + m.getName());
                                System.out.println("Ingredients: ");
                                Arrays.stream(m.getIngredients()).toList().forEach(System.out::println);
                                System.out.println();
                            }
                        }
                    } else {
                        System.out.println("No meals found.");
                    }
                    break;
                } else {
                    System.out.println("Wrong meal category! Choose from: breakfast, lunch, dinner.");
                }
            }


        }

    }

    public void add() throws SQLException {

        Statement statement = connection.createStatement();
        System.out.println("Which meal do you want to add (breakfast, lunch, dinner)?");
        String choice = scanner.nextLine().trim();
        String[] ingredients = new String[0];
        String meal = null;
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
        Statement statement4 = connection.createStatement();
        ResultSet rsMealsId = statement4.executeQuery("select meal_id from meals order by meal_id desc LIMIT 1;");
        int lastMealId = 0;
        if (rsMealsId.next()) {
            lastMealId = Integer.parseInt(rsMealsId.getString("meal_id"));
        }


        ResultSet rsIngredientId = statement4.executeQuery("select ingredient_id from ingredients order by ingredient_id desc LIMIT 1;");
        int lastIngredientId = 0;
        if (rsIngredientId.next()) {
            lastIngredientId = Integer.parseInt(rsIngredientId.getString("ingredient_id"));
        }

        Meal mealObj = new Meal(choice, meal, ingredients);
        mealArrayList.add(mealObj);

        statement.executeUpdate(
                String.format("insert into meals(category, meal, meal_id)" +
                                " values('%s', '%s', '%d');",
                        choice, meal, lastMealId + 1));


        for (String ingredient : ingredients) {
            lastIngredientId++;
            statement.executeUpdate(
                    String.format("insert into ingredients(ingredient, ingredient_id, meal_id)" +
                                    " values('%s', '%d', '%d');",
                            ingredient, lastIngredientId, lastMealId + 1));
        }
        System.out.println("The meal has been added!");
        getDataFromBD();
        statement.close();
        statement4.close();

    }


    public void getDataFromBD() {
        try {
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
        } catch (Exception ignored) {
        }
    }

    public void createTablesBD() throws SQLException {
        Statement statement = connection.createStatement();
        if (mealArrayListFromBd.size() == 0) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS meals " +
                    "(" +
                    "    category character varying(200) NOT NULL," +
                    "    meal character varying(200) NOT NULL," +
                    "    meal_id integer" +
                    ");");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS ingredients " +
                    "(" +
                    "    ingredient character varying(200) NOT NULL," +
                    "    ingredient_id integer," +
                    "    meal_id integer NOT NULL" +
                    ");");

        }
        statement.close();
    }

    public void plan() throws SQLException {
        String[] daysOfThwWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        String[] categoryArray = {"breakfast", "lunch", "dinner"};
        for (String dayOfTheWeek : daysOfThwWeek) {
            DayOfTheWeek dayOfTheWeek1 = new DayOfTheWeek();
            dayOfTheWeek1.setDayOfTheWeek(dayOfTheWeek);
            System.out.println(dayOfTheWeek);
            for (String categoryUser : categoryArray) {
                getAllBreakfasts(dayOfTheWeek, categoryUser, dayOfTheWeek1);
            }
            System.out.printf("Yeah! We planned the meals for %s.%n", dayOfTheWeek);
            System.out.println();
            daysOfTheWeekArray.add(dayOfTheWeek1);

        }

    }

    private void getAllBreakfasts(String dayOfTheWeekUser, String categoryUser, DayOfTheWeek dayOfTheWeek1) throws SQLException {
        List<Meal> arrayList = mealArrayListFromBd.stream().filter(x -> x.getCategory().equals(categoryUser)).toList();
        if (arrayList.size() != 0) {
            Statement statement = connection.createStatement();
            statement.executeQuery(String.format("Select meal from meals where category='%s' order by meal", categoryUser));
            ResultSet resultSet = statement.getResultSet();
            while (resultSet.next()) {
                System.out.println(resultSet.getString("meal"));

            }
            statement.close();
        } else {
            System.out.println("No meals found.");
        }
        System.out.printf("Choose the %s for %s from the list above:%n", categoryUser, dayOfTheWeekUser);
        while (true) {
            String mealUser = scanner.nextLine();
            if (mealArrayListFromBd.stream()
                    .filter(x -> x.getCategory().equals(categoryUser))
                    .filter(y -> y.getName().equals(mealUser))
                    .toList().size() != 0) {
                switch (categoryUser) {
                    case "breakfast" -> dayOfTheWeek1.setBreakfastMeal(mealUser);
                    case "lunch" -> dayOfTheWeek1.setLunchMeal(mealUser);
                    case "dinner" -> dayOfTheWeek1.setDinnerMeal(mealUser);
                }

                break;
            } else {
                System.out.println("This meal doesn’t exist. Choose a meal from the list above.");

            }
        }


    }

    public void printPlanOfTheWeek() {
        for (DayOfTheWeek dayOfTheWeek : daysOfTheWeekArray) {
            System.out.println(dayOfTheWeek.getDayOfTheWeek());
            System.out.println("Breakfast: " + dayOfTheWeek.getBreakfastMeal());
            System.out.println("Lunch: " + dayOfTheWeek.getLunchMeal());
            System.out.println("Dinner: " + dayOfTheWeek.getDinnerMeal());
            System.out.println();
        }
    }
}
