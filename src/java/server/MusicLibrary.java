package ser321.tcpjsonrpc.server;
import java.io.IOException;
import java.util.*;
import org.json.JSONObject;

/**
 * Copyright 2016 Duc Nguyen,
 *
 * MusicLibrary Interface
 * 
 * @author Duc Nguyen
 * @version 02/26/2020
 */
public interface MusicLibrary {
    
    public boolean removeTrack(String title);                                                      //SERVER	remove Track 
    public boolean removeAlbum(String album);                                                      //SERVER remove Album
    public boolean removeAlbumCol(String album);                                                   //SERVER	remove Album Collection - voided
    
    public String getImage();                                                                   	//SERVER	get Image() - image will be set at the moment search is triggered
    public String getSummary();                                                                 	//SERVER	get Summary() - summary will be set at the moment search is triggered
    
    public boolean add_ALL(String album, String artist, String key);                               //SERVER     	add all track in album collection to library
    public boolean add_ALBUMCOL(String album);                                                     //SERVER			add album info in album collection to library - voided
    public boolean add_SEARCH(String artist, String album, String key);                            //SERVER			add search - genereate valid search data to dialog 
    public boolean add_SINGLE(String title, String artist, String album, String key);			   //add Single - add single track
    
    public Track add_TREE(String title);                                              //SERVER		generate data for Tree
    public JSONObject add_TREE_obj(String title);									  //generate data for Tree as JSON
    public Album add_TREE_COL(String title);                                          //SERVER		generate data for Tree
    public JSONObject add_TRACKDIALOG(String artist, String title, String key, String album);  //SERVER	add Track Dialog - parsing data to track dialog interface
    
    public String[] add_TREE_genre(String title);                                           //SERVER	generate list of available genre
    public String[] add_TREE_COL_genre(String title);                                       //SERVER	generate list of available genre - voided
    public String[] getTrackList();                                                         //SERVER	generate list of available track 
    public String[] getTitle_EXIST();                                                       //SERVER	generate list of available track
    
    //C++ code
    public JSONObject add_TREE_C(String title);												//C code similar to add_Tree but data is generated as Json
    public JSONObject add_TREE_COL_C(String title);    										//C code similar to add_Tree but data is generated as Json - voided
    
    public Vector<String> add_TREE_genre_C(String title);                                       //SERVER		///similar to gettracklist - as vector - voided
    public JSONObject getTrackList_C();                                                         //SERVER		get trackList as json
    public JSONObject getTitle_EXIST_C();                                                       //SERVER		get trackListExist as json
    
    public JSONObject add_SEARCH_C(String artist, String album, String key);					//add Search as Json

   
}

