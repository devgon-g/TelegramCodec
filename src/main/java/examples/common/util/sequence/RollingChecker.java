package examples.common.util.sequence;

import examples.common.lang.Dates;

public class RollingChecker {
    String lastUpdated;
    String rollingDatePattern;

    public RollingChecker(String rollingDatePattern) {
        this.rollingDatePattern = rollingDatePattern;
        lastUpdated = Dates.current(rollingDatePattern);
    }

    public boolean isNeedRolling(String seed) {
        try {
            if(lastUpdated.equals(seed))
                return false;
            return true;
        } finally {
            lastUpdated = seed;
        }
    }
}
