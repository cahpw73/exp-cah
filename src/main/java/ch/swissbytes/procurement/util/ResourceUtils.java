package ch.swissbytes.procurement.util;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Created by Christian on 21/05/2015.
 */
public class ResourceUtils implements Serializable {

    public ResourceUtils() {
    }

    public String getResource(String fileName) {
        String result = "";
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getResourcePath(String fileName) {
        String result = "";
        ClassLoader classLoader = getClass().getClassLoader();
        result = classLoader.getResource(fileName).getPath();
        return result;
    }

    public InputStream getResourceAsStream(String fileName) {
        InputStream result = null;
        ClassLoader classLoader = getClass().getClassLoader();
        result = classLoader.getResourceAsStream(fileName);
        return result;
    }

}
