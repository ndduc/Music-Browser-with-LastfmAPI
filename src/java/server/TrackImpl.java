package ser321.tcpjsonrpc.server;

//package ser321.assign2.lindquis;
//package server;
//package ser321.assign3.dnnguye7.server;
import java.io.*;
import org.json.*;
import java.util.*;
import java.net.*;
import java.nio.charset.Charset;

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
* Purpose: TrackImpl is modified version of MediaDescription
*      Most modification is in the TrackImpl constructor
*      where additional variable is added
*  Additional: added RMI feature to allow networking
*              
* Ser321 Principles of Distributed Software Systems
* see http://pooh.poly.asu.edu/Ser321
* @author Duc Nguyen
* @version 2.0 02-02-2020
*/



public class TrackImpl implements Serializable, Track  {
  
    JSONObject data;
    List<String> genreArr;
    String title, author, album, summary, runtime, image, rank;

/**
  TrackImpl constructor which mostly use throught the software
  The method initiate neccessary variable
  @param JSONObject 
*/
 public TrackImpl(JSONObject jsonObj) throws Exception{
	// System.out.println("TEST TRACK: ");
	// System.out.println(jsonObj);
         data = jsonObj;
      try{
          genreArr = new ArrayList<String>();
          JSONArray ja = jsonObj.getJSONArray("genre");
          for(int i = 0; i < ja.length(); i++) {
              genreArr.add(ja.get(i).toString());    
          }
          title = jsonObj.getString("title");
          author = jsonObj.getString("author");
          album = jsonObj.getString("album");
          summary = jsonObj.getString("summary");
          runtime = jsonObj.getString("duration");
          image = jsonObj.getString("image");
          rank = String. valueOf(jsonObj.getInt("rank"));
      }catch(Exception ex){
          System.out.println("Exception in TrackImpl (JSONObject): "+ex.getMessage());
      }
 }


/**
 * Getter and Setter
 * */

  public String getSummary() {
      return summary;
  }

  public String getTitleString() {
      return this.title;
  }
  
  public String getAlbumString() {
      return this.album;
  }
  
  public String getRankString() {
      return this.rank;
  }
  public String getImageString() {
      return this.image;
  }
  public String getRuntime() {
      return this.runtime;
  }
  
  public String getAuthorString() {
      return this.author;
  }



@Override
public List<String> getGenre() {
    // TODO Auto-generated method stub
    return genreArr;
}



/**
*	return jsonobject
*	an object that hold data of the given track
*/
@Override
public JSONObject getData() {
    // TODO Auto-generated method stub
    return data;
}



}

  


