package expensetracker;

import java.time.LocalDate;

/** A single expense entry. Immutable, so it is safe to share around the program. */
public class Expense {
    private final int id;
    private final LocalDate date;
    private final String category;
    private final String description;
    private final double amount;

    public Expense(int id, LocalDate date, String category, String description, double amount) {
        this.id = id;
        this.date = date;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getAmount() { return amount; }

    @Override
    public String toString() {
        return String.format("%-4d %-12s %-14s %-28s %10.2f",
                id, date, category, truncate(description, 28), amount);
    }

    private static String truncate(String text, int max) {
        return text.length() <= max ? text : text.substring(0, max - 3) + "...";
    }
}
