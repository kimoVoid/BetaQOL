package me.kimovoid.betaqol.feature.debugscreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.render.TextRenderer;
import net.minecraft.client.render.Window;
import net.minecraft.item.ItemStack;
import net.minecraft.locale.LanguageManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.HitResult;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DebugScreen {
    
    public static final DebugScreen INSTANCE = new DebugScreen();
    private String cpuInfo;

    public void init() {
        try {
            CentralProcessor cpu = new SystemInfo().getHardware().getProcessor();
            this.cpuInfo = String.format("%dx %s", cpu.getLogicalProcessorCount(), cpu.getProcessorIdentifier().getName()).replaceAll("\\s+", " ");
        } catch (Throwable ignored) {}
    }

    public void renderLeftSideDebug(Minecraft mc) {
        int x = MathHelper.floor(mc.player.x);
        int y = MathHelper.floor(mc.player.y);
        int z = MathHelper.floor(mc.player.z);
        int chunkX = x >> 4;
        int chunkZ = z >> 4;
        long time = MathHelper.floor((double)(mc.world.getTime() / 24000L));
        String biome = mc.world.getBiomeSource().getBiome(x, z).name;
        float angle = (mc.world.getTimeOfDay(1.0F) * 24.0F + 12.0F) % 24.0F;
        int h = (int)Math.floor(angle);
        int m = (int)Math.floor(angle * 60.0F) - h * 60;
        int s = (int)Math.floor(angle * 3600.0F) - h * 3600 - m * 60;
        int f = (MathHelper.floor((double)(mc.player.yaw * 4.0f / 360.0f) + 0.5) & 3);
        String facing = f == 0 ? "west (Towards positive Z)"
                : f == 1 ? "north (Towards negative X)"
                : f == 2 ? "east (Towards negative Z)"
                : "south (Towards positive X)";

        /* Add lines */
        List<String> lines = new ArrayList<>();
        lines.add("Minecraft Beta 1.7.3 (b1.7.3/fabric/QOL)");
        lines.add(mc.fpsDebugString);
        lines.add(mc.getChunkDebugInfo());
        lines.add(mc.getEntityDebugInfo());
        lines.add(mc.getParticleDebugInfo());
        lines.add(mc.getChunkSourceDebugInfo());
        lines.add("");
        lines.add(String.format("XYZ: %.3f / %.3f / %.3f", mc.player.x, mc.player.y, mc.player.z));
        lines.add(String.format("Block: %s %s %s", x, y, z));
        lines.add(String.format("Facing: %s (%.1f / %.1f)", facing, this.wrapDegrees(mc.player.yaw), this.wrapDegrees(mc.player.pitch)));
        lines.add("Chunk: " + String.format("%s, %s [%s, %s]", chunkX, chunkZ, x & 15, z & 15));
        lines.add("");
        lines.add("Light: " + mc.world.getLight(x, y, z));
        lines.add("Biome: " + (biome == null ? "Unknown" : biome));
        lines.add("Seed: " + mc.world.getSeed());
        lines.add("Day: " + time);
        lines.add("Time: " + String.format("%02d:%02d:%02d", h, m, s));
        lines.add("Slime: " + this.isSlimeChunk(mc.world.getSeed(), mc.player.chunkX, mc.player.chunkZ));
        if (mc.world != null && mc.crosshairTarget != null && mc.crosshairTarget.type == HitResult.Type.BLOCK) {
            lines.add(String.format("Looking at: %s %s %s", mc.crosshairTarget.x, mc.crosshairTarget.y, mc.crosshairTarget.z));
        }

        /* Render lines */
        for (int i = 0; i < lines.size(); i++) {
            this.renderText(lines.get(i), 2, 2 + i * 9);
        }
    }

    public void renderRightSideDebug(Minecraft mc) {
        Window window = new Window(mc.options, mc.width, mc.height);
        int width = window.getWidth();
        TextRenderer tr = mc.textRenderer;

        long maxMem = Runtime.getRuntime().maxMemory();
        long totalMem = Runtime.getRuntime().totalMemory();
        long freeMem = Runtime.getRuntime().freeMemory();
        long usedMem = totalMem - freeMem;

        /* Add lines */
        List<String> lines = new ArrayList<>();
        lines.add(String.format("Java: %s %sbit", System.getProperty("java.version"), System.getProperty("sun.arch.data.model")));
        lines.add("Mem: " + usedMem * 100L / maxMem + "% (" + usedMem / 1024L / 1024L + "MB) of " + maxMem / 1024L / 1024L + "MB");
        lines.add("Allocated: " + totalMem * 100L / maxMem + "% (" + totalMem / 1024L / 1024L + "MB)");
        lines.add("");
        if (this.cpuInfo != null) {
            lines.add(String.format("CPU: %s", this.cpuInfo));
            lines.add("");
        }
        lines.add(String.format("Display: %dx%d (%s)", Display.getWidth(), Display.getHeight(), GL11.glGetString(7936)));
        lines.add(GL11.glGetString(7937));
        lines.add(GL11.glGetString(7938));
        if (mc.world != null && mc.crosshairTarget != null && mc.crosshairTarget.type == HitResult.Type.BLOCK) {
            int id = mc.world.getBlock(mc.crosshairTarget.x, mc.crosshairTarget.y, mc.crosshairTarget.z);
            int meta = mc.world.getBlockMetadata(mc.crosshairTarget.x, mc.crosshairTarget.y, mc.crosshairTarget.z);
            ItemStack stack = new ItemStack(id, 1, meta);
            String name = LanguageManager.getInstance().translateName(stack.getTranslationKey()).trim();

            lines.add("");
            lines.add(String.format("%s (#%s%s)", name.isEmpty() ? stack.getTranslationKey() : name, stack.itemId, stack.getMetadata() != 0 && !stack.isDamaged() ? "/" + stack.getMetadata() : ""));
        }

        /* Render lines */
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            this.renderText(line, width - tr.getWidth(line) - 1, 2 + i * 9);
        }
    }

    private boolean isSlimeChunk(long seed, int x, int z) {
        Random rnd = new Random(
                seed +
                        (int) (x * x * 0x4c1906) +
                        (int) (x * 0x5ac0db) +
                        (int) (z * z) * 0x4307a7L +
                        (int) (z * 0x5f24f) ^ 0x3ad8025fL
        );

        return rnd.nextInt(10) == 0;
    }

    private void renderText(String text, int x, int y) {
        if (text.isEmpty()) {
            return;
        }
        Minecraft.INSTANCE.gui.fillGradient(x - 1, y - 1, x + Minecraft.INSTANCE.textRenderer.getWidth(text), y + 8, -1873784752, -1873784752);
        Minecraft.INSTANCE.textRenderer.draw(text, x, y, 0xE0E0E0);
    }

    private float wrapDegrees(float degrees) {
        if ((degrees %= 360.0f) >= 180.0f) {
            degrees -= 360.0f;
        }
        if (degrees < -180.0f) {
            degrees += 360.0f;
        }
        return degrees;
    }
}