package me.kimovoid.betaqol.feature.itemdurability;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.locale.LanguageManager;

public class ItemDurability {

    public static boolean debugItemDurability = false;

    public static void renderDetailedTooltip(Screen screen, TextRenderer tr, ItemStack stack, int x, int y) {
        String name = LanguageManager.getInstance().translateName(stack.getTranslationKey()).trim()
                + String.format(" (#%s%s)", stack.itemId, stack.getMetadata() != 0 && !stack.isDamaged() ? "/" + stack.getMetadata() : "");
        String dur = !stack.isDamaged() ? "" : String.format("Durability: %s/%s", stack.getMaxDamage() - stack.getDamage(), stack.getMaxDamage());

        int w = Math.max(tr.getWidth(name), tr.getWidth(dur));
        int h = stack.isDamaged() ? 20 : 8;

        screen.fillGradient(x - 3, y - 3, x + 3 + w, y + 3 + h, -1073741824, -1073741824);
        tr.drawWithShadow(name, x, y, -1);
        if (stack.isDamaged()) {
            tr.drawWithShadow(dur, x, y + 12, 11842740);
        }
    }
}
