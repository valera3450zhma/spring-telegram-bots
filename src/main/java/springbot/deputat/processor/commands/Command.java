package springbot.deputat.processor.commands;

public enum Command {
    DEPUTAT("/deputat", "меню депутата"),
    ADMIN("/admin", "меню для адмінів бота"),
    GET_USER("/get_user", "команда для адмінів шоб здеанонити вашу сраку");

    private final String trigger;
    private final String description;

    public String getTrigger() {
        return trigger;
    }

    public String getDescription() {
        return description;
    }

    Command(String trigger, String description) {
        this.trigger = trigger;
        this.description = description;
    }
}
