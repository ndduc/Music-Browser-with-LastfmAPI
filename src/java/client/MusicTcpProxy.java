package ser321.tcpjsonrpc;

import ser321.tcpjsonrpc.server.Album;
import ser321.tcpjsonrpc.server.AlbumImpl;
import ser321.tcpjsonrpc.server.Track;
import ser321.tcpjsonrpc.server.TrackImpl;
import ser321.tcpjsonrpc.server.MusicLibrary;
import ser321.tcpjsonrpc.server.MusicLibraryImpl;
import java.net.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONArray;
/**
 * Copyright 2020 Duc Nguyen,
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * A class for client-server connections with a threaded server.
 * The student collection client proxy implements the server methods
 * by marshalling/unmarshalling parameters and results and using a TCP
 * connection to request the method be executed on the server.
 * Byte arrays are used for communication to support multiple langs.
 *
 * @author Duc Nguyen
 * @version 02/26/2020
 */
 
 /**
 * This class represent proxy structure for client
 * proxy send data from client to server - in form of curl format {method.. param..}
 * when data is considered as valid proxy will received respond from server and send those responses back to client
 * */
 
public class MusicTcpProxy extends Object implements MusicLibrary {

       private static final boolean debugOn = false;
       private static final int buffSize = 4096;
       private static int id = 0;
       private String host;
       private int port;
       
       public MusicTcpProxy (String host, int port){
          this.host = host;
          this.port = port;
       }
    
       private void debug(String message) {
          if (debugOn)
             System.out.println("debug: "+message);
       }
    
		/**
		 * callMethod
		 * Format request to the correct format then send to server
		 * */
       public String callMethod(String method, Object[] params){
		   id++;
          JSONObject theCall = new JSONObject();
          String ret = "{}";
          try{
             debug("Request is: "+theCall.toString());
             theCall.put("method",method);
             theCall.put("id",id);
             theCall.put("jsonrpc","2.0");
             ArrayList<Object> al = new ArrayList();
             for (int i=0; i<params.length; i++){
                al.add(params[i]);
             }
             JSONArray paramsJson = new JSONArray(al);
             theCall.put("params",paramsJson);
             Socket sock = new Socket(host,port);
             OutputStream os = sock.getOutputStream();
             InputStream is = sock.getInputStream();
             int numBytesReceived;
             int bufLen = 1024;
             String strToSend = theCall.toString();
             byte bytesReceived[] = new byte[buffSize];
             byte bytesToSend[] = strToSend.getBytes();
             os.write(bytesToSend,0,bytesToSend.length);
             numBytesReceived = is.read(bytesReceived,0,bufLen);
             ret = new String(bytesReceived,0,numBytesReceived);
             debug("callMethod received from server: "+ret);
             os.close();
             is.close();
             sock.close();
          }catch(Exception ex){
             System.out.println("exception in callMethod: "+ex.getMessage());
          }
          return ret;
       }
    
    /**
     * 	These act in the similar way to studs do
     * 	Detail functionality can be trace by to musicLibaray class
     *  
     * */
    @Override
    public boolean removeTrack(String title) {
        boolean ret = false;
        String result = callMethod("removeTrack", new Object[]{title});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result",false);
        return ret;
    }
    
    @Override
    public boolean removeAlbum(String album) {
        boolean ret = false;
        String result = callMethod("removeAlbum", new Object[]{album});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result",false);
        return ret;
    }
    
    @Override
    public boolean removeAlbumCol(String album) {
        boolean ret = false;
        String result = callMethod("removeAlbumCol", new Object[]{album});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result",false);
        return ret;
    }
    
    @Override
    public String getImage() {
        // TODO Auto-generated method stub
        String ret = null;
        String result = callMethod("getImage", new Object[]{});
        JSONObject res = new JSONObject(result);
        ret = res.optString("result",result);
        return ret;
    }
    
    @Override
    public String getSummary() {
        // TODO Auto-generated method stub
        String ret = null;
        String result = callMethod("getSummary", new Object[]{});
        JSONObject res = new JSONObject(result);
        ret = res.optString("result",result);
        return ret;
    }
    
    @Override
    public boolean add_ALL(String album, String artist, String key) {
        boolean ret = false;
        String result = callMethod("add_ALL", new Object[]{album, artist, key});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result",false);
        return ret;
    }
    
    @Override
    public boolean add_ALBUMCOL(String album) {
        // TODO Auto-generated method stub
        boolean ret = false;
        String result = callMethod("add_ALBUMCOL", new Object[]{album});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result",false);
        return ret;
    }
    
    @Override
    public boolean add_SEARCH(String artist, String album, String key) {
        // TODO Auto-generated method stub
        boolean ret = false;
        String result = callMethod("add_SEARCH", new Object[]{artist, album, key});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result",false);
        return ret;
    }
    
    @Override
    public boolean add_SINGLE(String title, String artist, String album, String key) {
        // TODO Auto-generated method stub
        boolean ret = false;
        String result = callMethod("add_SINGLE", new Object[]{title, artist, album, key});
        JSONObject res = new JSONObject(result);
        ret = res.optBoolean("result",false);
        return ret;
    }
    
    /**
    public Student get(String aName) {
          Student ret = new Student("unknown",-999, new String[]{"unknown"});
          String result = callMethod("get", new Object[]{aName});
          JSONObject res = new JSONObject(result);
          JSONObject studJson = res.optJSONObject("result");
          ret = new Student(studJson);
          return ret;
       }
        
    */
    @Override
    public Track add_TREE(String title) {
        
        Track ret = null;
        try {
            //ret = new TrackImpl(null);
            String result = callMethod("add_TREE", new Object[]{title});
            JSONObject res = new JSONObject(result);
            JSONObject studJson = res.optJSONObject("result");
           // System.out.println("PROXY: " + studJson);
            ret = new TrackImpl(studJson); 
          //  System.out.println("PROXY: " + ret.getSummary());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    @Override
    public JSONObject add_TREE_obj(String title) {
        System.out.println("PROXY");
        JSONObject ret = null;
        try {
            ret = new JSONObject();
            String result = callMethod("add_TREE_obj", new Object[]{title});
            //System.out.println("PROXY a: " + result);
			JSONObject res = new JSONObject(result);
            JSONObject studJson = res.optJSONObject("tree_obj");
            ret = studJson; 
           // System.out.println("PROXY: " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    @Override
    public JSONObject add_SEARCH_C(String artist, String album, String key) {
        System.out.println("PROXY");
        JSONObject ret = null;
        try {
            ret = new JSONObject();
            String result = callMethod("add_SEARCH_C", new Object[]{artist, album, key});
            //System.out.println("PROXY a: " + result);
			JSONObject res = new JSONObject(result);
            JSONObject studJson = res.optJSONObject("result");
            ret = studJson; 
           // System.out.println("PROXY: " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    
    
    @Override
    public Album add_TREE_COL(String title) {
        Album ret = null;
        try {
            //ret = new TrackImpl(null);
            String result = callMethod("add_TREE_COL", new Object[]{title});
            JSONObject res = new JSONObject(result);
            JSONObject studJson = res.optJSONObject("result");
           // System.out.println("PROXY: " + studJson);
            ret = new AlbumImpl(studJson, "search"); 
          //  System.out.println("PROXY: " + ret.getSummary());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    @Override
    public JSONObject add_TRACKDIALOG(String artist, String title, String key, String album) {
        JSONObject ret = null;
        String result = callMethod("add_TRACKDIALOG", new Object[]{artist, title, key, album});
        JSONObject res = new JSONObject(result);
        ret = res;
        return ret;
    }
    
    @Override
    public String[] add_TREE_genre(String title) {
        String[] ret = new String[]{};
        String result = callMethod("add_TREE_genre", new Object[] {title});
        //debug("result of add_TREE_genre is: "+result);
        JSONObject res = new JSONObject(result);
        JSONArray namesJson = res.optJSONArray("result");
        ret = new String[namesJson.length()];
        for (int i=0; i<namesJson.length(); i++){
           ret[i] = namesJson.optString(i,"unknown");
        }
        return ret;
    }
    
    @Override
    public String[] add_TREE_COL_genre(String title) {
        // TODO Auto-generated method stub
        String[] ret = new String[]{};
        String result = callMethod("add_TREE_COL_genre", new Object[] {title});
        //debug("result of add_TREE_COL_genre is: "+result);
        JSONObject res = new JSONObject(result);
        JSONArray namesJson = res.optJSONArray("result");
        ret = new String[namesJson.length()];
        for (int i=0; i<namesJson.length(); i++){
           ret[i] = namesJson.optString(i,"unknown");
        }
        return ret;
    }
    
    @Override
    public String[] getTrackList() {
        // TODO Auto-generated method stub
        String[] ret = new String[]{};
        String result = callMethod("getTrackList", new Object[0]);
        //debug("result of getTrackList is: "+result);
        JSONObject res = new JSONObject(result);
        JSONArray namesJson = res.optJSONArray("result");
        ret = new String[namesJson.length()];
        for (int i=0; i<namesJson.length(); i++){
           ret[i] = namesJson.optString(i,"unknown");
        }
        return ret;
    }
    
    @Override
    public String[] getTitle_EXIST() {
        // TODO Auto-generated method stub
        String[] ret = new String[]{};
        String result = callMethod("getTitle_EXIST", new Object[0]);
        //debug("result of getTitle_EXIST is: "+result);
        JSONObject res = new JSONObject(result);
        JSONArray namesJson = res.optJSONArray("result");
        ret = new String[namesJson.length()];
        for (int i=0; i<namesJson.length(); i++){
           ret[i] = namesJson.optString(i,"unknown");
        }
        return ret;
    }
    
    
    //Code for C++ - not use in java
    @Override
    public JSONObject add_TREE_C(String title) {
        System.out.println("PROXY");
        JSONObject ret = null;
        try {
            ret = new JSONObject();
            String result = callMethod("add_TREE_C", new Object[]{title});
            //System.out.println("PROXY a: " + result);
			JSONObject res = new JSONObject(result);
            JSONObject studJson = res.optJSONObject("add_tree");
            ret = studJson; 
           // System.out.println("PROXY: " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    @Override
    public JSONObject add_TREE_COL_C(String title) {
        System.out.println("PROXY");
        JSONObject ret = null;
        try {
            ret = new JSONObject();
            String result = callMethod("add_TREE_COL_C", new Object[]{title});
            //System.out.println("PROXY a: " + result);
			JSONObject res = new JSONObject(result);
            JSONObject studJson = res.optJSONObject("add_tree");
            ret = studJson; 
           // System.out.println("PROXY: " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    

    public Vector<String> add_TREE_genre_C(String title) {
        // TODO Auto-generated method stub
       Vector<String> ret = new Vector<String>();
        return ret;
    }
    
    public Vector<String> add_TREE_COL_genre_C(String title) {
        // TODO Auto-generated method stub
       Vector<String> ret = new Vector<String>();
        return ret;
    }
    
    public JSONObject getTrackList_C() {
        // TODO Auto-generated method stub

       System.out.println("PROXY");
        JSONObject ret = null;
        try {
            ret = new JSONObject();
            String result = callMethod("getTrackList_C", new Object[0]);
            //System.out.println("PROXY a: " + result);
			JSONObject res = new JSONObject(result);
            JSONObject studJson = res.optJSONObject("getTrackList_C");
            ret = studJson; 
           // System.out.println("PROXY: " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;

    }
    
    public JSONObject getTitle_EXIST_C() {
      System.out.println("PROXY");
        JSONObject ret = null;
        try {
            ret = new JSONObject();
            String result = callMethod("getTitle_EXIST_C", new Object[0]);
            //System.out.println("PROXY a: " + result);
			JSONObject res = new JSONObject(result);
            JSONObject studJson = res.optJSONObject("getTitle_EXIST_C");
            ret = studJson; 
           // System.out.println("PROXY: " + ret);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
}

