package expensetracker;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Holds all expenses in memory and provides the business logic (add, delete, search, totals). */
public class ExpenseManager {
    private final List<Expense> expenses = new ArrayList<>();
    private int nextId = 1;

    public ExpenseManager(List<Expense> existing) {
        expenses.addAll(existing);
        for (Expense e : expenses) {
            nextId = Math.max(nextId, e.getId() + 1);
        }
    }

    public Expense add(LocalDate date, String category, String description, double amount) {
        Expense expense = new Expense(nextId++, date, category, description, amount);
        expenses.add(expense);
        return expense;
    }

    public boolean delete(int id) {
        return expenses.removeIf(e -> e.getId() == id);
    }

    /** All expenses, newest first. */
    public List<Expense> getAll() {
        List<Expense> copy = new ArrayList<>(expenses);
        copy.sort(Comparator.comparing(Expense::getDate).reversed().thenComparing(Expense::getId));
        return copy;
    }

    public List<Expense> getByMonth(YearMonth month) {
        List<Expense> result = new ArrayList<>();
        for (Expense e : getAll()) {
            if (YearMonth.from(e.getDate()).equals(month)) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Expense> search(String keyword) {
        String key = keyword.toLowerCase();
        List<Expense> result = new ArrayList<>();
        for (Expense e : getAll()) {
            if (e.getDescription().toLowerCase().contains(key)
                    || e.getCategory().toLowerCase().contains(key)) {
                result.add(e);
            }
        }
        return result;
    }

    public static double total(List<Expense> list) {
        double sum = 0;
        for (Expense e : list) {
            sum += e.getAmount();
        }
        return sum;
    }

    /** Category name -> total spent, sorted alphabetically. */
    public static Map<String, Double> totalsByCategory(List<Expense> list) {
        Map<String, Double> totals = new TreeMap<>();
        for (Expense e : list) {
            totals.merge(e.getCategory(), e.getAmount(), Double::sum);
        }
        return totals;
    }

    public List<Expense> snapshot() {
        return new ArrayList<>(expenses);
    }
}
