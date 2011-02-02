package info.crlog.api;

import info.crlog.interfaces.Constants;
import info.crlog.json.JSONException;
import info.crlog.json.JSONObject;

/**
 *
 * @author Courtney
 */
public class RequestHandler implements Constants {

    private String rawData, results;

    public RequestHandler(String data) {
        rawData = data;
        processData();
    }

    /**
     * return the results generated from the request
     * @return
     */
    public byte[] getResults() {
        if (results == null) {
            return util.failed(Version.V1, "Nothing available to return").getBytes();
        } else {
            return results.getBytes();
        }
    }

    /**
     * Creates the api manager instance and invokes the appropriate methods
     * It gets the results and assigns it to the results var ready to be returned
     * to the client
     */
    private void processData() {
        try {
            ApiManager api = new ApiManager(new JSONObject(rawData));
            api.parseAndProcessData();
            results = api.getResults();
        } catch (JSONException ex) {
            results = util.failed(Version.V1, "The data recieved is not formatted properly");
        }
    }
}
