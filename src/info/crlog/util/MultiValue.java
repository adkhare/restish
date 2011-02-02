package info.crlog.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author Courtney
 */
public class MultiValue<K, V> implements  Cloneable, Serializable{

    /**
     * Keys stored as key:valueID
     */
    private HashMap<K, Integer> keys = new HashMap<K, Integer>();
    private HashMap<Integer, ArrayList<V>> values = new HashMap<Integer, ArrayList<V>>();
    /**
     * Adds a new value for the specified key
     * @param key the key for the current value
     * @param value the value to associate with this key
     */
    public void put(K key, V value) {
        ArrayList<V> vals = new ArrayList<V>();
        
        //if the key already exist get the current list and add to it
        if (this.containsKey(key)) {
           int id = keys.get(key);
           //get the existing items
            vals = values.get(id);
            //add the new item
            vals.add(value);
            //put the list back
            values.put(id, vals);
        } else {
            //add the new item
            vals.add(value);
            int id=keys.size();
            //if there is more than one item then add this item as size+1
//                if(id==0)
                    id++;
            values.put(id, vals);
            keys.put(key, id);
        }
    }
    /**
     * @param key they key to get the values of
     * @return a list of all values associated with the key
     */
    public ArrayList<V> get(K key) {
        if (this.containsKey(key)) {
            int id = keys.get(key);
            return values.get(id);
        }
        return null;
    }
    /**
     *
     * @return the set of keys available
     */
    public Set<K> getKeySet(){
        return keys.keySet();
    }
  /**
   *
   * @param key the key to check for
   * @return true if the key exists and false otherwise
   */
    public boolean containsKey(K key) {
        return keys.containsKey(key);
    }
}
