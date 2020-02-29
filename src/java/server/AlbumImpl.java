package ser321.tcpjsonrpc.server;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Copyright 2020 Duc Nguyen,
 * Purpose: store data for album
 * 		    a better form of storage
 * @author Duc Nguyen
 * @version 02/26/2020
 */
public class AlbumImpl implements Album{
    JSONObject data;
    String author;
    List<String> genre;
    String image;
    String summary;
    String duration;
    String album;
    
    /**
     * Constructor
     * received data
     * either from file or decoded string from url
     * option search - use for search purpose (from file)
     * option add - use for add new data (url)
     * 
     * json data in search will be saved to local json file - with get method
     * */
    public AlbumImpl(JSONObject jsonObj, String option) {
        
        
        if (option.equalsIgnoreCase("search")) {
            this.data = jsonObj;
            try{
                genre = new ArrayList<String>();
                JSONArray ja = jsonObj.getJSONArray("genre");
                for(int i = 0; i < ja.length(); i++) {
                    genre.add(ja.get(i).toString());    
                }
                author = jsonObj.getString("author");
                summary = jsonObj.getString("summary");
                duration = jsonObj.getString("duration");
                image = jsonObj.getString("image");
            }catch(Exception ex){
                System.out.println("Exception in AlbumImpl(JSONObject): "+ex.getMessage());
            }
            
            //Else condition retrieve raw json from url
            //thus this need more logic to process the json data
        } else if (option.equalsIgnoreCase("add")) {
            decodeOpt_ADD(jsonObj);
        }
        
    }
    
    /**
     * this method decode json from url
     * aka give the json a proper format
     * */
    private void decodeOpt_ADD(JSONObject jsonObj) {
        try {
            
            genre = new ArrayList<String>();
            JSONObject tmpRoot = jsonObj.getJSONObject("album");
            album = tmpRoot.get("name").toString();
            author = tmpRoot.get("artist").toString();
            JSONArray tmpImg = tmpRoot.getJSONArray("image");
            for (int i = 0; i < tmpImg.length();) {
                String objImg = tmpImg.getJSONObject(3).getString("#text");
                image = objImg;
                System.out.println(image);
                break;
            }
            JSONObject gen = tmpRoot.getJSONObject("tags");
            JSONArray genArr = gen.getJSONArray("tag");
            for (int i = 0; i < genArr.length(); i++) {
                String tmp = genArr.getJSONObject(i).get("name").toString();
                genre.add(tmp);
            }
            
            JSONObject wiki =tmpRoot.getJSONObject("wiki");
            summary = wiki.getString("summary");

            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


	/**
	 * getter and setter
	 * */
    public JSONObject getData() {
        return data;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getGenre() {
        return genre;
    }

    public String getImage() {
        return image;
    }

    public String getSummary() {
        return summary;
    }

    public String getDuration() {
        return duration;
    }
    
    
}
