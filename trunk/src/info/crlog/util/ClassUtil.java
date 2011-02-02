package info.crlog.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import info.crlog.interfaces.Constants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Courtney
 */
public class ClassUtil implements Constants {

    public static String[] readFile(String filename) throws IOException {
        FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> lines = new ArrayList<String>();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            lines.add(line);
        }
        bufferedReader.close();
        return lines.toArray(new String[lines.size()]);
    }

    public static boolean saveFile(String url, String data) throws IOException {
        BufferedWriter fileWriter = null;
        fileWriter = new BufferedWriter(new FileWriter(url));
        fileWriter.write(data);
        if (fileWriter != null) {
            fileWriter.close();
        }
        return true;
    }

    public static boolean createDirectory(String dir) {
        boolean done = (new File(dir)).mkdir();
        return done;
    }

    public static void copyPath(String old, String n) {
        //adopted from http://www.java-tips.org/java-se-tips/java.how-to-copy-a-directory-from-one-location-to-another-loc.html
        File src = new File(old);
        File des = new File(n);
        if (src.isDirectory()) {
            if (!des.exists()) {
                des.mkdir();
            }

            String[] children = src.list();
            for (int i = 0; i < children.length; i++) {
                copyPath(src + children[i],
                        des + children[i]);
            }
        } else {
            InputStream in = null;
            try {
                in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(des);
                // Copy the bits from instream to outstream
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(ClassUtil.class.getName()).log(Level.SEVERE, "IOException thrown in copyPath");
            } finally {
                if ((in == null) == false) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(ClassUtil.class.getName()).log(Level.SEVERE, "IOException thrown in copyPath");
                    }
                }
            }
        }
    }

    public static boolean deleteFileOrDirectory(String dir) {
        File path;
        try {
            path = new File(dir);
            if (path.exists()) {
                File[] files = path.listFiles();
                if (files == null) {
                    return path.delete();
                } else {
                    for (int i = 0; i < files.length; i++) {
                        if (files[i].isDirectory()) {
                            deleteFileOrDirectory(files[i].getPath());
                        } else {
                            files[i].delete();
                        }
                    }

                }

            }

        } catch (Exception e) {
            Logger.getLogger(ClassUtil.class.getName()).log(Level.SEVERE, "an error occured while trying to delete {0} message : \n{1}", new Object[]{dir, e.getMessage()});
            return false;
        }
        return path.delete();
    }

    /**
     * Parses the types for the java primitives boolean, byte, char, short, int, long, float, and double
     * uses boxing to return an object of the type if its not a primitive it checks
     * if a parameter is passed if so then it creates an object passing the parameters
     * into the constructor, otherwise it just returns an instance of the said object
     * For a char it returns only the first letter as a char
     * each time this is called by getParams one item is removed from the queue
     * therefore no need for a loob since getParam does that already
     * @return - an object of the type stored in the values property
     */
    public static Object strToType(String data) {
        if (data == null) {
            return null;
        }
        String[] temp = data.split(":");
        String value = null;
        String type = null;
        if (temp.length > 1)//if no value is passed set it to null
        {
            value = temp[1];

        } else {
            value = null;

        }
        type = temp[0];
        try {

            if (type.equals("String")) {
                return value;
            } else if (type.equals("int")) {
                return Integer.parseInt(value);
            } else if (type.equals("boolean")) {
                return Boolean.parseBoolean(value);
            } else if (type.equals("byte")) {
                return Byte.parseByte(value);
            } else if (type.equals("char")) {
                return value.charAt(0);
            } else if (type.equals("double")) {
                return Double.parseDouble(value);
            } else if (type.equals("short")) {
                return Short.parseShort(value);
            } else if (type.equals("long")) {
                return Long.parseLong(value);
            } else if (type.equals("float")) {
                return Float.parseFloat(value);
            } else {
                if (value == null) {
                    //this is the tricky one. If the value is null then we can simple
                    //return a new instance as is. However, if its not we need to pass
                    //the value and its type in to the contructor
                    return Class.forName(type).newInstance();
                } else {
                    /**
                     * As it turns out unless the Type to be passed can be created without
                     * constructor values it becomes difficult to pass more than one
                     * parameter to the constructor without modifying the entire class
                     * and the way DynamicMethod objects are used as a result types other than
                     * the Java primitives have a limited support....for now
                     * that type passed must also be a string
                     */
                    Constructor ct;
                    Class customType = Class.forName(type);
                    ct = customType.getConstructor();
                    return ct.newInstance(value);
                }
            }
        } catch (SecurityException ex) {
            if (Boolean.parseBoolean(props.getProperty("verbose"))) {
                Logger.getLogger(ClassUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (InstantiationException ex) {
            if (Boolean.parseBoolean(props.getProperty("verbose"))) {
                Logger.getLogger(ClassUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IllegalAccessException ex) {
            if (Boolean.parseBoolean(props.getProperty("verbose"))) {
                Logger.getLogger(ClassUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IllegalArgumentException ex) {
            if (Boolean.parseBoolean(props.getProperty("verbose"))) {
                Logger.getLogger(ClassUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (InvocationTargetException ex) {
            if (Boolean.parseBoolean(props.getProperty("verbose"))) {
                Logger.getLogger(ClassUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            if (Boolean.parseBoolean(props.getProperty("verbose"))) {
                Logger.getLogger(ClassUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (NoSuchMethodException ex) {
            if (Boolean.parseBoolean(props.getProperty("verbose"))) {
                Logger.getLogger(ClassUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
}
