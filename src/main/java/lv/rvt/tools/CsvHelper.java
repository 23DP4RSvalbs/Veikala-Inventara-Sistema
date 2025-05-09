package lv.rvt.tools;

public class CsvHelper {
    public static String[] parseLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return new String[0];
        }

        java.util.List<String> tokens = new java.util.ArrayList<>();
        StringBuilder currentToken = new StringBuilder();
        boolean inQuotes = false;
        boolean hasContent = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    // Handle escaped quote
                    currentToken.append('"');
                    i++;
                    hasContent = true;
                } else {
                    // Only toggle quotes if it's at start/end of field or preceded by comma
                    if (!hasContent || (i > 0 && line.charAt(i-1) == ',')) {
                        inQuotes = !inQuotes;
                    } else {
                        currentToken.append(c);
                    }
                }
                hasContent = true;
            } else if (c == ',' && !inQuotes) {
                tokens.add(currentToken.toString().trim());
                currentToken.setLength(0);
                hasContent = false;
            } else if (!inQuotes && Character.isWhitespace(c)) {
                // Skip whitespace outside quotes unless it's between content
                if (hasContent) {
                    currentToken.append(c);
                }
            } else {
                currentToken.append(c);
                hasContent = true;
            }
        }
        
        // Add final token
        String finalToken = currentToken.toString().trim();
        if (!finalToken.isEmpty() || !tokens.isEmpty()) {
            tokens.add(finalToken);
        }
        
        return tokens.toArray(new String[0]);
    }

    public static String escapeCsv(String value) {
        if (value == null) {
            return "";
        }

        value = value.trim();
        
        // Always quote if empty string to distinguish from null
        if (value.isEmpty()) {
            return "\"\"";
        }

        // Quote if contains any special characters
        if (value.contains(",") || value.contains("\"") || 
            value.contains("\n") || value.contains("\r") ||
            value.startsWith(" ") || value.endsWith(" ")) {
            
            // Escape quotes by doubling them
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        
        return value;
    }
}