# Expense Tracker (Java)

A simple console-based expense tracker written in core Java. Add your daily expenses, see where your money goes, and keep your data saved between runs. No external libraries needed.

## Features

- Add expenses with date, category, description and amount
- View all expenses, newest first, with a running total
- View expenses for any month
- Spending summary by category with percentages
- Search by keyword in category or description
- Delete an expense by ID
- Data is saved automatically to `expenses.csv`
- Input validation: invalid dates, amounts and empty fields are rejected politely

## Project Structure

```
expense-tracker/
├── src/expensetracker/
│   ├── Main.java            # Console menu and user input
│   ├── Expense.java         # Expense model (one entry)
│   ├── ExpenseManager.java  # Business logic: add, delete, search, totals
│   └── FileStorage.java     # Reads and writes the CSV file
├── .gitignore
└── README.md
```

## Requirements

- Java 17 or newer (JDK)

## How to Run

```bash
# 1. Clone the repository
git clone https://github.com/<your-username>/expense-tracker.git
cd expense-tracker

# 2. Compile
javac -d out src/expensetracker/*.java

# 3. Run
java -cp out expensetracker.Main
```

## Sample Output

```
ID   Date         Category       Description                      Amount
------------------------------------------------------------------------
2    2026-09-30   Travel         Bus pass                         800.00
1    2026-09-28   Food           Lunch                            250.50
------------------------------------------------------------------------
Total                                                            1050.50
```

## Concepts Used

- Object-oriented design (separate model, logic and storage classes)
- Java Collections (`List`, `Map`, `TreeMap`)
- `java.time` API (`LocalDate`, `YearMonth`)
- File I/O with `java.nio.file`
- Input validation and exception handling

## Future Improvements

- Monthly budget limits with warnings
- Edit an existing expense
- Export a monthly report
- Swing or JavaFX GUI
- Store data in a database using JDBC

## License

Free to use for learning.
