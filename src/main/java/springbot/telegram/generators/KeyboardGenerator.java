package springbot.telegram.generators;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import springbot.telegram.Button;

import java.util.ArrayList;
import java.util.List;

public class KeyboardGenerator {

    public static InlineKeyboardMarkup generateInline(Button[] buttons) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        int buttonsCount = buttons.length;
        int buttonsIt = 0;
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = null;

        while (buttonsIt < buttonsCount) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttons[buttonsIt].getLabel());
            button.setCallbackData(buttons[buttonsIt].getCallbackData());
            if ((buttonsIt & 1) == 0) { // odd-iteration
                row = new ArrayList<>();
                rows.add(row);
            }
            row.add(button);
            buttonsIt++;
        }

        markup.setKeyboard(rows);
        return markup;
    }

}
