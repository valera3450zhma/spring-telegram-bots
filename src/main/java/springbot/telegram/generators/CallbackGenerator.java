package springbot.telegram.generators;

import springbot.telegram.Button;

public class CallbackGenerator {

    public static void setUserId(Button[] buttons, Long userId) {
        for (Button button : buttons) {
            button.setCallbackData(button.getCallbackData().replace("?", userId.toString()));
        }
    }

}
