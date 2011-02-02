package info.crlog.api;

import java.util.HashMap;

/**
 *Object registry maintains a register of objects to avoid recreating
 * Uses registry and singleton design pattern to ensure only one instance is ever
 * created.
 * @author Courtney Robinson
 * @since 0.1
 * @version 0.1
 */
public class ObjectRegistry<K, V> extends HashMap<K, V> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 840431518527069337L;
	/**
     * Handle to unique instance
     */
    private static ObjectRegistry<?,?> instance = null;
    //prevent external instanciation

    private ObjectRegistry() {
        new HashMap<K, V>();
    }

    /**
     * 
     * @return the unique instance of the registry currently available in the program.
     */
    public static ObjectRegistry<?,?> getRegistry() {
        if (null == instance) {
            instance = new ObjectRegistry();
        }
        return instance;
    }

    @Override
    public V put(K key, V value) {
        if (instance.get(key) == null) {
          return super.put(key,value);
        }
        return null;
    }
}
