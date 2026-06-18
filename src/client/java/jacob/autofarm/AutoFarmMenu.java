package jacob.autofarm;

import jacob.autofarm.manager.ConfigManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static jacob.autofarm.AutoFarm.enabled;

public class AutoFarmMenu extends Screen {
    private AbstractSliderButton healthSlider;
    private AbstractSliderButton swingDelaySlider;
    private AbstractSliderButton eatHungerSlider;
    private static final Minecraft client = Minecraft.getInstance();
    private Button toggleButton;
    private Button passiveMobToggle;
    public static boolean hostileMob = true;

    protected AutoFarmMenu() {
        super(Component.nullToEmpty("AutoFarm Settings"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Logout on low health
        this.healthSlider = new AbstractSliderButton(centerX - 100, centerY - 50, 200, 20, Component.nullToEmpty("Logout Health"), Config.logoutHealth / 20.0F) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(Component.nullToEmpty("Logout Health: " + (int)(this.value * 20)));
            }

            @Override
            protected void applyValue() {
                Config.logoutHealth = (int)Math.floor(this.value * 20);
                checkModState();
            }

        };
        this.addRenderableWidget(healthSlider);


        // AutoSwing Delay
        this.swingDelaySlider = new AbstractSliderButton(centerX - 100, centerY - 20, 200, 20, Component.nullToEmpty("Swing Delay"), Config.swingDelay / 50.0F) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                if (this.value == 0){
                    setMessage(Component.nullToEmpty("Swing Delay: §4OFF"));
                } else{
                    setMessage(Component.nullToEmpty("Swing Delay: " + (int)(this.value * 50)));
                }
            }

            @Override
            protected void applyValue() {
                Config.swingDelay = (int)(this.value * 50);
            }
        };
        this.addRenderableWidget(swingDelaySlider);

        // AutoEat Hunger
        this.eatHungerSlider = new AbstractSliderButton(centerX - 100, centerY + 10, 200, 20, Component.nullToEmpty("Eat Hunger"), Config.eatHunger / 20.0F) {
            {
                updateMessage();
            }
            @Override
            protected void updateMessage() {
                setMessage(Component.nullToEmpty("Eat Hunger: " + (int)(this.value * 20)));
            }

            @Override
            protected void applyValue() {
                Config.eatHunger = (int)Math.floor(this.value * 20);
                checkModState();
            }
        };
        this.addRenderableWidget(eatHungerSlider);

        this.toggleButton = Button.builder(getToggleText(), (button) -> {
                    toggleMod();
                    button.setMessage(getToggleText());
                })
                .bounds(centerX - 50, centerY + 40, 110, 20)
                .build();
        this.addRenderableWidget(toggleButton);


        this.passiveMobToggle = Button.builder(Component.nullToEmpty("Ignore Passive: " + (hostileMob ? "§aON" : "§cOFF")), (button) ->
                {
                    hostileMob = !hostileMob;
                    button.setMessage(Component.nullToEmpty("Ignore Passive: " + (hostileMob ? "§aON" : "§cOFF")));
                })
                .bounds(centerX - 50, centerY + 70, 110, 20)
                .build();
        this.addRenderableWidget(passiveMobToggle);

        Button closeButton = Button.builder(Component.nullToEmpty("Close"), (button) -> minecraft.setScreen(null))
                .bounds(centerX - 50, centerY + 100, 110, 20)
                .build();
        this.addRenderableWidget(closeButton);

    }

    @Override
    public void removed() {
        super.removed();
        ConfigManager.saveConfig();
    }




    private void checkModState() {
        if (Config.logoutHealth == 0 && Config.swingDelay == 0 && Config.eatHunger == 0) {
            enabled = false;
            minecraft.player.displayClientMessage(Component.nullToEmpty("AutoFarm disabled due to slider values being 0."), true);
        }
    }

    private Component getToggleText() {
        return Component.nullToEmpty("AutoFarm: " + (enabled ? "§aON" : "§cOFF"));
    }



    // Toggle the mod state and update the button text
    public static void toggleMod() {
        enabled = !enabled;
        if (enabled) {
            minecraft.player.displayClientMessage(Component.nullToEmpty("AutoFarm enabled"), true);
        } else {
            minecraft.player.displayClientMessage(Component.nullToEmpty("AutoFarm disabled"), true);
        }
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        long time = System.currentTimeMillis();

        // watermark lol
        float hue = (time % 5000L) / 5000.0f;
        int rgb = java.awt.Color.HSBtoRGB(hue, 0.5f, 1.0f);

        context.drawString(this.font, "AutoFarm Menu - By JacobTheIdiot", 5, 5, rgb, false);
    }

}
