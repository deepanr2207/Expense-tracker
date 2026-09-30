package expensetracker;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/** Console menu for the Expense Tracker. */
public class Main {
    private static final String DATA_FILE = "expenses.csv";
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        FileStorage storage = new FileStorage(DATA_FILE);
        ExpenseManager manager;
        try {
            manager = new ExpenseManager(storage.load());
        } catch (IOException e) {
            System.out.println("Could not read " + DATA_FILE + ": " + e.getMessage());
            manager = new ExpenseManager(List.of());
        }

        System.out.println("=== Expense Tracker ===");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = in.nextLine().trim();
            switch (choice) {
                case "1" -> addExpense(manager, storage);
                case "2" -> printTable(manager.getAll());
                case "3" -> viewMonth(manager);
                case "4" -> categorySummary(manager);
                case "5" -> searchExpenses(manager);
                case "6" -> deleteExpense(manager, storage);
                case "7" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Please enter a number from 1 to 7.");
            }
        }
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Add expense");
        System.out.println("2. View all expenses");
        System.out.println("3. View expenses by month");
        System.out.println("4. Spending by category");
        System.out.println("5. Search");
        System.out.println("6. Delete expense");
        System.out.println("7. Exit");
        System.out.print("Choose: ");
    }

    private static void addExpense(ExpenseManager manager, FileStorage storage) {
        LocalDate date = readDate("Date (yyyy-mm-dd, blank for today): ");
        String category = readNonEmpty("Category (e.g. Food, Travel, Bills): ");
        String description = readNonEmpty("Description: ");
        double amount = readAmount("Amount: ");

        Expense e = manager.add(date, category, description, amount);
        save(manager, storage);
        System.out.println("Added expense #" + e.getId() + ".");
    }

    private static void viewMonth(ExpenseManager manager) {
        System.out.print("Month (yyyy-mm, blank for this month): ");
        String text = in.nextLine().trim();
        try {
            YearMonth month = text.isEmpty() ? YearMonth.now() : YearMonth.parse(text);
            System.out.println("Expenses for " + month + ":");
            printTable(manager.getByMonth(month));
        } catch (DateTimeParseException ex) {
            System.out.println("Invalid month. Use the format yyyy-mm, e.g. 2026-09.");
        }
    }

    private static void categorySummary(ExpenseManager manager) {
        List<Expense> all = manager.getAll();
        if (all.isEmpty()) {
            System.out.println("No expenses yet.");
            return;
        }
        double grandTotal = ExpenseManager.total(all);
        System.out.println();
        for (Map.Entry<String, Double> entry : ExpenseManager.totalsByCategory(all).entrySet()) {
            double percent = entry.getValue() / grandTotal * 100;
            System.out.printf("%-16s %10.2f  (%.1f%%)%n", entry.getKey(), entry.getValue(), percent);
        }
        System.out.printf("%-16s %10.2f%n", "TOTAL", grandTotal);
    }

    private static void searchExpenses(ExpenseManager manager) {
        String keyword = readNonEmpty("Search for: ");
        printTable(manager.search(keyword));
    }

    private static void deleteExpense(ExpenseManager manager, FileStorage storage) {
        System.out.print("ID to delete: ");
        try {
            int id = Integer.parseInt(in.nextLine().trim());
            if (manager.delete(id)) {
                save(manager, storage);
                System.out.println("Deleted expense #" + id + ".");
            } else {
                System.out.println("No expense with ID " + id + ".");
            }
        } catch (NumberFormatException ex) {
            System.out.println("Please enter a valid number.");
        }
    }

    private static void printTable(List<Expense> list) {
        if (list.isEmpty()) {
            System.out.println("Nothing to show.");
            return;
        }
        System.out.println();
        System.out.printf("%-4s %-12s %-14s %-28s %10s%n", "ID", "Date", "Category", "Description", "Amount");
        System.out.println("-".repeat(72));
        for (Expense e : list) {
            System.out.println(e);
        }
        System.out.println("-".repeat(72));
        System.out.printf("%-59s %12.2f%n", "Total", ExpenseManager.total(list));
    }

    private static void save(ExpenseManager manager, FileStorage storage) {
        try {
            storage.save(manager.snapshot());
        } catch (IOException e) {
            System.out.println("Warning: could not save data: " + e.getMessage());
        }
    }

    // ---- input helpers: keep asking until the input is valid ----

    private static String readNonEmpty(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = in.nextLine().trim();
            if (!text.isEmpty()) return text;
            System.out.println("This field cannot be empty.");
        }
    }

    private static double readAmount(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(in.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Amount must be greater than zero.");
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static LocalDate readDate(String prompt) {
        while (true) {
            System.out.print(prompt);
            String text = in.nextLine().trim();
            if (text.isEmpty()) return LocalDate.now();
            try {
                return LocalDate.parse(text);
            } catch (DateTimeParseException ex) {
                System.out.println("Invalid date. Use yyyy-mm-dd, e.g. 2026-09-30.");
            }
        }
    }
}
