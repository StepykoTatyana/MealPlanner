package org.example;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) throws SQLException, IOException {

        Scanner scanner = new Scanner(System.in);
        MealService mealService = new MealService();
        mealService.createTablesBD();
        mealService.getDataFromBD();
        mealService.downloadPlan();

        loop:
        while (true) {
            System.out.println("What would you like to do (add, show, plan, save, exit)?");
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
                    MealService.connection.close();
                    break loop;
                }
                case "plan" -> {
                    mealService.plan();
                    mealService.printPlanOfTheWeek();
                    System.out.println();
                }
                case "save"->{
                    mealService.save();
                    System.out.println();
                }
            }
        }

    }


}