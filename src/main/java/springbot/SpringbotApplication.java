package springbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import springbot.deputat.DeputatBot;
import springbot.deputat.config.BotConfig;

@SpringBootApplication
public class SpringbotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbotApplication.class, args);
	}

}
