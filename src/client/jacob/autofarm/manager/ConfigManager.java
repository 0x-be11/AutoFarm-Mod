package jacob.autofarm.manager;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jacob.autofarm.AutoFarmMenu;
import jacob.autofarm.Config;
import net.fabricmc.loader.api.FabricLoader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    static Path configDir = FabricLoader.getInstance().getConfigDir().resolve("autofarm");
    static Path configFile = configDir.resolve("settings.json");

    public static void init() throws IOException {

        Files.createDirectories(configDir);

        if (!Files.exists(configFile)){
            Files.writeString(configFile, "{}");
            saveConfig();
        }
        //saveConfig(); //calling saveconfig for testing
        loadConfig();
    }

    public static void saveConfig(){
        Map<String, Object> data = new HashMap<>();

        data.put("logoutHealth", Config.logoutHealth);
        data.put("swingDelay", Config.swingDelay);
        data.put("eatHunger", Config.eatHunger);
        data.put("attackPassive", AutoFarmMenu.hostileMob);

        try (FileWriter writer = new FileWriter(String.valueOf(configFile))){
            new GsonBuilder().setPrettyPrinting().create().toJson(data, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void loadConfig(){
        try (FileReader reader = new FileReader(String.valueOf(configFile))){
            JsonObject json = new JsonParser().parse(reader).getAsJsonObject();

            int logoutHealth = Config.logoutHealth;
            int swingDelay = Config.swingDelay;
            int eatHunger = Config.eatHunger;
            boolean attackPassive = AutoFarmMenu.hostileMob;

            if (json.has("logoutHealth"))
                logoutHealth = json.get("logoutHealth").getAsInt();

            if (json.has("swingDelay"))
                swingDelay = json.get("swingDelay").getAsInt();

            if (json.has("eatHunger"))
                eatHunger = json.get("eatHunger").getAsInt();

            if (json.has("attackPassive"))
                attackPassive = json.get("attackPassive").getAsBoolean();

            Config.logoutHealth = logoutHealth;
            Config.swingDelay = swingDelay;
            Config.eatHunger = eatHunger;

            AutoFarmMenu.hostileMob = attackPassive;

        } catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
