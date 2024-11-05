package me.kimovoid.betaqol.config;

import io.github.axolotlclient.AxolotlClientConfig.api.AxolotlClientConfig;
import io.github.axolotlclient.AxolotlClientConfig.api.manager.ConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.api.options.OptionCategory;
import io.github.axolotlclient.AxolotlClientConfig.impl.managers.JsonConfigManager;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.BooleanOption;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.StringOption;
import net.fabricmc.loader.api.FabricLoader;

public class Config {

    /* Root configs */
    public final BooleanOption showLabel = new BooleanOption(
            "always_show_label",
            true
    );

    public final BooleanOption modernF3 = new BooleanOption(
            "modern_F3",
            true
    );

    public final BooleanOption disableChart = new BooleanOption(
            "disable_F3_graph",
            true
    );

    public final BooleanOption frontPerspective = new BooleanOption(
            "front_F5",
            true
    );

    public final BooleanOption screenshotToClipboard = new BooleanOption(
            "clipboard_screenshots",
            false
    );

    /* Inventory Tweaks configs */
    public final BooleanOption dragGraphics = new BooleanOption(
            "drag_graphics",
            true
    );

    public final BooleanOption leftClickDrag = new BooleanOption(
            "dragging",
            true
    );

    public final BooleanOption preferShiftLMB = new BooleanOption(
            "prefer_shift_lmb",
            true
    );

    public final BooleanOption preferShiftRMB = new BooleanOption(
            "prefer_shift_rmb",
            true
    );

    public final BooleanOption dropKeyInv = new BooleanOption(
            "drop_key_inv",
            true
    );

    public final BooleanOption ctrlDropStack = new BooleanOption(
            "drop_stack_key",
            true
    );

    public final BooleanOption hotkeySwap = new BooleanOption(
            "hotkey_swap",
            true
    );
    
    /* Mouse Tweaks configs */
    public final BooleanOption shiftClickAnyLMB = new BooleanOption(
            "shift_click_any_lmb",
            true
    );

    public final BooleanOption tweakLMBShiftClick = new BooleanOption(
            "tweak_shift_lmb",
            true
    );

    public final BooleanOption tweakRMB = new BooleanOption(
            "tweak_rmb",
            true
    );

    public final BooleanOption tweakLMBPickUp = new BooleanOption(
            "lmb_pickup",
            true
    );

    public final BooleanOption scrollWheelTweaks = new BooleanOption(
            "scroll_tweaks",
            true
    );

    public final BooleanOption invertScrollCursorSlotDirection = new BooleanOption(
            "scroll_invert_direction",
            false
    );

    /* Gameplay configs */
    public final StringOption deathScreenMsg = new StringOption(
            "death_screen_msg",
            "Score: §e{score}"
    );

    public final BooleanOption enableMpClickMining = new BooleanOption(
            "mp_click_mining",
            false
    );

    public final BooleanOption texturePackButton = new BooleanOption(
            "texture_pack_button",
            true
    );

    public final BooleanOption alwaysPlayMusic = new BooleanOption(
            "always_play_music",
            false
    );

    public Config init() {
        OptionCategory root = OptionCategory.create("betaqol");
        OptionCategory inventory = OptionCategory.create("inventory");
        OptionCategory mouse = OptionCategory.create("mouse");
        OptionCategory gameplay = OptionCategory.create("gameplay");

        root.add(showLabel,
                modernF3,
                disableChart,
                frontPerspective,
                screenshotToClipboard);

        inventory.add(dragGraphics,
                leftClickDrag,
                preferShiftLMB,
                preferShiftRMB,
                dropKeyInv,
                ctrlDropStack,
                hotkeySwap);

        mouse.add(shiftClickAnyLMB,
                tweakLMBShiftClick,
                tweakRMB,
                tweakLMBPickUp,
                scrollWheelTweaks,
                invertScrollCursorSlotDirection);

        gameplay.add(deathScreenMsg,
                enableMpClickMining,
                texturePackButton,
                alwaysPlayMusic);

        inventory.add(mouse);
        root.add(inventory);
        root.add(gameplay);

        AxolotlClientConfig conf = AxolotlClientConfig.getInstance();
        ConfigManager manager = new JsonConfigManager(FabricLoader.getInstance().getConfigDir().resolve("betaqol.json"), root);
        manager.load();
        conf.register(manager);

        return this;
    }
}
