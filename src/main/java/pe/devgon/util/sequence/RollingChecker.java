package pe.devgon.util.sequence;

import pe.devgon.util.Dates;
import pe.devgon.functional.util.Pad;

public class RollingChecker {
    private final String dateFormat;
    private String currentDate;

    public RollingChecker(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isRollOver() {
        String today = Dates.current(dateFormat);
        if (!today.equals(currentDate)) {
            currentDate = today;
            return true;
        }
        return false;
    }

    public String pad(String value, int size) {
        return Pad.LEFT_ZERO.getFunction(size).apply(value);
    }
}
