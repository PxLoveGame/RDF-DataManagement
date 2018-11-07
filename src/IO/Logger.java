package IO;

public class Logger {

    private static boolean enabled = false;

    static void setLogging(boolean log){
        enabled = log;
    }

    static void log(String msg){
        if (enabled){
            System.out.println(msg);
        }
    }

    public static void logError(String msg){
        if (enabled){
            System.err.println(msg);
        }
    }

    public static boolean logEnabled() {
        return enabled;
    }
}
