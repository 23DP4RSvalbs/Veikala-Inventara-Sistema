package lv.rvt.tools;

import java.util.ArrayList;
import java.util.List;

public class CsvHelper {
    // Sadala CSV rindu atsevišķos elementos, ņemot vērā pēdiņās iekļautos laukus
    public static String[] parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return new String[0];
        }

        List<String> tokens = new ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;
        
        // Apstrādā katru rindu pa vienam simbolam
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                // Apstrādā dubultās pēdiņas
                if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    currentToken.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                tokens.add(currentToken.toString().trim());
                currentToken = new StringBuilder();
            } else {
                currentToken.append(c);
            }
        }
        
        // Pievieno pēdējo tokenu
        String finalToken = currentToken.toString().trim();
        if (!finalToken.isEmpty() || !tokens.isEmpty()) {
            tokens.add(finalToken);
        }
        
        return tokens.toArray(new String[0]);
    }

    // Pārveido vērtību CSV formātā, pievienojot pēdiņas, ja nepieciešams
    public static String escapeCsv(String value) {
        if (value == null) return "";
        
        value = value.trim();
        if (value.isEmpty()) return "\"\"";
        
        // Pārbauda, vai nepieciešams pievienot pēdiņas
        boolean needsQuoting = value.contains(",") || value.contains("\"") || 
                             value.contains("\n") || value.contains("\r") ||
                             value.startsWith(" ") || value.endsWith(" ");
        
        return needsQuoting ? "\"" + value.replace("\"", "\"\"") + "\"" : value;
    }
}