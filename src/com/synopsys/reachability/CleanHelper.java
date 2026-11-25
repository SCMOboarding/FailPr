package com.synopsys.reachability;

/** Pure helper with no user input, no I/O and no sinks: nothing for a security scan to report. */
public final class CleanHelper {

    private CleanHelper() {
    }

    public static int add(int first, int second) {
        return first + second;
    }
}
