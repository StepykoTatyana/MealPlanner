package org.example;
import java.sql.*;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        Scanner scanner = new Scanner(System.in);
        MealService mealService = new MealService();
        mealService.getDataFromBD();
        loop:
        while (true) {
            System.out.println("What would you like to do (add, show, exit)?");
            String action = scanner.nextLine().trim();
            switch (action) {
                case "add" -> {
                    mealService.add();
                    System.out.println();
                }
                case "show" -> {
                    mealService.show();
                    System.out.println();
                }

                case "exit" -> {
                    System.out.println("Bye!");
                    break loop;
                }
            }
        }

    }


}