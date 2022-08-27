package springbot.deputat.processor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springbot.telegram.executable.ExecutablesContainer;

import java.util.List;

@Service
public class DeputatExeContainer extends ExecutablesContainer {

    @Autowired
    public DeputatExeContainer(List<DeputatExecutable> executables) {
        this.executables = executables;
    }

}
