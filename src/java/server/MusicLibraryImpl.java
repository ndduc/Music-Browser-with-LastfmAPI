package ser321.tcpjsonrpc.server;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Copyright 2020 Duc Nguyen,
 * Purpose:  This is where data is being manipulate thourghout the entire operation
 * 			musiclibrary allow server to process received data
 * 			add data to server directory
 * 			remove data from server directory
 * 			send data to client side 
 * @author Duc Nguyen
 * @version 02/26/2020
 */
 
 
public class MusicLibraryImpl implements MusicLibrary{
    String trackFile = "album.json";		//main json file
    String albumFile = "classes/albumCol.json";	//voided
    String image;
    String summary;
    String alArtit;
    
    JSONObject rootAlbum;								
    JSONObject rootAlbumCol;
    JSONObject newAlbumTrack_ADD = new JSONObject();       //jsonobject store iterate json
    List<String> albumGenre;
    List<String> trackList; //one for GUI search
    List<String> trackSet;  //one for GUI retrieve
    public Map<String, JSONObject> trackMap;				//Track Map with Json Data
    public Map<String, JSONObject> albumMap;				//Album Map with Json Data
    public Map<String, String> trackDialog;					//Map of Track Name
    

	/**
	 * This constructor iniate track and album
	 * the moment library is being called by client or server
	 * */
    public MusicLibraryImpl() {
            initTrackCollection(trackFile);
           // System.out.println(trackMap);
            initAlbumCollection(albumFile);
    }
    
    /**
     * initiate track collection
     * data from file will be process and put in the proper form
     * then will be save in temporary memory
     * */
    
    public void initTrackCollection(String filename) {
        trackSet = new ArrayList<String>();
        trackMap = new HashMap<String, JSONObject>();
        try {
            JSONObject root = parseJSONFile(filename);
            rootAlbum = root;
            for(Object e : root.keySet()) {
                trackSet.add(e.toString());
                
                Track tr = new TrackImpl(root.getJSONObject(e.toString()));
                trackMap.put(e.toString(), tr.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
      //debugList(trackSet);
    }
    
    /**
     * This method use data from album.json
     * purpose
     * get data for album
     * affected functions are click on album in tree structure
     * corresponding data will be generate
     * */
    public void initAlbumCollection(String filename) {
        try {
        /*JSONObject root = parseJSONFile(filename);
        rootAlbumCol = root;
        for(Object e : root.keySet()) {

            Album tr = new AlbumImpl(root.getJSONObject(e.toString()), filename);
            albumMap.put(e.toString(), tr.getData());
            
            break;
            
        }*/
            JSONObject tmp = rootAlbum;
            List<String> tmpList = new ArrayList<String>();
            for(String e:tmp.keySet()) {
                tmpList.add(e);
            }
            debugList(tmpList);
            Map<String, String> tmpMap1 = new HashMap<String, String>();
            for(int i = 0; i < tmpList.size(); i++) {
                tmpMap1.put(tmp.getJSONObject(tmpList.get(i)).get("album").toString(), tmp.getJSONObject(tmpList.get(i)).get("album").toString());
            }
			JSONObject albumCol = new JSONObject();
            Map<String, String> albumMap_Duration = new HashMap<String, String>();
            for(int i = 0; i < tmpList.size(); i++) {
                String album = tmp.getJSONObject(tmpList.get(i)).get("album").toString();
                for(String e:tmpMap1.keySet()) {
                    if(album.contentEquals(tmpMap1.get(e))) {
                        String summary = tmp.getJSONObject(tmpList.get(i)).get("summary").toString();
                        String artist = tmp.getJSONObject(tmpList.get(i)).get("author").toString();
                        String duration = tmp.getJSONObject(tmpList.get(i)).get("duration").toString();
                        String image = tmp.getJSONObject(tmpList.get(i)).get("image").toString();
                        JSONArray genre = tmp.getJSONObject(tmpList.get(i)).getJSONArray("genre");
                        //System.out.println(duration);
                        JSONObject obj= new JSONObject();
                        obj.put("summary", summary);
                        obj.put("author", artist);
                        obj.put("image", image);
                        obj.put("genre", genre);
                        if(albumMap_Duration.containsKey(album)) {
                            String dura = albumMap_Duration.get(album);
                            long duraOld = Long.parseLong(dura);
                            long duraNew = Long.parseLong(duration);
                            long durat = duraOld + duraNew;
                            String durati = String.valueOf(durat);
                            albumMap_Duration.put(album, durati);
                            obj.put("duration", durati);
                        } else {
                            albumMap_Duration.put(album, duration);
                            obj.put("duration", duration);
                            
                        }
                        albumCol.put(album,obj);
                        break;
                    }
                }
            }
            rootAlbumCol = albumCol;
            albumMap = new HashMap<String, JSONObject>();
            for(String e : albumCol.keySet()) {
               // System.out.println(albumCol.getJSONObject(e.toString()));
                Album tr = new AlbumImpl(albumCol.getJSONObject(e.toString()), "search");
                System.out.println(tr.getData());
                albumMap.put(e, tr.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
    
    
    
    /**
     * This method generate existing title - track 
     * */
    public String[] getTitle_EXIST() {
        initTrackCollection(trackFile);
        
        String[] tmp = trackSet.toArray(new String[0]);
        return tmp;
      
    }
    
    /**
     * This method generate existing title - track  for C
     * */
    public JSONObject getTitle_EXIST_C() {
        initTrackCollection(trackFile);
        JSONObject vec = new JSONObject();
        for(int i = 0; i < trackSet.size(); i++) {
            vec.put(String.valueOf(i),trackSet.get(i));
        }
        System.out.println(vec);
        return vec;
    }
     /**
     * Json Object parser
     * get string as filename
     * return content from file as Jsonobject
     * */
    public JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        //remember to add import file
       File file = new File(filename);
       if(!file.exists()) {
           file.createNewFile();
           FileWriter writer = new FileWriter(filename);
           writer.append("{}");
           writer.flush();
           writer.close();
           
           String content = new String(Files.readAllBytes(Paths.get(filename)));
           return new JSONObject(content);
       } else {
           String content = new String(Files.readAllBytes(Paths.get(filename)));
           return new JSONObject(content);
       }
    }
    
    
    /**
     * 	Method perform read from URL
     * */
    public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        System.out.println("[SERVER-URL]: " + url);
        try {
          BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
          String jsonText = readAll(rd);
          JSONObject json = new JSONObject(jsonText);
          

          return json;
        } finally {
          is.close();
        }
    }
    
    /**
     * 	Helper method for readJsonFromUrl
     * */
    private  String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
    }
    
    
    
    
    /**
     * decode Album Collection - voided
     * brief: 
     * 	param- album is a json data from url that was decoded in this library, however the format is inconsistence thus libary will send this data to album to re-format the format
     * */
    public void decodeAlbumForCollection(JSONObject album) {
        albumGenre = new ArrayList<String>();
        Album al = new AlbumImpl(album, "add");
        albumGenre = al.getGenre();
        summary = al.getSummary();
        image = al.getImage();
    }
    

    
    /**
     * this purposely to get a list of available track name
     * */
    public List<String> decodeAlbumForTrackList(JSONObject album) {
        JSONObject root = album.getJSONObject("album");
        JSONObject root2 = root.getJSONObject("tracks");
        JSONArray trArr = root2.getJSONArray("track");
        
        List<String> tmpList = new ArrayList<String>();
        for(int i = 0; i < trArr.length(); i++) {
            String title = trArr.getJSONObject(i).get("name").toString();
            tmpList.add(title);
        }
   
        debugList(tmpList);
        return tmpList;
    }
    
    /**
     * use by client - 
     * add search allow the program to perform search on url link with provided arguments
     * string artist adnd string album will be converted to UTF-8 format
     * */
    public boolean add_SEARCH(String artist, String album, String key) {
        try {
            String codeAl = URLEncoder.encode(album, "UTF-8");
            String codeArt= URLEncoder.encode(artist, "UTF-8");
            String urlAl = "https://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key="+ key + "&artist="+codeArt +"&album="+codeAl+"&format=json";
            JSONObject data = readJsonFromUrl(urlAl);
            trackList = decodeAlbumForTrackList(data);
            decodeAlbumForCollection(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    /**
     * use by client - 
     * add search allow the program to perform search on url link with provided arguments
     * string artist adnd string album will be converted to UTF-8 format
     * C - support method 
     * */
    public JSONObject add_SEARCH_C(String artist, String album, String key) {
		add_SEARCH( artist,  album,  key);
		JSONObject obj = new JSONObject();
		List<String> tmp = trackList;
		for(int i = 0; i < tmp.size(); i++) {
			obj.put(String.valueOf(i), tmp.get(i));
		}
		return obj;
	}
    
    /**
     * Method allow the program to add all track in album to file
     * */
    public boolean add_ALL(String album, String artist, String key) {
        try {
            String codeAl = URLEncoder.encode(album, "UTF-8");
            String codeArt= URLEncoder.encode(artist, "UTF-8");
            String urlAl = "https://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key="+ key + "&artist="+codeArt +"&album="+codeAl+"&format=json";
            JSONObject data = readJsonFromUrl(urlAl);
            List <String> tit = decodeAlbumForTrackList(data);
          //  debugList(tit);
            decodeAlbum_ADD(album, data, key, artist);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    /**
     * Method allow the program to add single track in album to file
     * */
    public boolean add_SINGLE(String title, String artist, String album, String key) {
        try {
            String codeTitle = URLEncoder.encode(title, "UTF-8");
            String codeArt= URLEncoder.encode(artist, "UTF-8");
            String urlTr = "https://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key="+key+"&artist="+codeArt+"&track="+codeTitle+"&format=json";
            JSONObject data = readJsonFromUrl(urlTr);
            decodeTrack_ADD(title, data, "SINGLE", album);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
    
    /**
     * 	this method genereate data for tree list
     * */
    public Track add_TREE(String title) {
       // Map<String, String> tmpMap = new HashMap<String, String>();
        Track tr = null;
        try {
            tr = new TrackImpl(getTrackRoot().get(title));
            
            /*
            tmpMap.put("artist", tr.getAuthorString());
            tmpMap.put("album", tr.getAlbumString());
            tmpMap.put("rank", tr.getRankString());
            tmpMap.put("duration", tr.getRuntime());
            tmpMap.put("summary", tr.getSummary());
            tmpMap.put("image", tr.getImageString());*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tr;
    }
    
    
    /**
     * 	this method genereate data for tree list
     * C support
     * */
     public JSONObject add_TREE_C(String title) {
        // Map<String, String> tmpMap = new HashMap<String, String>();
         Track tr = null;
         try {
             tr = new TrackImpl(getTrackRoot().get(title));
         } catch (Exception e) {
             // TODO Auto-generated catch block
             e.printStackTrace();
         }
         return tr.getData();
     }
     /**
     * 	this method genereate data for tree list
     * */
    public JSONObject add_TREE_obj(String title) {
        JSONObject tmpMap = new JSONObject();
        Track tr = null;
        try {
            tr = new TrackImpl(getTrackRoot().get(title));
            
            
            tmpMap.put("artist", tr.getAuthorString());
            tmpMap.put("album", tr.getAlbumString());
            tmpMap.put("rank", tr.getRankString());
            tmpMap.put("duration", tr.getRuntime());
            tmpMap.put("summary", tr.getSummary());
            tmpMap.put("image", tr.getImageString());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tmpMap;
    }
    
    
    /**
     * 	this method genereate data for tree list
     * */
    @SuppressWarnings("null")
    public String[] add_TREE_genre(String title) {
        List<String> tmp = new ArrayList<String>();
        String[] str = null;
        try {
            Track tr = new TrackImpl(getTrackRoot().get(title));
            tmp = tr.getGenre();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
     
            str = tmp.toArray(new String[0]);
        
        return str;
    }
    
    /**
     * 	this method genereate data for tree list
     * */
    public Vector<String> add_TREE_genre_C(String title) {
        List<String> tmp = new ArrayList<String>();
        Vector<String> vec = new Vector<String>();
        String[] str = null;
        try {
            Track tr = new TrackImpl(getTrackRoot().get(title));
            tmp = tr.getGenre();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        for(int i = 0; i < tmp.size(); i++) {
            vec.add(tmp.get(i));
        }
        return vec;
    }
    
    /**
     * method add album data for tree list
     * data respond to a click on album folder
     * */
    public Album add_TREE_COL(String title) {
		initAlbumCollection(albumFile);
        Album tr = null;//new HashMap<String, String>();
        try {
            tr = new AlbumImpl(getAlbumRoot().get(title), "search");
            
           /* tmpMap.put("artist", tr.getAuthor());
            tmpMap.put("duration", tr.getDuration());
            tmpMap.put("summary", tr.getSummary());
            tmpMap.put("image", tr.getImage());*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tr;
    }
    
    /**
     * method add album data for tree list
     * data respond to a click on album folder
     * C support
     * */
    public JSONObject add_TREE_COL_C(String title) {
		initAlbumCollection(albumFile);
        Album tr = null;//new HashMap<String, String>();
        try {
            tr = new AlbumImpl(getAlbumRoot().get(title), "search");
            
           /* tmpMap.put("artist", tr.getAuthor());
            tmpMap.put("duration", tr.getDuration());
            tmpMap.put("summary", tr.getSummary());
            tmpMap.put("image", tr.getImage());*/
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return tr.getData();
	}
    
    /**
     * add tree col for C	- voided
     * */
    public String[] add_TREE_COL_genre(String title) {
		initAlbumCollection(albumFile);
        List<String> tmp = new ArrayList<String>();
        String[] str = null;
        try {
            Album tr = new AlbumImpl(getAlbumRoot().get(title), "search");
            tmp = tr.getGenre();
            
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        str = tmp.toArray(new String[0]);
        return str;
    }
    
    
    /**
     * get album root map
     * */
    private Map<String, JSONObject>getAlbumRoot() {
        return albumMap;
    }
    
    
    /**
     * 	the method allow server to send coresponding data when trackdialog is trigged by client
     * */
    public JSONObject add_TRACKDIALOG(String artist, String title, String key, String album) {
        JSONObject tmpMap = new JSONObject();
        System.out.println("TEST DIAGLOG: " + title);
        try {
            String codeTitle = URLEncoder.encode(title, "UTF-8");
            String codeArt= URLEncoder.encode(artist, "UTF-8");
            String urlTr = "https://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key="+key+"&artist="+codeArt+"&track="+codeTitle+"&format=json";
            JSONObject data = readJsonFromUrl(urlTr);
           
            JSONObject tmp = data.getJSONObject("track");
            String duration = tmp.get("duration").toString();
            JSONObject objAl = tmp.getJSONObject("album");
            JSONObject att = objAl.getJSONObject("@attr");
            String rank;
            if(att != null) {
				rank = att.get("position").toString();
			} else {
				rank = "None";
			} 
			
            
            JSONObject objTag = tmp.getJSONObject("toptags");
            JSONArray jarrTag = objTag.getJSONArray("tag");
            List<String> genre = new ArrayList<String>();
            for (int i = 0; i < jarrTag.length(); i++) {
                genre.add(jarrTag.getJSONObject(i).get("name").toString());
            }
            
            tmpMap.put("artist", artist);
            tmpMap.put("title", title);
            tmpMap.put("album", album);
            tmpMap.put("duration", duration);
            tmpMap.put("rank", rank);
            tmpMap.put("genre", genre.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tmpMap;
    }

    
    
    /**
     * 	Decode method
     * 	provided album and data
     *  
     * */
    public void decodeAlbum_ADD(String album, JSONObject data, String key
            ,String artist) {
        List<String> titleList = decodeAlbumForTrackList(data);
        for(int i = 0; i < titleList.size(); i++) {
            trackIteration(titleList.get(i), artist, key , album);
        }
        debugList(titleList);
    }
    
    /**
     * 	helper class for decodeAlbum_Data 
     * */
    private void trackIteration(String title, String artist, String key, String album) {
        try {
            String codeTitle = URLEncoder.encode(title, "UTF-8");
            String codeArt= URLEncoder.encode(artist, "UTF-8");
            String urlTr = "https://ws.audioscrobbler.com/2.0/?method="
                    + "track.getInfo&"
                    + "api_key="+ key + ""
                    + "&artist=" +codeArt +""
                    + "&track="+codeTitle+ ""
                    + "&format=json";
            
            JSONObject data = readJsonFromUrl(urlTr);
            decodeTrack_ADD(title, data, "ALL", album);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        for(Object e : newAlbumTrack_ADD.keySet()) {
            rootAlbum.put(e.toString(), newAlbumTrack_ADD.get(e.toString()));
        }
        
        
        writetoJson(trackFile, rootAlbum);
        newAlbumTrack_ADD = new JSONObject();
    }
    
    
    /**
     * Step to add Track
     *      readJsomFromUrl as JSON - Album
     *      decodeAlbumForCollection (JSON)
     *      readJsonFromUrl as JSON - TRACK
     *      decodeTrack_ADD
     * */
    public void decodeTrack_ADD(String title, JSONObject data, String option, String al) {
        
        
        JSONObject tmp = data.getJSONObject("track");
        String duration = tmp.get("duration").toString();
        JSONObject objArt = tmp.getJSONObject("artist");
        String artist = objArt.get("name").toString();
        
        JSONObject objAl = tmp.getJSONObject("album");
        String album = al;
        JSONObject att = objAl.getJSONObject("@attr");
        String rank = att.get("position").toString();
        
        String tit = title;
        
        JSONObject objTag = tmp.getJSONObject("toptags");
        JSONArray jarrTag = objTag.getJSONArray("tag");
        
        
        List<String> genre = new ArrayList<String>();
        for (int i = 0; i < jarrTag.length(); i++) {
            genre.add(jarrTag.getJSONObject(i).get("name").toString());
        }
        
        if(option.equalsIgnoreCase("SINGLE")) {
            writetoJson_TRACK( title,  album,  artist,  duration,
                    getImage(),  getSummary(),  rank,  genre);
        } else if (option.equalsIgnoreCase("ALL")) {
            
            JSONObject newData = new JSONObject();
            newData.put("title", title);
            newData.put("author", artist);
            newData.put("album", album);
            newData.put("fileName", "mp3");
            newData.put("summary", getSummary());
            newData.put("duration", duration);
            newData.put("image", getImage());
            newData.put("genre", genre);
            newData.put("rank", rank);
            
            newAlbumTrack_ADD.put(title, newData);
            
            
        }
        
    }
    
    /**
     * add Album Col - voided
     * */
    public boolean add_ALBUMCOL(String album) {
        JSONObject newData = new JSONObject();
        
        newData.put("genre", albumGenre );
        newData.put("summary", summary );
        newData.put("artist", alArtit);
        newData.put("image", image);
        JSONObject tmpRoot =null;
        if(rootAlbumCol == null) {
            tmpRoot = new JSONObject();
        } else {
            tmpRoot = rootAlbumCol;
        }
        
        tmpRoot.put(album, newData);
        writetoJson(albumFile, tmpRoot);
        initAlbumCollection(albumFile);
        return true;
    }
    
   
    /**
     * 	Method perform write to Json - Track
     * */
    private void writetoJson_TRACK(String title, String album, String artist, String duration,
            String image, String summary, String rank, List<String> genre) {
        try {
            JSONObject newData = new JSONObject();
            newData.put("title", title);
            newData.put("author", artist);
            newData.put("album", album);
            newData.put("fileName", "mp3");
            newData.put("summary", summary);
            newData.put("duration", duration);
            newData.put("image", image);
            newData.put("genre", genre);
            newData.put("rank", rank);
            
            
            JSONObject newRoot = getRootAlbum();
            newRoot.put(title, newData);
            
            writetoJson(trackFile, newRoot);
            
            rootAlbum = newRoot;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    
    /**
     * 	remore track method
     * */
    public boolean removeTrack(String title) {
        getRootAlbum();
        JSONObject data = rootAlbum;
        data.remove(title);
        writetoJson(trackFile, data);
        return true;
    }
    
    /**
     * 	remore album method
     * */
     
    public boolean removeAlbum(String album) {
        getRootAlbum();
        List<String> tmpTrack = new ArrayList<String>();
        JSONObject data = rootAlbum;
        for(Object e: data.keySet()) {
            if(data.getJSONObject(e.toString()).getString("album").equalsIgnoreCase(album)){
                tmpTrack.add(e.toString());
            }
        }
        
        for(int i = 0; i < tmpTrack.size(); i++) {
            removeTrack(tmpTrack.get(i));
        }
        return true;
    }
    
    /**
     * 	remore album col method - voided
     * */
    public boolean removeAlbumCol(String album) {
        
        JSONObject data = getRootAlbumCol();;
        data.remove(album);
        writetoJson(albumFile, data);
        return true;
    }
    
    
    /**
     * 	any write method will call this method to finalize write sequence
     * */
    private void writetoJson(String fileName, JSONObject data) {
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(data.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    /***
     * 	Getter and Setter
     * 
     * */
    public List<String> getTrackSet() {
        return trackSet;
    }
    
    public Map<String, JSONObject> getTrackRoot() {
        return trackMap;
    }
    
    
    public String[] getTrackList() {
        String[] tmp = trackList.toArray(new String[0]);
        return tmp;
    }
    
    public JSONObject getTrackList_C() {
        JSONObject vec = new JSONObject();
        for(int i = 0; i < trackList.size(); i++) {
            vec.put(String.valueOf(i), trackList.get(i));
        }
        return vec;
    }

    public String getImage() {
        return image;
    }

    public String getSummary() {
        return summary;
    }

    public JSONObject getRootAlbum() {
        initTrackCollection(trackFile);
        return rootAlbum;
    }
    
    public JSONObject getRootAlbumCol() {
        initAlbumCollection(albumFile);
        return rootAlbumCol;
    }
    
    
    public void debugList(List<?> tmp) {
        System.out.println("[DEBUG] " + Arrays.toString(tmp.toArray()));
    }
    
    public void debugString(String tmp) {
        System.out.println("[DEBUG] " + tmp);
    }
    
    
    
    
}
