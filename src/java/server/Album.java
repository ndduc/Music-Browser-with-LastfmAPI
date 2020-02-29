package ser321.tcpjsonrpc.server;

import java.util.List;

import org.json.JSONObject;
/**
 * Copyright 2020 Duc Nguyen,
 * Purpose:  album interface
 * 
 * store data for album
 *  a better form of data storage
 * @author Duc Nguyen
 * @version 02/26/2020
 */
public interface Album {
    public JSONObject getData();

    public String getAuthor();

    public List<String> getGenre();

    public String getImage();

    public String getSummary();

    public String getDuration();
}
