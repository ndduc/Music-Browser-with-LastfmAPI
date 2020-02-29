package ser321.tcpjsonrpc.server;
//package ser321.assign3.dnnguye7.server;
/**
 * Copyright 2016 Duc Nguyen,
 *
 * Track Interface
 * 
 * @author Duc Nguyen
 * @version 02/26/2020
 */
import java.io.IOException;
import java.rmi.Remote;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
public interface Track{

	/**
	 * getter and setter
	 * */
    public String getSummary() ;
    public String getTitleString() ;
    public String getAlbumString() ;
    public String getRankString();
    public String getImageString();
    public String getRuntime();
    public String getAuthorString() ;

    public JSONObject getData();			//get json data - use by C and some java function
    public List<String> getGenre();			//get a list of available genre
}
