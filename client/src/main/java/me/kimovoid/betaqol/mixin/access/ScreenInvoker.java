package me.kimovoid.betaqol.mixin.access;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * This is a port of MojangFix for Babric.
 * All credits to js6pak and everyone involved in that project.
 * <a href="https://github.com/js6pak/mojangfix">View here</a>
 */
@Mixin(Screen.class)
public interface ScreenInvoker {

    @Invoker
    void callButtonClicked(ButtonWidget button);
}