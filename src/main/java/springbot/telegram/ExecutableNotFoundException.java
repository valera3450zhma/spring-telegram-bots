package springbot.telegram;

public class ExecutableNotFoundException extends Exception {
    public ExecutableNotFoundException(String name) {
        super(name);
    }
}
