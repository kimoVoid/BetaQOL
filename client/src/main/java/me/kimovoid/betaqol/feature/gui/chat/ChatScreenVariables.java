package me.kimovoid.betaqol.feature.gui.chat;

import net.minecraft.client.gui.widget.TextFieldWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * These variables can be accessed and modified by other
 * mods to support Beta QOL's chat implementation.
 */
public class ChatScreenVariables {
    public static TextFieldWidget textField;
    public static String initialMessage = "";
    public static int chatMessageLength = 100;
    public static int chatHistoryPosition;
    public static final List<String> CHAT_HISTORY = new ArrayList<>();
}