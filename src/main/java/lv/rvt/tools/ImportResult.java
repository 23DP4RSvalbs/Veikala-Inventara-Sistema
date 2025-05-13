package lv.rvt.tools;

import java.util.ArrayList;
import java.util.List;

public class ImportResult {
    private int totalCount;
    private int validCount;
    private List<String> errors;

    public ImportResult() {
        this.errors = new ArrayList<>();
        this.totalCount = 0;
        this.validCount = 0;
    }

    public void incrementTotal() {
        totalCount++;
    }

    public void incrementValid() {
        validCount++;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getValidCount() {
        return validCount;
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public void addError(String error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}
