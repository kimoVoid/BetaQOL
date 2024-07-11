package me.kimovoid.betaqol.feature.fov;

import net.minecraft.locale.LanguageManager;

public class FOVOption {

    public static float fov = 0.5F;

    public static int getFovInDegrees() {
        return Math.round(30.0f + fov * 80.0f);
    }

    public static String getLocalizedFov() {
        LanguageManager lang = LanguageManager.getInstance();
        if (getFovInDegrees() == 110) {
            return String.format("%s: %s", lang.translate("options.fov"), lang.translate("options.fov_max"));
        } else if (getFovInDegrees() == 70) {
            return String.format("%s: %s", lang.translate("options.fov"), lang.translate("options.fov_normal"));
        } else {
            return String.format("%s: %s", lang.translate("options.fov"), getFovInDegrees());
        }
    }
}
