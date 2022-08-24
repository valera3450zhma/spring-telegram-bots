package springbot.telegram;

public interface ExecutablesContainer {
    Executable getExecutable(String name) throws ExecutableNotFoundException ;
}
