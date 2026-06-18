package jacob.autofarm;

import jacob.autofarm.manager.ConfigManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import static jacob.autofarm.AutoFarm.enabled;

public class AutoFarmMenu extends Screen {
    private SliderWidget healthSlider;
    private SliderWidget swingDelaySlider;
    private SliderWidget eatHungerSlider;
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private ButtonWidget toggleButton;
    private ButtonWidget passiveMobToggle;
    public static boolean hostileMob = true;

    protected AutoFarmMenu() {
        super(Text.of("AutoFarm Settings"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Logout on low health
        this.healthSlider = new SliderWidget(centerX - 100, centerY - 50, 200, 20, Text.of("Logout Health"), Config.logoutHealth / 20.0F) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(Text.of("Logout Health: " + (int)(this.value * 20)));
            }

            @Override
            protected void applyValue() {
                Config.logoutHealth = (int)Math.floor(this.value * 20);
                checkModState();
            }

        };
        this.addDrawableChild(healthSlider);


        // AutoSwing Delay
        this.swingDelaySlider = new SliderWidget(centerX - 100, centerY - 20, 200, 20, Text.of("Swing Delay"), Config.swingDelay / 50.0F) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                if (this.value == 0){
                    setMessage(Text.of("Swing Delay: §4OFF"));
                } else{
                    setMessage(Text.of("Swing Delay: " + (int)(this.value * 50)));
                }
            }

            @Override
            protected void applyValue() {
                Config.swingDelay = (int)(this.value * 50);
            }
        };
        this.addDrawableChild(swingDelaySlider);

        // AutoEat Hunger
        this.eatHungerSlider = new SliderWidget(centerX - 100, centerY + 10, 200, 20, Text.of("Eat Hunger"), Config.eatHunger / 20.0F) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(Text.of("Eat Hunger: " + (int)(this.value * 20)));
            }

            @Override
            protected void applyValue() {
                Config.eatHunger = (int)Math.floor(this.value * 20);
                checkModState();
            }
        };
        this.addDrawableChild(eatHungerSlider);

        this.toggleButton = ButtonWidget.builder(getToggleText(), (button) -> {
                    toggleMod();
                    button.setMessage(getToggleText());
                })
                .dimensions(centerX - 50, centerY + 40, 110, 20)
                .build();
        this.addDrawableChild(toggleButton);


        this.passiveMobToggle = ButtonWidget.builder(Text.of("Ignore Passive: " + (hostileMob ? "§aON" : "§cOFF")), (button) ->
                {
                    hostileMob = !hostileMob;
                    button.setMessage(Text.of("Ignore Passive: " + (hostileMob ? "§aON" : "§cOFF")));
                })
                .dimensions(centerX - 50, centerY + 70, 110, 20)
                .build();
        this.addDrawableChild(passiveMobToggle);

        ButtonWidget closeButton = ButtonWidget.builder(Text.of("Close"), (button) -> client.setScreen(null))
                .dimensions(centerX - 50, centerY + 100, 110, 20)
                .build();
        this.addDrawableChild(closeButton);

    }

    @Override
    public void removed() {
        super.removed();
        ConfigManager.saveConfig();
    }




    private void checkModState() {
        if (Config.logoutHealth == 0 && Config.swingDelay == 0 && Config.eatHunger == 0) {
            enabled = false;
            client.player.sendMessage(Text.of("AutoFarm disabled due to slider values being 0."), true);
        }
    }

    private Text getToggleText() {
        return Text.of("AutoFarm: " + (enabled ? "§aON" : "§cOFF"));
    }



    // Toggle the mod state and update the button text
    public static void toggleMod() {
        enabled = !enabled;
        if (enabled) {
            client.player.sendMessage(Text.of("AutoFarm enabled"), true);
        } else {
            client.player.sendMessage(Text.of("AutoFarm disabled"), true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        long time = System.currentTimeMillis();

        // watermark lol
        float hue = (time % 5000L) / 5000.0f;
        int rgb = java.awt.Color.HSBtoRGB(hue, 0.5f, 1.0f);

        context.drawText(this.textRenderer, "AutoFarm Menu - By JacobTheIdiot", 5, 5, rgb, false);
    }

}
