package info.crlog.api.v1;

import info.crlog.api.Version;
import info.crlog.interfaces.Constants;
import info.crlog.interfaces.Returnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import me.prettyprint.cassandra.model.BasicColumnFamilyDefinition;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftCfDef;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnType;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;

/**
 * @author Courtney Robinson
 *
 */
public class Schema implements Constants, Returnable {

    private static StringSerializer stringSerializer = StringSerializer.get();
    private static String KEYSPACE,
            replicationStrat = "org.apache.cassandra.locator.SimpleStrategy",
            results;
    private static int replicationFact = 1;

    public Schema(String ks) {
        KEYSPACE = ks;
    }

    public void createKeyspace(String rs, int rf, HashMap<String, HashMap<String, String>> serializedColumnDefs) {
        if (rs != null) {
            replicationStrat = rs;
        }
        replicationFact = rf;
        try {
            int size = serializedColumnDefs.size();
            BasicColumnFamilyDefinition[] columnFamilies = getColDefs(serializedColumnDefs, size);
            ColumnFamilyDefinition[] cfDef = new ColumnFamilyDefinition[columnFamilies.length];
            for (int i = 0; i < columnFamilies.length; i++) {
                cfDef[i] = new ThriftCfDef(columnFamilies[i]);
            }
            KeyspaceDefinition keyspaceDefinition =
                    HFactory.createKeyspaceDefinition(KEYSPACE, replicationStrat,
                    replicationFact, Arrays.asList(cfDef));
           results= util.success(Version.V1,cluster.addKeyspace(keyspaceDefinition));

        } catch (HectorException he) {
            results = util.failed(Version.V1,
                    "Unable to create keyspace \n message: " + he.getMessage()
                    + " \n cause : " + he.getCause());
        }
    }

    private BasicColumnFamilyDefinition[] getColDefs(HashMap<String, HashMap<String, String>> cf, int size) {
        BasicColumnFamilyDefinition[] columnFamilyDefinition = new BasicColumnFamilyDefinition[size];
        Iterator cfList = cf.keySet().iterator();
        String name;
        HashMap<String, String> config;
        for (int i = 0; i < size; i++) {
            name = (String) cfList.next();
            config = cf.get(name); //new HashMap<String,String>();
            columnFamilyDefinition[i] = new BasicColumnFamilyDefinition();
            columnFamilyDefinition[i].setKeyspaceName(KEYSPACE);
            columnFamilyDefinition[i].setName(name);
            if (config.get("setColumnType") != null) {
                ColumnType type = ColumnType.getFromValue(config.get("setColumnType"));
                columnFamilyDefinition[i].setColumnType(type);
                
                //check subcompare type incase type is a super cf
            if (config.get("setSubComparatorType") != null) {
                //sub comparator only valid if the type is super
                if(type==ColumnType.SUPER)
                columnFamilyDefinition[i].setSubComparatorType(getCompareType(config.get("setSubComparatorType")));
            }
            }
            columnFamilyDefinition[i].setComparatorType(getCompareType(config.get("setComparatorType")));
            
            if (config.get("setGcGraceSeconds") != null) {
                try {
                    columnFamilyDefinition[i].setGcGraceSeconds(Integer.parseInt(config.get("setGcGraceSeconds")));
                } catch (NumberFormatException e) {
                }
            }
            if (config.get("setKeyCacheSize") != null) {
                try {
                    columnFamilyDefinition[i].setKeyCacheSize(Double.parseDouble(config.get("setKeyCacheSize")));
                } catch (NumberFormatException e) {
                }
            }
            if (config.get("setRowCacheSize") != null) {
                try {
                    columnFamilyDefinition[i].setRowCacheSize(Double.parseDouble(config.get("setKeyCacheSize")));
                } catch (NumberFormatException e) {
                }
            }
            if (config.get("setMaxCompactionThreshold") != null) {
                try {
                    columnFamilyDefinition[i].setMaxCompactionThreshold(Integer.parseInt(config.get("setMaxCompactionThreshold")));
                } catch (NumberFormatException e) {
                }
            }
            if (config.get("setMinCompactionThreshold") != null) {
                try {
                    columnFamilyDefinition[i].setMinCompactionThreshold(Integer.parseInt(config.get("setMinCompactionThreshold")));
                } catch (NumberFormatException e) {
                }
            }
            if (config.get("setReadRepairChance") != null) {
                try {
                    columnFamilyDefinition[i].setReadRepairChance(Double.parseDouble(config.get("setReadRepairChance")));
                } catch (NumberFormatException e) {
                }
            }
        }
        return columnFamilyDefinition;
    }

    public String getResults() {
        return results;
    }

    private enum CTYPE {

        BYTESTYPE, ASCIITYPE, UTF8TYPE, LEXICALUUIDTYPE, TIMEUUIDTYPE, LONGTYPE, INTEGERTYPE
    };

    private ComparatorType getCompareType(String get) {
        switch (CTYPE.valueOf(get)) {
            case BYTESTYPE:
                return ComparatorType.BYTESTYPE;
            case ASCIITYPE:
                return ComparatorType.ASCIITYPE;
            case UTF8TYPE:
                return ComparatorType.UTF8TYPE;
            case LEXICALUUIDTYPE:
                return ComparatorType.LEXICALUUIDTYPE;
            case TIMEUUIDTYPE:
                return ComparatorType.TIMEUUIDTYPE;
            case LONGTYPE:
                return ComparatorType.LONGTYPE;
            case INTEGERTYPE:
                return ComparatorType.INTEGERTYPE;
            default:
                return ComparatorType.BYTESTYPE;
        }
    }
}
