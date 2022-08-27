package springbot.telegram.executable;

import springbot.telegram.exceptions.ExecutableNotFoundException;

import java.util.List;

public abstract class ExecutablesContainer {

    protected List<? extends Executable> executables;

    public Executable getExecutable(String trigger) throws ExecutableNotFoundException {
        for (Executable executable : executables) {
            if (executable.getTriggers().contains(trigger)) {
                return executable;
            }
        }

        throw new ExecutableNotFoundException(trigger);
    }
}
