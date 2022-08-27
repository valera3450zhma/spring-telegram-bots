package springbot.telegram.exceptions;

public class ExecutableNotFoundException extends Exception {
    public ExecutableNotFoundException(String executableName) {
        super(executableName);
    }
}
