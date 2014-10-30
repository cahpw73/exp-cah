package ch.swissbytes.fqmes.util;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Created by christian on 27/10/14.
 */
@SessionScoped
@Named
public class LanguagePreference implements Serializable {

    private static final Logger log= Logger.getLogger(LanguagePreference.class.getName());

    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        log.info("setLanguage: "+ language);
        this.language = language;
    }
}
