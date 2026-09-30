package expensetracker;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Saves and loads expenses as a simple CSV file so data survives between runs. */
public class FileStorage {
    private static final String HEADER = "id,date,category,description,amount";
    private final Path path;

    public FileStorage(String fileName) {
        this.path = Path.of(fileName);
    }

    public List<Expense> load() throws IOException {
        List<Expense> list = new ArrayList<>();
        if (!Files.exists(path)) {
            return list;
        }
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int i = 1; i < lines.size(); i++) { // skip header
            String line = lines.get(i).trim();
            if (line.isEmpty()) continue;
            try {
                List<String> f = parseCsvLine(line);
                list.add(new Expense(
                        Integer.parseInt(f.get(0)),
                        LocalDate.parse(f.get(1)),
                        f.get(2),
                        f.get(3),
                        Double.parseDouble(f.get(4))));
            } catch (RuntimeException ex) {
                System.out.println("Skipping bad line " + (i + 1) + " in " + path);
            }
        }
        return list;
    }

    public void save(List<Expense> expenses) throws IOException {
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Expense e : expenses) {
            lines.add(e.getId() + "," + e.getDate() + "," + quote(e.getCategory())
                    + "," + quote(e.getDescription()) + "," + e.getAmount());
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /** Wraps text in quotes so commas inside descriptions don't break the CSV. */
    private static String quote(String text) {
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }

    private static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"' && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else if (c == '"') {
                    inQuotes = false;
                } else {
                    current.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
            } else if (c == ',') {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields;
    }
}
