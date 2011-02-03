/**
 * 
 */
package info.crlog.api.v1;

import me.prettyprint.hector.api.beans.HColumn;


/**
 * Essentially we need a faster way to serialise possibly thousands of columns to JSON
 * The JSON lib included does far too many string searches etc which can have a performance
 * hit with large rows
 * Aiming for {"name":"column name","value":"column value","timestamp":"3658985898459589459"}
 * @author Courtney
 *
 */
public class JSONColumn {
	private Object name,value,timestamp;
	/**
	 * 
	 */
	public JSONColumn(HColumn<?,?> column) {
		name=column.getName();
		value=column.getValue();
		timestamp=column.getClock();
	}
	
//	public String toString(){
//		
//		return "";
//	}

}
