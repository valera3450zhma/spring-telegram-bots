package springbot.telegram.generators;

import springbot.telegram.Button;

import java.util.List;

public class CallbackGenerator {

    public static void setUserId(List<Button> buttons, Long userId) {
        for (Button button : buttons) {
            button.setCallbackData(button.getCallbackData().replace("?", userId.toString()));
        }
    }

}
