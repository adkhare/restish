package info.crlog.api.v1;

import static me.prettyprint.hector.api.factory.HFactory.createKeyspace;
import static me.prettyprint.hector.api.factory.HFactory.createMutator;
import info.crlog.api.Version;
import info.crlog.interfaces.Constants;
import info.crlog.interfaces.Returnable;
import info.crlog.json.JSONException;
import info.crlog.json.JSONObject;

import java.util.Iterator;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.ddl.ColumnType;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;

/**
 *
 * @author Courtney Robinson
 */
public class Inserts implements Constants, Returnable {

    private Mutator mutator;
    private String KEYSPACE;
    private String CF_NAME;
    private final StringSerializer serializer = StringSerializer.get();
    private Keyspace keyspace;
    private String results;

    /**
     * sets the name of the keyspace to work with
     * @param ks
     */
    public void setKeyspace(String ks) {
        //create keyspace obj
        keyspace = createKeyspace(ks, cluster);
        registry.put("keyspace", keyspace);
        //create the mutator for us to use
        mutator = createMutator(((Keyspace) registry.get("keyspace")), serializer);
    }

    public void insertRows(String CF, ColumnType type, JSONObject rows) {
        Iterator columns,keys=rows.keys();
        //column name and individual key
        String key, columnName,value;
        //columns for a row
        JSONObject columnArray;
        //loop through rows
        try {
            while (keys.hasNext()) {
                //get this row's key
                key = (String) keys.next();
                //columns for the row
                columnArray = rows.getJSONObject(key);
                //column names for this row
                columns = columnArray.keys();
               // System.out.printf("key : %s   - val : %s \n", key, columnArray);
                //add all the columns for this row
                while (columns.hasNext()) {
                    columnName = (String) columns.next();
                    value=columnArray.optString(columnName);
//                    System.out.printf("Col Name : %s   - val : %s \n",
//                            columnName, columnArray.optString(columnName));
                    if (type == ColumnType.STANDARD) {
                        mutator.addInsertion(key, CF, column(columnName, value));
                    } else {
                        //TODO add support for inserting super column
                    }
                }
            }
            results = util.success(Version.V1,mutator.execute().toString());
        } catch (JSONException ex) {
            results=util.success(Version.V1,util.pushExceptionToClient(Version.V1, ex, "Unable to insert value"));
        }
    }

    /**
     * Inserts a single row into they specified column family
     * @param CF the column family to insert into
     * @param type the column family type, SUPER or STANDARD
     * @param key they row key
     * @param columns the set of columns and their values to insert
     */
    public void insertSingleRow(String CF, ColumnType type, String key, JSONObject columns) {
        //COLUMN_FAMILY,rowKey,columns[]
        Iterator cols = columns.keys();
        //column name
        String name;
        while (cols.hasNext()) {
            name = (String) cols.next();
            try {
                if (type == ColumnType.STANDARD) {
//                    mutator.addInsertion(key, CF, column(name, columns.getString(name)));
                   results= util.success(Version.V1, mutator.insert(key, CF, column(name, columns.getString(name))).toString());
                } else {
                    //TODO add support for inserting super column
                }
            } catch (JSONException ex) {
                results=util.pushExceptionToClient(Version.V1, ex, "Unable to insert value");
            }
            results = util.success(Version.V1,mutator.execute().toString());
        }
    }

    /**
     * creates a single column of type standard
     * @param name the column name
     * @param value the value for this column
     * @return the HColumn for the KV pair
     */
    private HColumn column(String name, String value) {
        return HFactory.createColumn(name, value, serializer, serializer);
    }

    public String getResults() {
        return results;
    }
}
