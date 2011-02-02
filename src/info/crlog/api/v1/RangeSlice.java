package info.crlog.api.v1;

import info.crlog.api.Version;
import info.crlog.interfaces.Constants;
import info.crlog.interfaces.Returnable;
import info.crlog.json.JSONException;
import info.crlog.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

/**
 * A simple example showing what it takes to page over results using
 * get_range_slices.
 * 
 * To run this example from maven:
 * mvn -e exec:java -Dexec.mainClass="com.riptano.cassandra.hector.example.PaginateGetRangeSlices"
 * 
 * @author zznate
 *
 */
public class RangeSlice implements Constants, Returnable {

    private static StringSerializer stringSerializer = StringSerializer.get();
    private String COLUMN_FAMILY, KEYSPACE, results;
    private Keyspace operator;

    public void setColumnFamily(String CF) {
        COLUMN_FAMILY = CF;
    }

    public void setKeySpace(String KS) {
        KEYSPACE = KS;
    }

    public void getSlice(String startKey, String endKey, String startRange,
            String endRange, boolean reversed, boolean keysOnly,boolean ignoreTombstones, int columnLimit, int rowLimit) {

        operator = HFactory.createKeyspace(KEYSPACE, cluster);
        JSONObject data = new JSONObject(), columns;
        try {
            RangeSlicesQuery<String, String, String> rangeSlicesQuery =
                    HFactory.createRangeSlicesQuery(operator, stringSerializer,
                    stringSerializer, stringSerializer);
            rangeSlicesQuery.setColumnFamily(COLUMN_FAMILY);
            //define the set to be returned
            rangeSlicesQuery.setKeys(startKey, endKey);
            if (columnLimit != 0) //limit cols
            {
                rangeSlicesQuery.setRange(startRange, endRange, reversed, columnLimit);
            }
            //row limit is optional and will be 0 if not set
            if (rowLimit != 0) {
                rangeSlicesQuery.setRowCount(rowLimit);
            }

            QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
            OrderedRows<String, String, String> orderedRows = result.get();
//            System.out.println("Row size : " + result.get().getList().size());
            if (keysOnly) {
                try {
                    data = new JSONObject();
                    for (Row<String, String, String> rowData : orderedRows) {
                        //new column jsonobject for each rowkey
                    columns = new JSONObject();
                    List<HColumn<String, String>> cols = rowData.getColumnSlice().getColumns();
                    if (cols.isEmpty() && ignoreTombstones) {//tombstone returns no columns
                        continue;
                    }
                        data.accumulate("keys", rowData.getKey());
                       // data.accumulate("size", cols.size());

                    }
                    results = util.success(Version.V1, data);
                } catch (JSONException ex) {
                    Logger.getLogger(RangeSlice.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                HashMap rowValues= new HashMap();
                for (Row<String, String, String> rowData : orderedRows) {
                    //new column jsonobject for each rowkey
                    columns = new JSONObject();
                    List<HColumn<String, String>> cols = rowData.getColumnSlice().getColumns();
                    if (cols.isEmpty() && ignoreTombstones) {//tombstone returns no columns
                        continue;
                    }
//                    System.out.println("Column size : " + cols.size());
                    //TODO create column objects to efficiently serialise results
                    HColumn<String, String> col;
                    for (int i = 0; i < cols.size(); i++) {
                        col = cols.get(i);
                        try {
                            columns.put(col.getName(), col.getValue());
                           // columns.put("timestamp", col.getClock());
                        } catch (JSONException ex) {
                            //bury this
                        }
//                        System.out.println("Row Key : " + r.getKey() + " Col name : " + col.getName() + " Value : " + col.getValue() + " Clock : " + col.getClock());
                    }
                    try {
                        rowValues.put(rowData.getKey(), columns);
                        data.accumulate("rows", rowValues);
                    } catch (JSONException ex) {
                        //
                    }
                }
                results = util.success(Version.V1, data);
            }
        } catch (HectorException he) {
            util.pushExceptionToClient(Version.V1, he, "Unable to perform query");
        }

    }

    public String getResults() {
        return results;
    }
}
