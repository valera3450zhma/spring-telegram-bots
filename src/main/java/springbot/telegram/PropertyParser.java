package springbot.telegram;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

public class PropertyParser {

    private static final Properties propertiesFile = new Properties();

    static {
        try {
            propertiesFile.load(new FileInputStream("src/main/resources/resources.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getList(String propertyPath) {
        List<String> properties = new ArrayList<>();
        String value = "";
        for (int i = 0; (value = propertiesFile.getProperty(String.format("%s.%d", propertyPath, i+1))) != null; i++) {
            properties.add(value);
        }
        return properties;
    }

    public static String getRandom(String propertyPath) {
        List<String> list = getList(propertyPath);
        return list.get(ThreadLocalRandom.current().nextInt(list.size()));
    }

}
