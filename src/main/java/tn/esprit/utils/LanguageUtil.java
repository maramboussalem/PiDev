package tn.esprit.utils;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageUtil {
    private static ObjectProperty<Locale> languageTrigger = new SimpleObjectProperty<>();
    private static ResourceBundle bundle;

    static {
        Locale defaultLocale = new Locale("en");
        languageTrigger.set(defaultLocale);
        bundle = ResourceBundle.getBundle("messages", defaultLocale);

        // Add a listener to update bundle whenever language changes
        languageTrigger.addListener((obs, oldLocale, newLocale) -> {
            bundle = ResourceBundle.getBundle("messages", newLocale);
        });
    }

    public static void setLocale(String languageCode) {
        Locale newLocale = new Locale(languageCode);
        languageTrigger.set(newLocale); // This will trigger bundle reload through listener
    }

    public static String getMessage(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!"; // fallback if key not found
        }
    }

    public static String getCurrentLanguage() {
        return languageTrigger.get().getLanguage();
    }

    public static ObjectProperty<Locale> languageTriggerProperty() {
        return languageTrigger;
    }
}
