package info.crlog.api;

import info.crlog.api.v1.Inserts;
import info.crlog.api.v1.RangeSlice;
import info.crlog.api.v1.Schema;
import info.crlog.interfaces.Constants;
import info.crlog.interfaces.Returnable;
import info.crlog.json.JSONException;
import info.crlog.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import me.prettyprint.hector.api.ddl.ColumnType;

/**
 *
 * @author Courtney
 */
public class ApiManager implements Constants {

    private JSONObject data;
    private Version v;
    private String results;
    private boolean okay = true;
    private Commands cmd;

    public ApiManager(JSONObject d) {
        data = d;
    }

    public void parseAndProcessData() {
        //debug
//        out.println(data);
        try {
            v = Version.valueOf("" + data.get("apiVersion"));

        } catch (Exception ex) {
            results = util.failed(v.V1, "API version specified was not recognised.");
            okay = false;
        }
        try {
            cmd = Commands.valueOf(data.getString("cmd"));
        } catch (Exception ex) {
            results = util.failed(v.V1, "Command specified was not recognised or the there was no cmd specified.");
            okay = false;
        }
        if (okay) {
            switch (v) {
                case V1:
                    v1Handler();
                    break;
                default:
                    results = util.failed(v, "The API version recieved is not recognised Version : " + v);
                    break;
            }
        } else {
            results = util.failed(v, "The data/format is not correct...");
        }
    }

    public String getResults() {
        return results;
    }

    private void v1Handler() {
        //add objects to registry. if already exists then new instance is ignored
        registry.put("mutator", new Inserts());
        registry.put("help", new SupportedCommands());
        switch (cmd) {
            case createKeySpace:
                createKeySpace();
                break;
            case insert:
                doInsert((Inserts) registry.get("mutator"));
                break;
            case get_slice:
                performQuery();
                break;
            case help:
                results = ((SupportedCommands) registry.get("help")).list();
                break;
            default:
                results = util.failed(v, "The command recieved is not recognised Command : " + cmd);
                break;
        }
    }

    /**
     * creates a keyspace with 1 or more column families
     * specified in the json object
     */
    private void createKeySpace() {
        Schema keyspace = null;
        try {
            keyspace = new Schema(data.getString("name"));
            String replicationStrategy = null;
            int replicationFactor = 1;
            //optional params so sollow exception
            try {
                replicationStrategy = data.getString("replica_placement_strategy");
            } catch (Exception e) {
            }
            try {
                replicationFactor = data.getInt("replication_factor");
            } catch (Exception e) {
            }
            HashMap<String, HashMap<String, String>> cfdef =
                    new HashMap<String, HashMap<String, String>>();
            //get all the column families and their definitions
            //get the list of column family names
            JSONObject list = data.getJSONObject("columnFamilyDef");
            //debug
//            out.println(list);
            HashMap<String, String> config;
            JSONObject def;
            String name;
            Iterator cfList = list.keys();
            while (cfList.hasNext()) {
                name = (String) cfList.next();
                def = list.getJSONObject(name);

                config = new HashMap<String, String>();
                config.put("setColumnType", def.getVal("setColumnType"));
                config.put("setComparatorType", def.getVal("setComparatorType"));
                config.put("setSubComparatorType", def.getVal("setSubComparatorType"));
                config.put("setGcGraceSeconds", def.getVal("setGcGraceSeconds"));
                config.put("setKeyCacheSize", def.getVal("setKeyCacheSize"));
                config.put("setRowCacheSize", def.getVal("setRowCacheSize"));
                config.put("setMaxCompactionThreshold", def.getVal("setMaxCompactionThreshold"));
                config.put("setMinCompactionThreshold", def.getVal("setMinCompactionThreshold"));
                config.put("setReadRepairChance", def.getVal("setReadRepairChance"));
                cfdef.put(name, config);
            }
            keyspace.createKeyspace(replicationStrategy, replicationFactor, cfdef);
        } catch (JSONException ex) {
        }
        getReturnable(keyspace);
    }

    /**
     * gets the results from a returnable object
     * assigns the value to the results property
     * @param obj the object which contains the result to be passed to the client
     */
    private void getReturnable(Object obj) {
        if (obj instanceof Returnable) {
            Returnable ret = (Returnable) obj;
            results = ret.getResults();
        }
    }

    private void doInsert(Inserts insertObject) {
        try {
            //get the keyspace we're going to work with
            String KEYSPACE = data.getString("keyspace"),
                    CF = data.getString("columnFamily");
            //MUST set keyspace before anythn else
            insertObject.setKeyspace(KEYSPACE);
            //individual rows for a CF
            JSONObject rows = data.getJSONObject("rows");
            //rows available
//            JSONObject singleRow;
            ColumnType type = ColumnType.getFromValue(data.getString("type"));
                //if only one row is recieved use the single insert and continue
                if (rows.length() == 1) {
                    String key = (String) rows.keys().next();
                    //get the data for the 1 and only row
//                    singleRow = rows.getJSONObject(key);
                    //KEYSPACE,COLUMN_FAMILY,rowKey,columns[]
                    insertObject.insertSingleRow(CF, type, key, rows.getJSONObject(key));
                } else {
                    //add multiple rows
                    insertObject.insertRows(CF, type, rows);
                }
        } catch (JSONException ex) {
          results=  util.pushExceptionToClient(v, ex, "Unable to perform insert");
        }
        getReturnable(insertObject);
    }

    private void performQuery() {
        RangeSlice slice= new RangeSlice();
        String startKey,endKey,startRange,endRange,CF,KS,type;
        int columnLimit=0,rowLimit=0;
        boolean reversed = false,keysOnly=false,ignoreTombstones=true;
        try{
            //required so use getstring to throw ex if they're missing
            CF=data.getString("columnFamily");
            KS=data.getString("keyspace");
            type=data.getString("type");
            //optional parameters
            startKey=data.optString("startKey");
            endKey=data.optString("endKey");
            startRange=data.optString("startRange");
            endRange=data.optString("endRange");
            columnLimit=data.optInt("columnLimit");
            rowLimit=data.optInt("rowLimit");
            keysOnly=data.optBoolean("keysOnly");
            ignoreTombstones=data.optBoolean("ignoreTombstones");
            //set cf
            slice.setColumnFamily(CF);
            slice.setKeySpace(KS);
            slice.getSlice(startKey, endKey, startRange, endRange, reversed, keysOnly,ignoreTombstones, columnLimit, rowLimit);
        }catch(Exception e){
          results=  util.pushExceptionToClient(v, e, "A required query parameter is missing or invalid");
        }
        getReturnable(slice);
    }
}
