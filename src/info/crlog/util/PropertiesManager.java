package info.crlog.util;

import info.crlog.interfaces.Constants;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *This utility class is a wrapper for the builting properties methods
 * which provides quick access to  methods
 * that makes it easy to get settings from a specified properties file
 * @author Courtney
 */
public class PropertiesManager implements Constants{

    private String file;
    private Properties properties = new Properties();

    /**
     * This constructor allows the file name to be specified without the
     * .properties extension.
     * filename can be a relevant path to the working directory or an
     * absolute path to the file.
     * @important Do not add the .properties extension to the filename/path
     *             only the filename
     * @param fileName
     */
    public PropertiesManager(String fileName) {
        this.file = fileName;
        try {
            properties.load(new FileInputStream(fileName + ".properties"));
        } catch (IOException ex) {
               Logger.getLogger(getClass().getName()).log(Level.WARNING,fileName+".properties file not found");
        }
    }

    /**
     * Commit any changes to the named file
     */
    private void saveProperties() {
        try {
            this.properties.store(new FileOutputStream(this.file + ".properties"), null);
        } catch (IOException ex) {
        }
    }

    /**
     * Update or create a new property in the file specified in the constructor.
     * @param property - the name of the property to set or update
     * @param value - the value to assign to the property
     */
    public void setProperty(String property, String value) {
        this.properties.setProperty(property, value);
        saveProperties();
    }

    /**
     * Return the string value of the specified property
     * @param property - the name of the property to get
     * @return - the string assigned to the said property
     */
    public String getProperty(String property) {
        return this.properties.getProperty(property);
    }

    /**
     * Allows an array structure to be defined within a properties file
     * The concept being that an array has a fixed size so the property
     * must define that size in the format propertyName[sizeOfArray] = integer
     * every array element is then defined as propertyName[int]
     * this mechanism sticks to standard java 0 based index so the first
     * item is assummed to be propertyName[0] and incremented by one per item
     * @param property
     * @return - a string array with all the items in the property file
     */
    public String[] getArrayProperty(String property) {
        String x = this.properties.getProperty(property + "[total]");
        int total = 0;

        try {
            total = Integer.parseInt(x);
        } catch (Exception e) {
            if(Boolean.parseBoolean(props.getProperty("verbose")))
            System.out.println("Tried to access a property array which does not"
                    + " specify its size using propertyName[IntegerSize]"
                    + " or the property does not exist i.e \n"+property + "[total]= int does not exist");
        }
        String[] returnProperties = null;
        if (total > 0) {
            returnProperties = new String[total];
            String var = null;
            for (int i = 0; i < total; i++) {
                var = this.properties.getProperty(property + "[" + i + "]");
                if (!var.isEmpty()) {
                    returnProperties[i] = var;
                    var = null;
                }
            }
        }
        return returnProperties;
    }
}
