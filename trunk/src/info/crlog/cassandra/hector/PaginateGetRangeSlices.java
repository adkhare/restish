package info.crlog.cassandra.hector;

import java.util.List;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
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
public class PaginateGetRangeSlices {

    private static StringSerializer stringSerializer = StringSerializer.get();

    public static void main(String[] args) throws Exception {

        Cluster cluster = HFactory.getOrCreateCluster("TestCluster", "168.144.82.85:9160");

        Keyspace keyspaceOperator = HFactory.createKeyspace("Test", cluster);

        try {
            Mutator<String> mutator = HFactory.createMutator(keyspaceOperator, stringSerializer);

            for (int i = 0; i < 200; i++) {
                mutator.addInsertion("akey_" + i, "Users", HFactory.createStringColumn("fake_column_0", "value_0_" + i)).addInsertion("key_" + i, "Users", HFactory.createStringColumn("fake_column_1", "value_1_" + i)).addInsertion("key_" + i, "Users", HFactory.createStringColumn("fake_column_2", "value_2_" + i));
            }
            mutator.execute();

            RangeSlicesQuery<String, String, String> rangeSlicesQuery =
                    HFactory.createRangeSlicesQuery(keyspaceOperator, stringSerializer, stringSerializer, stringSerializer);
            rangeSlicesQuery.setColumnFamily("Users");
            rangeSlicesQuery.setKeys("", "");
            rangeSlicesQuery.setRange("", "", false, 3);

//            rangeSlicesQuery.setRowCount(11);
            QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
            OrderedRows<String, String, String> orderedRows = result.get();


            Row<String, String, String> lastRow = orderedRows.peekLast();

            System.out.println("Contents of rows: \n");
            for (Row<String, String, String> r : orderedRows) {
                List<HColumn<String, String>> cols = r.getColumnSlice().getColumns();
                System.out.println("Column size : "+cols.size());
                HColumn<String, String> col;
                for (int i = 0; i < cols.size(); i++) {
                    col = cols.get(i);
                    System.out.println("Row Key : " + r.getKey() + " Col name : " + col.getName() + " Value : " + col.getValue() + " Clock : " + col.getClock());
                }
            }
            System.out.println("Should have 11 rows: " + orderedRows.getCount());

            rangeSlicesQuery.setKeys(lastRow.getKey(), "");
            orderedRows = rangeSlicesQuery.execute().get();

            System.out.println("2nd page Contents of rows: \n");
            for (Row<String, String, String> row : orderedRows) {
              
                List<HColumn<String, String>> cols = row.getColumnSlice().getColumns();
                HColumn<String, String> col;
                for (int i = 0; i < cols.size(); i++) {
                    col = cols.get(i);
                    System.out.println("Row Key : " + row.getKey() + " Col name : " + col.getName() + " Value : " + col.getValue() + " Clock : " + col.getClock());
                }
            }

        } catch (HectorException he) {
            he.printStackTrace();
        }
        cluster.getConnectionManager().shutdown();
    }
}
