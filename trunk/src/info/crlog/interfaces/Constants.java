package info.crlog.interfaces;

import info.crlog.api.ObjectRegistry;
import info.crlog.api.ServicePool;
import info.crlog.util.Common;
import info.crlog.util.PropertiesManager;

import java.awt.Toolkit;

import me.prettyprint.hector.api.Cluster;

/**
 *
 * @author Courtney
 */
public interface Constants {

    public int height = Toolkit.getDefaultToolkit().getScreenSize().height;
    public int width = Toolkit.getDefaultToolkit().getScreenSize().width;
    /**
     * props is a property manager object which has utility methods to quickly, add and update
     * the default settings.properties file
     */
    public static PropertiesManager props = new PropertiesManager("conf/settings");
    public static Common util = new Common();
    public Cluster cluster = new ServicePool().getCluster();
    public ObjectRegistry registry = ObjectRegistry.getRegistry();
}
