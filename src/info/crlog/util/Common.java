package info.crlog.util;

import info.crlog.api.Version;
import info.crlog.json.JSONException;
import info.crlog.json.JSONObject;

/**
 *
 * @author Courtney Robinson
 */
public class Common {
     /**
     * Sets up a simple message template to return on error.
     * @param version the current latest API version
     * @param data the data to be returned
     * @return a JSON formatted string with the parameters and status as failed
     * with keys apiVersion,status and reason.
     */
    public String success(Version version, JSONObject data) {
        try { data.put("apiVersion", version);
                data.put("status", "success");
            } catch (JSONException ex1) {
            }
        return data.toString();
    }
    /**
     * Sets up a simple message template to return on error.
     * @param version the current latest API version
     * @param msg the message to return to the client
     * @return a JSON formatted string with the parameters and status as failed
     * with keys apiVersion,status and reason.
     */
    public String success(Version version, String msg) {
        JSONObject data = new JSONObject();
        try { data.put("apiVersion", version);
                data.put("status", "success");
                data.put("reason", msg);
            } catch (JSONException ex1) {
            }
        return data.toString();
    }
    /**
     * Sets up a simple message template to return on error.
     * @param version the current latest API version
     * @param msg the message to return to the client
     * @return a JSON formatted string with the parameters and status as failed
     * with keys apiVersion,status and reason.
     */
    public String failed(Version version, String msg) {
        JSONObject data = new JSONObject();
        try { data.put("apiVersion", version);
                data.put("status", "fail");
                data.put("reason", msg);
            } catch (JSONException ex1) {
            }
        return data.toString();
    }
    /**
     * Captures exceptions generated by the request
     * @param version the api version used to handle the request
     * @param th the exception
     * @return
     * A json formatted version of the exception ready to be flushed directly
     * to the client
     */
    public String pushExceptionToClient(Version version, Throwable th,String ... msg) {
         JSONObject data = new JSONObject();
        try { data.put("apiVersion", version);
                data.put("status", "fail");
                data.put("reason", th);
//                String ex=th.getMessage()+" \n "+th.getCause();
//                StackTraceElement[] el=th.getStackTrace();
//                for(int i=0;i<el.length;i++){
//                    ex+=" \n "+el[i].toString();
//                }
                data.put("cause", th.getCause());
                data.put("stackTrace", th.getStackTrace());
                data.put("desc", msg);
            } catch (JSONException ex1) {
            }
        return data.toString();
    }

}
