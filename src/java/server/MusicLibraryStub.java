package ser321.tcpjsonrpc.server;

import java.net.*;
import java.io.*;
import java.util.*;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Copyright Duc Nguyen,
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
 * The student collection server creates a server socket.
 * When a client request arrives, which should be a JsonRPC request, a new
 * thread is created to service the call and create the appropriate response.
 * Byte arrays are used for communication to support multiple langs.
 *
 * @author Duc Nguyen
 * @version 02/26/2020
 */
public class MusicLibraryStub extends Object {

   private static final boolean debugOn = false;
    MusicLibrary musicStub;
    
    public MusicLibraryStub (MusicLibrary musicStub){
        this.musicStub = musicStub;
     }

     private void debug(String message) {
        if (debugOn)
           System.out.println("debug: "+message);
     }
     
     /**
      * callMethod class
      * act as stud similar to Cpp stud
      * data from client will be sent and received from Server
      * through this stud
      */
     public String callMethod(String request){
         JSONObject result = new JSONObject();
         try{
            JSONObject theCall = new JSONObject(request);
            //debug("Request is: "+theCall.toString());
            String method = theCall.getString("method");
            int id = theCall.getInt("id");
            JSONArray params = null;
            if(!theCall.isNull("params")){
               params = theCall.getJSONArray("params");
            }
            result.put("id",id);
            result.put("jsonrpc","2.0");
            
            
            if(method.equals("removeTrack")) {
                String title = params.getString(0);
                //debug("Remove Track "+title);
                boolean titleStr = musicStub.removeTrack(title);
                result.put("result",titleStr);
            } else if (method.equals("removeAlbum")) {
                String album = params.getString(0);
                //debug("Remove Album "+album);
                boolean albumStr = musicStub.removeAlbum(album);
                result.put("result",albumStr);
            } else if (method.equals("removeAlbumCol")) {
                String album = params.getString(0);
                //debug("Remove removeAlbumCol "+album);
                boolean albumStr = musicStub.removeAlbumCol(album);
                result.put("result",albumStr);
            } else if (method.equals("getImage")) {
                String image = musicStub.getImage();
                //debug("get Image: " + image);
                result.put("result", image);
            } else if (method.equals("getSummary")) {
                String summary = musicStub.getSummary();
                //debug("get Image: " + summary);
                result.put("result", summary);
            } else if (method.equals("add_ALL")) {
                String album = params.getString(0);
                String artist = params.getString(1);
                String key = params.getString(2);
                //debug("add_ALL: "+album + " " + artist + " " + key );
                boolean addAll = musicStub.add_ALL(album, artist, key);
                result.put("result",addAll);
            } else if (method.equals("add_ALBUMCOL")) {
                String album = params.getString(0);
                //debug("add_ALL: "+album);
                boolean addAll = musicStub.add_ALBUMCOL(album);
                result.put("result",addAll);
            } else if (method.equals("add_SEARCH")) {
                String artist = params.getString(0);
                String album = params.getString(1);
                String key = params.getString(2);
                //debug("add_SEARCH: ALBUM: "+album + "\tARTIST: " + artist + "\tKEY: " + key );
                boolean addAll = musicStub.add_SEARCH(artist, album, key);
                result.put("result",addAll);
            } else if (method.equals("add_SINGLE")) {
                String title = params.getString(0);
                String artist = params.getString(1);
                String album = params.getString(2);
                String key = params.getString(3);
                //debug("add_ALL: "+album + " " + artist + " " + key );
                boolean addAll = musicStub.add_SINGLE(title, artist, album, key);
                result.put("result",addAll);
            } else if (method.equals("add_TREE")) {
                
                
                String title = params.getString(0);
                Track stud = musicStub.add_TREE(title);
              //  System.out.println("TEST SKELETON: ");
               // System.out.println(stud.getData());
                JSONObject studJson = stud.getData();
               // //debug("add_TREE request found: "+studJson.toString());
                result.put("result",studJson);
                
            } else if (method.equals("add_TREE_C")) {
                String title = params.getString(0);
                JSONObject stud = musicStub.add_TREE_C(title);
				//System.out.println("TEST SKELETON: " + stud);
                result.put("result",stud);
               // System.out.println("SKE: result: " + result);
                
            } else if (method.equals("add_TREE_COL_C")) {
                String title = params.getString(0);
                JSONObject stud = musicStub.add_TREE_COL_C(title);
				//System.out.println("TEST SKELETON: " + stud);
                result.put("result",stud);
               // System.out.println("SKE: result: " + result);
            }
            else if (method.equals("add_SEARCH_C")) {
                String artist = params.getString(0);
                String album = params.getString(1);
                String key = params.getString(2);
                JSONObject stud = musicStub.add_SEARCH_C(artist, album, key);
				//System.out.println("TEST SKELETON: " + stud);
                result.put("result",stud);
               // System.out.println("SKE: result: " + result);
            }
            else if (method.equals("add_TREE_obj")) {
                String title = params.getString(0);
                JSONObject stud = musicStub.add_TREE_obj(title);
				//System.out.println("TEST SKELETON: " + stud);
                result.put("result",stud);
               // System.out.println("SKE: result: " + result);
                
            } else if (method.equals("add_TREE_COL")) {
                String title = params.getString(0);
                Album stud = musicStub.add_TREE_COL(title);
                JSONObject studJson = stud.getData();
             //   //debug("add_TREE_COL request found: "+studJson.toString());
                result.put("result",studJson);
                
            } else if (method.equals("add_TRACKDIALOG")) {
                String artist = params.getString(0);
                String title = params.getString(1);
                String key = params.getString(2);
                String album = params.getString(3);
                JSONObject obj = musicStub.add_TRACKDIALOG(artist, title, key, album);
                result.put("result",obj);
                
            } else if (method.equals("add_TREE_genre")) {
                
                String title = params.getString(0);
                String[] names = musicStub.add_TREE_genre(title);
                JSONArray resArr = new JSONArray();
                for (int i=0; i<names.length; i++){
                   resArr.put(names[i]);
                }
                //debug("add_TREE_genre request found: "+resArr.toString());
                result.put("result",resArr);
                
    
            }  else if (method.equals("add_TREE_COL_genre")) {
                String title = params.getString(0);
                String[] names = musicStub.add_TREE_COL_genre(title);
                JSONArray resArr = new JSONArray();
                for (int i=0; i<names.length; i++){
                   resArr.put(names[i]);
                }
                //debug("add_TREE_COL_genre request found: "+resArr.toString());
                result.put("result",resArr);
                
                
            } else if (method.equals("getTrackList")) {
                String[] names = musicStub.getTrackList();
                JSONArray resArr = new JSONArray();
                for (int i=0; i<names.length; i++){
                   resArr.put(names[i]);
                }
                //debug("getTrackList request found: "+resArr.toString());
                result.put("result",resArr);
            } else if (method.equals("getTitle_EXIST")) {
                String[] names = musicStub.getTitle_EXIST();
                JSONArray resArr = new JSONArray();
                for (int i=0; i<names.length; i++){
                   resArr.put(names[i]);
                }
                //debug("getTitle_EXIST request found: "+resArr.toString());
                result.put("result",resArr);
            }
            
         }catch(Exception ex){
            System.out.println("exception in callMethod: "+ex.getMessage());
         }
         return result.toString();
      }
}

