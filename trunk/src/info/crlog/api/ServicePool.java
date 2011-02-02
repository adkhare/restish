package info.crlog.api;

import info.crlog.util.PropertiesManager;
import me.prettyprint.cassandra.service.CassandraHost;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.factory.HFactory;

/**
 *
 * @author Courtney
 */
public class ServicePool {

    final public Cluster clusterVar;
    int[] ports;
    //can't implements constants to use props since service pool is instaniated in it
    final static PropertiesManager props = new PropertiesManager("conf/settings");

    public ServicePool() {
        CassandraHostConfigurator config = new CassandraHostConfigurator();
        config.setMaxActive(20);
        config.setMaxIdle(5);
        config.setCassandraThriftSocketTimeout(3000);
        config.setMaxWaitTimeWhenExhausted(4000);
        config.setAutoDiscoverHosts(true);
        String[] hosts = props.getArrayProperty("hosts");
        String hostList = "";
        for (int i = 0; i < hosts.length; i++) {
            if (i == 0) {
                hostList = hosts[i];
            } else if (i <= (hosts.length - 1)) {
                hostList += hosts[i] + ",";
            } else {
                hostList += hosts[i];
            } 
        }
        
        config.setHosts(hostList);
        config.setPort(Integer.parseInt(props.getProperty("port")));
        clusterVar = HFactory.getOrCreateCluster(props.getProperty("cluster"), config);
        CassandraHost[] hostArr = config.buildCassandraHosts();
        for (CassandraHost h : hostArr) {
            clusterVar.addHost(h, false);
        }
    }

    public Cluster getCluster() {
        return clusterVar;
    }
}
