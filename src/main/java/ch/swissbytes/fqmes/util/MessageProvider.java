package ch.swissbytes.fqmes.util;

import javax.faces.context.FacesContext;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Created by alvaro on 9/15/14.
 */
public class MessageProvider {

    private ResourceBundle bundle;

    public MessageProvider(){
    }

    public ResourceBundle getBundle() {
        if (bundle == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            bundle = context.getApplication().getResourceBundle(context, "msg");
        }
        return bundle;
    }

    public String getValue(String key) {
        String result = null;
        try {
            result = getBundle().getString(key);
        } catch (MissingResourceException mre) {
            result = "???" + key + "??? not found";
        }
        return result;
    }
}
