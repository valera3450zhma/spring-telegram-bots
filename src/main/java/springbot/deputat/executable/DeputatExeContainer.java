package springbot.deputat.executable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springbot.telegram.Executable;
import springbot.telegram.ExecutableNotFoundException;
import springbot.telegram.ExecutablesContainer;

import java.util.Map;

@Service
public class DeputatExeContainer implements ExecutablesContainer {

    private final Map<String, DeputatExecutable> executables;

    @Autowired
    public DeputatExeContainer(Map<String, DeputatExecutable> executables) {
        this.executables = executables;
    }

    public Executable getExecutable(String name) throws ExecutableNotFoundException {
        if (executables.containsKey(name)) {
            return executables.get(name);
        }
        throw new ExecutableNotFoundException(name);
    }

}
