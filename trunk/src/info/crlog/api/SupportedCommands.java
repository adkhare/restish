package info.crlog.api;

import info.crlog.json.JSONException;
import info.crlog.json.JSONObject;

import java.util.HashMap;

/**
 *
 * @author Courtney
 */
public class SupportedCommands {

    public String list() {
        JSONObject json = new JSONObject();
        try {
            HashMap<String, Object> api = new HashMap<String, Object>();
            HashMap<String, Object> map = new HashMap<String, Object>();
            HashMap<String, Object> imap = new HashMap<String, Object>();
            map.put("desc", "Allows you to create a keyspace on the server, only name is required. replication default=1,"
                    + "placement strategy = simplestrategy");
            imap.put("name", "Name of the keyspace you want to create");
            imap.put("required.name", "true");
            imap.put("replica_placement_strategy", "The replication strategy to be used");
            imap.put("required.replica_placement_strategy", "false");
            imap.put("replication_factor", "The replication factor for this keyspace");
            imap.put("required.replication_factor", "false");

            HashMap<String, String> cf = new HashMap<String, String>();
            cf.put("desc", "Allows you to specify a list of column families and the properties of each column family. "
                    + " List of CFs must have the CF name as the key e.g to create the CFs Users and Songs you would set columnFamilyDef property as : "
                    + " {\"Users\":{\"setComparatorType\":\"UTF8TYPE\",\"setColumnType\":\"STANDARD\"}"
                    + ",\"Songs\":{\"setColumnType\":\"STANDARD\"}} ... This creates the two CFs with different properties");
            cf.put("setColumnType", "The column type, STANDARD or SUPER are the only options");
            cf.put("setComparatorType", " All built in types supported, not yet support custome comparators supported list : BYTESTYPE, ASCIITYPE, UTF8TYPE, LEXICALUUIDTYPE, TIMEUUIDTYPE, LONGTYPE, INTEGERTYPE");
            cf.put("setSubComparatorType", " Same options available as setComparatorType");
            cf.put("setGcGraceSeconds", "Must be a number otherwise it will be ignored");
            cf.put("setKeyCacheSize", " Should be  a double e.g 0.7");
            cf.put("setRowCacheSize", " Should be  a double e.g 0.7");
            cf.put("setMaxCompactionThreshold", " Must be an integer");
            cf.put("setMinCompactionThreshold", " Must be an integer");
            cf.put("setReadRepairChance", "Should be  a double e.g 0.7");


            cf.put("required.setColumnType", "false");
            cf.put("required.setComparatorType", "false");
            cf.put("required.setSubComparatorType", "false");
            cf.put("required.setGcGraceSeconds", "false");
            cf.put("required.setKeyCacheSize", "false");
            cf.put("required.setRowCacheSize", "false");
            cf.put("required.setMaxCompactionThreshold", "false");
            cf.put("required.setMinCompactionThreshold", "false");
            cf.put("required.setReadRepairChance", "false");

            imap.put("required.columnFamilyDef", "true");
            imap.put("columnFamilyDef", cf);
            map.put("params", imap);
            api.put("createKeySpace", map);
            json.put("V1", api);
        } catch (JSONException ex) {
        }

        return json.toString();
    }
}
