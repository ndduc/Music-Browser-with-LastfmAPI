/**
 * Copyright (c) 2020 Duc Nguyen,
 * Purpose:
 * 	This program utilize redefineded gui content from MediaClientGui.cpp
 *  Allow the user to control the gui interface and its functionlities as will
 * 	This program will include these following functionalities
 * 		Search Album and Track
 * 		Add Album or Track to Music Library 
 * 				Music Library is a part of save
 * 				Data save to music library wil be saved in server
 * 		Remove Album or Track from Library
 * 		Save and Restore
 * 			Save event is triggered by Add or Remove event
 * 			Save point can also be set manual by user
 * 			Restore, revert data from library to the latest save point 
 * @author  Duc Nguyen
 * @file    MusicBrowser.cpp
 * @date    02/16/2020
 * @license See above
 **/



// the include below requires that you've built the jsonrpccpp package manually
// with the switchs as follows:
// cmake ../ -DTCP_SOCKET_SERVER=YES -DTCP_SOCKET_CLIENT=YES
// make
// make doc    // this will build html docs in the build/doc/html directory
// sudo make install
//#include <jsonrpccpp/client/connectors/tcpsocketclient.h>
#include "TCPSocket.hpp"  // For Socket and SocketException
#include "MediaClientGui.cpp"
#include <json/json.h>
//#include "musicstub.h"/*
//#include "../server/Tracks.hpp"
//#include "../server/Albums.hpp"*/
//#include "../server/MusicLibrary.hpp"
#include <jsonrpccpp/client/connectors/httpclient.h>

#include <FL/Fl.H>
#include <FL/Fl_Window.H>
#include <FL/Fl_Button.H>
#include <FL/Fl_Output.H>
#include <FL/Fl_Multiline_Input.H>
#include <FL/Fl_Check_Browser.H>
#include <stdio.h>
#include <iostream>
#include <chrono>
#include <ctime>
#include <stdlib.h>
#include <sstream>
#include <thread>
#include <json/json.h>
#include <curlpp/cURLpp.hpp>
#include <curlpp/Options.hpp>
#include <curlpp/Easy.hpp>
#include <curlpp/Exception.hpp>
#include <FL/fl_ask.H>
#include <json/json.h>
#include <fstream>
#include <stdio.h>
#include <curl/curl.h>
#include <map>
#include <iterator>
#include <algorithm>
#include <iostream>
#include <vector>
#include <stdlib.h>
#include <string>
#include <sstream>
#include <iostream>
#include <stdlib.h>
#include <cmath>
#include <stdio.h>
#include <iostream>
#include <fstream>
#include <string>
#include <vector>
#include <cctype>
#include <iomanip>
#include <sstream>
#include <string>
#include <json/json.h>

using namespace jsonrpc;
using namespace std;
std::string cmd;
void run() {
	system(cmd.c_str());
}

class MediaClient : public MediaClientGui {
	
	public:
	Json::Value da;				//OLD
	Json::Value title;			//OLD
	Json::Value reData;			//OLD
	Json::Value reAlbumData;	//OLD
	
	Json::Value trackClickData;	//json data genre -- data refresh after tree is rebuild
	//string lastfmkey = "eab1524872bc511168bc279f219c9d04";
	Json::Value albumClickData;
	string ip;
	 int port;
	
	/**
	 * Close message dialog
	 * */
	static void closeMessage(Fl_Widget*w, void*data) {
		MediaClient *o = (MediaClient*)data;
		o->msgDialog->hide();
	
	}
	/** ClickedX is one of the callbacks for GUI controls.
	* Callbacks need to be static functions. But, static functions
	* cannot directly access instance data. This program uses "userdata"
	* to get around that by passing the instance to the callback
	* function. The callback then accesses whatever GUI control object
	* that it needs for implementing its functionality.
	*/
	static void ClickedX(Fl_Widget * w, void * userdata) {
		std::cout << "You clicked Exit" << std::endl;
		exit(1);
	}
	
	
	/**
	 * 	Close Dialog method
	 *  Affected component: the additional dialog aka where you search for album and track
	 * */
	static void closeD(Fl_Widget*w, void*data) {
		MediaClient *o = (MediaClient*)data;
		o->dialog->hide();
		o->treeD->clear();
		cout << "Clicked Close" << endl;
	}

	/**
	* Static search button callback method.
	*/
	static void SearchCallbackS(Fl_Widget*w, void*data) {
		MediaClient *o = (MediaClient*)data;
		o->SearchCallbackS(o);
	}
	
	/**
	 * 	Helper method for searchCallbackS statis
	 *  goal: method is intended to unhide the addtional dialog
	 * */
	void SearchCallbackS(MediaClient *o) {
		o->alInputD->value(o->albSrchInput->value());
		o->arInputD->value(o->artSrchInput->value());
		o->dialog->show();
	}
	
	

	
	/**
	 * this void method is intended to be called only by search Dialog
	 * it performs necessary logic.
	 * */
	void SearchCallbackD(MediaClient *o, string ip, int port) {
		try {
			cout << "TEST TEST: " << ""  << endl; 
			o->dialog->show();
			o->treeD->clear();
			vector<string> dat;
			std::string searchAlbum = o->alInputD->value();
			std::string searchArt = o->arInputD->value();
			
			///////////////
			string js = add_SEARCH_C(searchArt, searchAlbum, o->lastfmkey);//("Roads Untraveled");
			int z = js.length();
			char char_ar[z + 1];
			strcpy(char_ar, js.c_str());
			int echoStringLen = strlen(char_ar); 
			Json::Value jsn = getResult(char_ar, echoStringLen, ip, port );
			
			cout << "TEST JSON: " << jsn << endl; 
			
			/*cout << "TEST JSON: "  << jsn << endl;
			///////////
			string test = "nothing";
			string test_o = jsn["none"].asCString();
			cout << test_o << endl;
			if(test_o.compare(test) == 0) {
				cout << "HIT E" << endl;
			} else {
				*/
				
				
				try {
					for (string id : jsn.getMemberNames()) {
							//std::cout << id << std::endl;
							dat.push_back(jsn[id].asString());
					}
					for (int i = 0; i < dat.size(); i++) {
						std::stringstream stream;
						stream << "Music"
							<< "/" << dat[i];
						o->treeD->add(stream.str().c_str());
					}
					o->treeD->redraw();
					}
					catch (curlpp::LogicError & e) {
						std::cout << e.what() << std::endl;
					}catch (curlpp::RuntimeError & e) {
						std::cout << e.what() << std::endl;
					}
			//}
			
		}
		catch (std::exception & e) {
			std::cout << e.what() << std::endl;
		}
	}
	


	/**
	 * static method activate search Dialog after user click on search buttom in main frame
	 * */
	static void SearchCallbackD(Fl_Widget*w, void*data) {
		MediaClient *o = (MediaClient *)data;
		o->SearchCallbackD(o, o->ip, o->port);
	}
	
	
	/**
	 * static method to building tree in main frame
	 * */
	static void TreeCallbackS(Fl_Widget*w, void*data) {
		MediaClient *o = (MediaClient*)data;
		o->TreeCallback(o->da, o->title); //call the instance callback method
	}
	
	/**
	* TreeCallback is a callback for tree selections, deselections, expand or
	* collapse.
	* Functionalities:
	* 	The program will respond to any data on the tree
	*/
	void TreeCallback(Json::Value da, Json::Value title) {

		Fl_Tree_Item *item = (Fl_Tree_Item*)tree->item_clicked();
		cout << "Tree callback. Item selected: ";
		if (item) {
			cout << item->label();
		}
		else {
			cout << "none";
		}
		cout << endl;
		std::string aStr("unknown");
		std::string aTitle(item->label());
		switch (tree->callback_reason()) {  // reason callback was invoked
		case       FL_TREE_REASON_NONE: {aStr = "none"; break; }
		case     FL_TREE_REASON_OPENED: {aStr = "opened"; break; }
		case     FL_TREE_REASON_CLOSED: {aStr = "closed"; break; }
		case   FL_TREE_REASON_SELECTED: {
			aStr = "selected";
			Json::Value valTr;
			Json::Value valAl;
			//test code for new method
			
			
				cout << "trying to get: " << item->label() << endl;
				valTr = trackClickData[aTitle];
//				valAl = add_TREE_COL
				try {
				if(valTr.size() < 1) {
					///////////////
					string js = add_TREE_COL_C(aTitle);//("Roads Untraveled");
					int z = js.length();
					char char_ar[z + 1];
					strcpy(char_ar, js.c_str());
					int echoStringLen = strlen(char_ar); 
					valAl =  getResult(char_ar, echoStringLen, ip, port );
					//cout << valAl << endl;
		
					string dura = valAl["duration"].asString();
					string cont = covertTime(dura,  2) ;
					char charD[cont.size() + 1];
					strcpy(charD, cont.c_str());
					
					timeInput->value(charD);
					summaryMLI->value(valAl["summary"].asCString());
					albumInput->value(aTitle.c_str());
					
					
					genreChoice->clear();
					for (int i = 0; i < valAl["genre"].size(); i++) {
						genreChoice->add(valAl["genre"][i].asCString());
					}
					genreChoice->value(1);
					try {
						std::string url = valAl["image"].asCString();
						std::ostringstream os;
						curlpp::Easy myRequest;
						myRequest.setOpt(new curlpp::options::WriteStream(&os));
						myRequest.setOpt(new curlpp::options::Url(url.c_str()));
						myRequest.perform();
						string aString = os.str();
						int n = url.length();
						char char_array[n + 1];
						download_image(strcpy(char_array, url.c_str()));
						Fl_PNG_Image *test = new Fl_PNG_Image("bin/out.png");

						box->image(test);
						box->redraw();

					}
					catch (curlpp::LogicError & e) {
						std::cout << e.what() << std::endl;
					}
					catch (curlpp::RuntimeError & e) {
						std::cout << e.what() << std::endl;
					}
					trackInput->value("");
					rankInput->value("");
				
				
					
					
				} else {
					trackInput->value(valTr["title"].asCString());
					albumInput->value(valTr["album"].asCString());
					authorInput->value(valTr["author"].asCString());
					
					try {
						rankInput->value(valTr["rank"].asCString());
					} catch (std::exception & e) {
						rankInput->value("0");
						std::cout << e.what() << std::endl;
					}
					
					summaryMLI->value(valTr["summary"].asCString());
					
					string dura = valTr["duration"].asString();
					string cont = covertTime(dura,  1) ;
					char charD[cont.size() + 1];
					strcpy(charD, cont.c_str());
					
					timeInput->value(charD);
					genreChoice->clear();
					
					try {
						for (int i = 0; i < valTr["genre"].size(); i++) {
						genreChoice->add(valTr["genre"][i].asCString());
						}
					} catch (std::exception & e) {
						genreChoice->add("unknown");
						std::cout << e.what() << std::endl;
					}
					
					
					

					genreChoice->value(1);
					
					try {
						std::string url = valTr["image"].asCString();
						std::ostringstream os;
						curlpp::Easy myRequest;
						myRequest.setOpt(new curlpp::options::WriteStream(&os));
						myRequest.setOpt(new curlpp::options::Url(url.c_str()));
						myRequest.perform();
						string aString = os.str();
						int n = url.length();
						char char_array[n + 1];
						download_image(strcpy(char_array, url.c_str()));
						Fl_PNG_Image *test = new Fl_PNG_Image("bin/out.png");

						box->image(test);
						box->redraw();

					}
				
					catch (curlpp::LogicError & e) {
						std::cout << e.what() << std::endl;
					}
					catch (curlpp::RuntimeError & e) {
						std::cout << e.what() << std::endl;
					}
				}
				} catch (std::exception & e) {
					std::cout << e.what() << std::endl;
				}
			}
			break;
		
		case FL_TREE_REASON_DESELECTED: {aStr = "deselected"; break; }
		default: {break; }
		}
		cout << "Callback reason: " << aStr.c_str() << endl;
	}
	
	/**
	 * Build Tree method
	 * this is initiated right after the program is executed
	 * buildtree will generate a list of existed data in .json file and then display it on tree
	 * elements on the tree are clickable
	 * assume a track name believe
	 * 	if click on element believe then the relative data will be disply on other component of GUI 
	 * 
	 * */
	void buildTree(string ip,  unsigned short port) {
		const int RCVBUFSIZE = 4096;    // Size of receive buffer
		vector<string> titleList;
		Json::Value masterData;
		try {
			
			///////////////
			string js = getTitle_EXIST_C();//("Roads Untraveled");
			int z = js.length();
			char char_ar[z + 1];
			strcpy(char_ar, js.c_str());
			int echoStringLen = strlen(char_ar); 
			Json::Value title_Exsit =  getResult(char_ar, echoStringLen, ip, port );
			cout<<"Test Tree: "<< title_Exsit << endl;
			//build tree
			//get titlExist
			//add tree
			//cout << title_Exsit << endl;
			
			for (string id : title_Exsit.getMemberNames()) {
						//std::cout << id << std::endl;
						titleList.push_back(title_Exsit[id].asString());
			}
			
			
			/////////////
			
			for(int i = 0; i < titleList.size(); i++) {
				
				cout << "cp" << endl;
				cout << "cp" << titleList[i] << endl;
				js = add_TREE_C(titleList[i]);//("Roads Untraveled");
				z = js.length();
				cout << "cp 1" << endl;
				char_ar[z + 1];
				strcpy(char_ar, js.c_str());
				echoStringLen = strlen(char_ar); 
				cout << echoStringLen << endl;
				Json::Value addTree =  getResult(char_ar, echoStringLen, ip, port );
				cout << "cp 2" << endl;
				masterData[titleList[i]] = addTree;
			}
			cout << "Rebuilding Tree" << endl;
			tree->clear();
			for (int i = 0; i < titleList.size(); i++) {
				cout << " " << titleList[i];
				string res = titleList[i];
				Json::Value md = masterData[titleList[i]];
				std::stringstream stream;
				stream << "Music"
					<< "/"
					<< md["album"].asString()
					<< "/" << md["title"].asString();
				tree->add(stream.str().c_str());
			}
			cout << endl;
			tree->redraw();
		} catch (JsonRpcException e) {
			cout << e.what() << endl;
		}
		
		trackClickData = masterData;
	}

	/**
	 * static method to building tree in search dialog
	 * ex: after album is sucessfully found the dialog will generate a list of track
	 * in the album as tree
	 *
	 * */
	static void TreeCallbackD(Fl_Widget *w, void *data) {
		MediaClient *o = (MediaClient*)data;
		o->TreeCallbackD(o); //call the instance callback method
	}

	/**
	 * 	helper method for static TreeCallbackD
	 *  user can click on any data exist in the tree
	 * 	after clicked, addtional track dialog will be generated. It will ask for either add track or close the dialog
	 * */
	void TreeCallbackD(MediaClient * o) {
		// Find item that was clicked
		Fl_Tree_Item *item = (Fl_Tree_Item*)treeD->item_clicked();
		cout << "Tree callback. Item selected: ";
		if (item) {
			cout << item->label();
		}
		else {
			cout << "none";
		}
		cout << endl;
		std::string aStr("unknown");
		std::string aTitle(item->label());
		switch (treeD->callback_reason()) {  // reason callback was invoked
		case       FL_TREE_REASON_NONE: {aStr = "none"; break; }
		case     FL_TREE_REASON_OPENED: {aStr = "opened"; break; }
		case     FL_TREE_REASON_CLOSED: {aStr = "closed"; break; }
		case   FL_TREE_REASON_SELECTED: {
			aStr = "selected";
			//Album md;
		//	if (library) {
		string test = "ROOT";
		string slec = item->label();
				cout << "trying to get: " << item->label() << endl;
				if(slec.compare(test) == 0) {
					cout << "Selected Item is Root - No proceed" << endl;
				} else {
					o->dialogTrack->show();
					o->arInputTrack->value(o->arInputD->value());
					//o->InputTrack->value(item->label());
					o->alInputTrack->value(o->alInputD->value());
					//md = library->get(aTitle);
					trackClick(o->arInputD->value(), item->label(), lastfmkey, o);
					cout << "Test Key: ";
					cout << lastfmkey << endl;
				}
			break;
		}
		case FL_TREE_REASON_DESELECTED: {aStr = "deselected"; break; }
		default: {break; }
		}

	}
	
	/**
	 * 	track Click Method
	 *  initiate by Album dialog
	 *  purpose: fire event on track click
	 *  event such as adding text to input in the track Dialog
	 * */
	void trackClick(string artist, string track, string key, MediaClient * o) {
		/*add_TRACKDIALOG
		(String artist, String title, String key, String album)
		*/
		std::string searchAl = o->alInputD->value();
		vector<string> dat;

		
		///////////////
			string js = add_TRACKDIALOG(artist, track, key, searchAl);//("Roads Untraveled");
			int z = js.length();
			char char_ar[z + 1];
			strcpy(char_ar, js.c_str());
			int echoStringLen = strlen(char_ar); 
			Json::Value jsn = getResult(char_ar, echoStringLen, ip, port );
			cout << jsn << endl;
			///////////
			
			cout << "TEST JSN: " << jsn << endl;
			string test = jsn["none"].asString();
			string testSub = "nothing";

			if(test.compare(testSub) == 0) {
				cout << "TRAPPED" << endl;
					o->duInputTrack->value("Track Contain Invalid Characters");
					o->trInputTrack->value("Track Contain Invalid Characters");
					o->rkInputTrack->value("Track Contain Invalid Characters");
					o->geInputTrack->value("Track Contain Invalid Characters");
			}
			else {
				try {

					Json::Value track = jsn;
					Json::Value trackName = track["title"];
					Json::Value trackDu = track["duration"];
					Json::Value trackAlArr = track["album"];
					Json::Value trackRank = track["rank"];
					Json::Value trackGe = track["genre"];
					/*for (int i = 0; i < trackGe.size(); i++) {
						Json::Value ge = trackGe[i];
						o->genreData.push_back(ge.asCString());
					}
					std::string genStr;
					for (int i = 0; i < genreData.size(); i++) {

						genStr += ", " + genreData[i];
					}*/
					o->duInputTrack->value(trackDu.asCString());
					o->trInputTrack->value(trackName.asCString());
					o->rkInputTrack->value(trackRank.asCString());
					o->geInputTrack->value(trackGe.asCString());

				}
				catch (curlpp::LogicError & e) {
					std::cout << e.what() << std::endl;
				}
				catch (curlpp::RuntimeError & e) {
					std::cout << e.what() << std::endl;
				}
			}
	}
	
	/**
	 * Close Dialog method
	 * Affected component: Track dialog, the one that appear when you click on track list which is generated by search
	 * */
	static void closeT(Fl_Widget*w, void*data) {
		MediaClient *o = (MediaClient*)data;
		o->dialogTrack->hide();
	}
	
	
	/**
	 * static method add Track
	 * activate after user click on track name in on tree in search dialog
	 * */
	static void addTrack(Fl_Widget *w, void *data) {
		MediaClient *o = (MediaClient*)data;
		o->addTrack(o, o->trackURL, o->summary, o->trackData);
	}
	/**
	 * 	Helper method ofr addTrack
	 * */
	void addTrack(MediaClient * o, string trackURL, string summary, Json::Value trackData) {
		cout << "TEST ADD SINGLE: " << trackData << endl;
		string test = o->trInputTrack->value();
		string testSub = "Track Contain Invalid Characters";
		if(test.compare(testSub) == 0) {
			cout << "Invalid Track Title" << endl;
		} else {
			string js = add_SINGLE(o->trInputTrack->value(), o->arInputTrack->value(), o->alInputD->value(), o->lastfmkey);//("Roads Untraveled");
			int z = js.length();
			char char_ar[z + 1];
			strcpy(char_ar, js.c_str());
			int echoStringLen = strlen(char_ar); 
			getResult_NonJson(char_ar, echoStringLen, ip, port );
			o->msgDialog->show();
		}
		
	}
	
	
	/**
	 * static method add Album
	 * */
	static void addAlbum(Fl_Widget *w, void *data) {
		MediaClient *o = (MediaClient*)data;
		o->addAlbum(o);
	}
	
	
	/**
	 * Helper method for add Album
	 * After the list of track is generated on the tree if click on add Album
	 * All current data on the tree will be added to json
	 * */
	void addAlbum(MediaClient * o) {
		string js = add_ALL(o->alInputD->value(), o->arInputD->value(), o->lastfmkey);
		int z = js.length();
		char char_ar[z + 1];
		strcpy(char_ar, js.c_str());
		int echoStringLen = strlen(char_ar); 
		getResult_NonJson(char_ar, echoStringLen, ip, port );
		//buildTree(o->ip, o->port);
		
		o->msgDialog->show();
	}
	
	
	/**
	 * 	static method - menu click
	 * */
	static void Menu_ClickedS(Fl_Widget*w, void*data) {
		MediaClient *o = (MediaClient*)data;
		o->Menu_Clicked(o); //call the instance callback method
	}
	
	/**
	 * 	part of static menu click.
	 *  initiate event on click such as Save, Restore, Refresh, Exit
	 * 				Track/Add, Track/Remove, Album/Add, Album/Revmoe
	 *  Save: save current data in tree to tmp json - creating restore point
	 *  Restore: override data from tmp json to the master data
	 *  Refresh: rebuidling tree
	 *  Exit: exit;
	 * */
	void Menu_Clicked(MediaClient *o) {
		char picked[80];
		menubar->item_pathname(picked, sizeof(picked) - 1);
		string selectPath(picked);
		int select = genreChoice->value();
		cout << "Selected genre: " << ((select == 0) ? "rock" : "blues") << endl;
		// Handle menu selections
		if (selectPath.compare("File/Save") == 0) {
			cout << "[MusicBrower] - Hit add Save on Menu" << endl;
			o->saveTemporaryData(o);
		}
		else if (selectPath.compare("File/Restore") == 0) {
			cout << "[MusicBrower] - Hit add Restore on Menu" << endl;
			o->restoreData(o);
		//	buildTree2(host);
		}
		else if (selectPath.compare("File/Tree Refresh") == 0) {
			cout << "[MusicBrower] - Hit add Refresh on Menu" << endl;
			buildTree(o->ip, o->port);
		}
		else if (selectPath.compare("File/Exit") == 0) {
			cout << "[MusicBrower] - Hit add Exit on Menu" << endl;
			exit(0);
		}
		else if (selectPath.compare("Track/Add") == 0) {
			cout << "[MusicBrower] - Hit add Track on Menu" << endl;
			o->SearchCallbackS(o);
		}
		else if (selectPath.compare("Track/Remove") == 0) {
			trackRemove(o);													///TRACK REMOVE
		}
		else if (selectPath.compare("Album/Remove") == 0) {
			albumRemove(o);
		}
		else if (selectPath.compare("Album/Add") == 0) {
			cout << "Hit add album" << endl;
			o->SearchCallbackS(o);
		}
		else if (selectPath.compare("Track/Play") == 0) {
			cout << "Play is not implemented" << endl;
		}
	}
	
	
	/**
	 * 	Track remove method
	 *  To Use:
	 * 			Click on a track that currently presented on the Tree
	 * 			Then choose Track Remove option from Menu
	 * */

	void trackRemove(MediaClient *o) {
		
		try {
			string track = trackInput->value();
			const int RCVBUFSIZE = 4096;    // Size of receive buffer
			string js = removeTrack(track);//("Roads Untraveled");
			int z = js.length();
			char char_ar[z + 1];
			strcpy(char_ar, js.c_str());
			int echoStringLen = strlen(char_ar); 
			getResult_NonJson(char_ar, echoStringLen, o->ip, o->port );
			//buildTree(o->ip, o->port);
		} catch (JsonRpcException e) {
			cout << e.what() << endl;
		}
		o->msgDialog->show();
	}
	
	
	/**
	 * 	Album remove method
	 *  To Use:
	 * 			Click on a album that currently presented on the Tree
	 * 			Then choose Album Remove option from Menu
	 * */

	void albumRemove(MediaClient *o) {
		try {
			string track = albumInput->value();
			const int RCVBUFSIZE = 4096;    // Size of receive buffer
			string js = removeAlbum(track);//("Roads Untraveled");
			int z = js.length();
			char char_ar[z + 1];
			strcpy(char_ar, js.c_str());
			int echoStringLen = strlen(char_ar); 
			getResult_NonJson(char_ar, echoStringLen, o->ip, o->port );
		//	buildTree(o->ip, o->port);
		} catch (JsonRpcException e) {
			cout << e.what() << endl;
		}
		o->msgDialog->show();
	}
	
	
	/**
	 * 	This method save the existing data from media.json to tmp.json
	 * 	Purpose is to create new save point.
	 *  Update: this method have been reverted to the similar function with treefresh
	 * */
	void saveTemporaryData(MediaClient *o) {
		//this method is reverted back to the similar function like refresh
		buildTree(o->ip, o->port);
	}
	
	/**
	 * 	Purpose is to restore library from saved point.
	 *  Update: this method have been reverted to the similar function with treefresh
	 * */
	void restoreData(MediaClient *o) {
		//this method is reverted back to the similar function like refresh
		buildTree(o->ip, o->port);
	}
	
	
	
		/**
	 * This method allow the program to download image from URL link then save to local directory
	 * allowing fltk gui to retrieve image data.
	 * image will be saved in bin folder
	 * */
	bool download_image(char* url) {
		FILE* fp = fopen("bin/out.png", "wb");
		if (!fp)
		{
			printf("!!! Failed to create file on the disk\n");
			return false;
		}
		CURL* curlCtx = curl_easy_init();
		curl_easy_setopt(curlCtx, CURLOPT_URL, url);
		curl_easy_setopt(curlCtx, CURLOPT_WRITEDATA, fp);
		curl_easy_setopt(curlCtx, CURLOPT_FOLLOWLOCATION, 1);
		CURLcode rc = curl_easy_perform(curlCtx);
		if (rc) {
			printf("!!! Failed to download: %s\n", url);
			return false;
		}
		long res_code = 0;
		curl_easy_getinfo(curlCtx, CURLINFO_RESPONSE_CODE, &res_code);
		if (!((res_code == 200 || res_code == 201) && rc != CURLE_ABORTED_BY_CALLBACK)) {
			printf("!!! Response code: %d\n", res_code);
			return false;
		}
		curl_easy_cleanup(curlCtx);
		fclose(fp);
		return true;
	}


	
	 /**
	 string summary Store summary data; it is intended to be global, value will be change when different search event is trigger 
	 * */
	 string summary;

	/**
	 *  This is a helper class for search Dialog and add data method
	 * 	get Summary method: method is activated right after usr click on search button in the search dialog
	 *  In short it search for the summary of the album then save the value to global variable summary
	 *  @param root as Json::Value is a value that is generated after search is clicked
	 * 
	 * */

	string getSummary(Json::Value root) {
		Json::Value a1 = root["wiki"]["summary"];
		summary = a1.asCString();
		return summary;
	}
		
	
	/**
	 * Helper method and Helper variable
	 * */
	string trackURL;
	Json::Value trackData;
	string getTrackURL(string url) {
		trackURL = url;
		return trackURL;
	}
	/**
	 * Get json value track Dat
	 * */
	Json::Value getTrackData(Json::Value track) {
		trackData = track;
		return trackData;
	}
	/****/
	std::string lastfmkey; //"eab1524872bc511168bc279f219c9d04";
	std::string userId;
	std::string host;
	MediaClient(string h, string name, string key, string ip2,  int port2) : MediaClientGui() {
		ip = ip2;
		port = port2;
		
		host = h;
		lastfmkey = key;
		/////////////
		/*
		if (key.size() < 1) {
			lastfmkey = "eab1524872bc511168bc279f219c9d04";
		} else {
			cout << "Reached: "<< key.size() << endl;
			lastfmkey = trim(key);
			cout << "After Trim: " << lastfmkey.size() << endl;
		}
		*/
		userId = name;

		try {
		} catch (JsonRpcException e) {
			cout << e.what() << endl;
			da = "";
			title = "";
		}
		
		 btnExitD->callback(closeD, (void*)this);
		 searchButt->callback(SearchCallbackS, (void*)this);
		 btnSearchD->callback(SearchCallbackD, (void*)this);
		 tree->callback(TreeCallbackS, (void*)this);
		 treeD->callback(TreeCallbackD, (void*)this);
		 btnExitTrack->callback(closeT, (void*)this);
		 btnAddTrack->callback(addTrack, (void*)this);
		 btnAddAlbumD->callback(addAlbum, (void*)this);
		 menubar->callback(Menu_ClickedS, (void*)this);
		 
		 btnConfirm->callback(closeMessage, (void*)this);
		 callback(ClickedX);
		// cout << title << endl;
		 buildTree(ip2, port2);
		// buildTree2(host);
	}
	
	std::string trim(const std::string& input) {
    std::stringstream string_stream;
    for (const auto character : input) {
        if (!isspace(character)) {
            string_stream << character;
        }
    }

    return string_stream.str();
	}
	
	
	
/**
 * Code in this section
 * Stub that allow the program to properly communicate with server
 * */
	
	/**
	 * generate header for stud
	 * such as {metod="". id=""...}
	 * */
	std::string createJsonHeader(string method){
		string meth = method;
		string header = "{\"method\":\"" + meth + "\",\"id\":0,\"jsonrpc\":\"2.0\",\"";
		//params\":[\""+ a +"\"]}	//remain
		return header;
	}
	
	/**
	 * generate full stud string including require parameters
	 * add_Search_C function
	 * trigger when perform search
	 * */
	std::string add_SEARCH_C(string artist, string album, string key) {
		string header = createJsonHeader("add_SEARCH_C");
		string param = "params\":[\""+ artist +"\",\""+album+"\",\""+key+"\"]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * addTreeGenre function
	 * trigger when perform tree build
	 * */
	std::string addTreeGenre(string title) {
		string header = createJsonHeader("add_TREE_genre");
		string param = "params\":[\""+ title +"\"]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * add_TRACKDIALOG function
	 * trigger when perform ignite track dialog - click on it
	 * */
	std::string add_TRACKDIALOG(string artist, string title, string key, string album) {
		string header = createJsonHeader("add_TRACKDIALOG");
		string param = "params\":[\""+ artist +"\",\""+title+"\",\""+key+"\",\""+album+"\"]}";
		string rem = header + param;
		return rem;
	}
	
	
	/**
	 * generate full stud string including require parameters
	 * method generate require json data
	 * */
	std::string add_TREE_obj(string title) {
		string header = createJsonHeader("add_TREE_obj");
		string param = "params\":[\""+ title +"\"]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * add_TREE_C function
	 * trigger on upon tree build
	 * */
	std::string add_TREE_C(string title) {
		cout << "HIT HERE" << endl;
		cout << title << endl;
		string header = createJsonHeader("add_TREE_C");
		string param = "params\":[\""+ title +"\"]}";
		string rem = header + param;
		cout << rem << endl;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * add_TREE_COL_C function
	 * trigger on upon tree build
	 * */
	std::string add_TREE_COL_C(string title) {
		string header = createJsonHeader("add_TREE_COL_C");
		string param = "params\":[\""+ title +"\"]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * removeTrack function
	 * trigger on upon remove track
	 * */
	std::string removeTrack(string title) {
		string header = createJsonHeader("removeTrack");
		string param = "params\":[\""+ title +"\"]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * removeAlbum function
	 * trigger on upon remove album
	 * */
	 
	std::string removeAlbum(string title) {
		string header = createJsonHeader("removeAlbum");
		string param = "params\":[\""+ title +"\"]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * getImage function
	 * trigger on upon add track or add album
	 * */
	 
	std::string getImage(string title) {				///NO PARAM
		string header = createJsonHeader("getImage");
		string param = "params\":[\""+ title +"\"]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * getImage function
	 * trigger on upon add track or add album
	 * */
	 
	std::string getSummary(string title) {				///NO PARAM
		string header = createJsonHeader("getSummary");
		string param = "params\":[\""+ title +"\"]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * getTrackList_C function
	 * */
	 
	std::string getTrackList_C() {
		string header = createJsonHeader("getTrackList_C");	///NO PARAM
		string param = "params\":[]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * getTitle_EXIST_C function
	 * get existing title in json file
	 * */
	 
	std::string getTitle_EXIST_C() {
		string header = createJsonHeader("getTitle_EXIST");	///NO PARAM
		string param = "params\":[]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * add_SINGLE function
	 * trigger on upon add track
	 * */
	 
	std::string add_SINGLE(string title, string artist, string album, string key) {
		string header = createJsonHeader("add_SINGLE");
		string param = "params\":[\""+ title +"\",\""+artist+"\",\""+album+"\",\""+key+"\"]}";
		string rem = header + param;
		return rem;
	}
	
	/**
	 * generate full stud string including require parameters
	 * add_ALL function
	 * trigger on upon a add album
	 * */
	 
	std::string add_ALL(string album, string artist, string key){ 
		string header = createJsonHeader("add_ALL");
		string param = "params\":[\""+ album +"\",\""+artist+"\",\""+key+"\"]}";
		string rem = header + param;
		return rem;
	}
	
	///END STUD//
	/**
 * 	Method convert time in milisecond to the correct format
 * 	There are 2 option
 * 	option 1: mostly use by data from media.json - time in format mm:ss
 * 	option 2: use by data from mediaAlbum.json - time in format hh:mm:ss
 * */
string covertTime(string duration, int option) {
	cout << duration << endl;
			stringstream number(duration);

		//	cout << "checkpoint 1"<< endl;
			long du = 0;
			number >> du;
			long hr = du / 3600000;
			du = du - 3600000 * hr;
			long min = du / 60000;
			du = du - 60000 * min;
			long sec = du / 1000;
			du = du - 1000 * sec;
			try {
				std::ostringstream ss1;
				ss1 << hr;
				std::string hours = ss1.str();
				std::ostringstream ss2;
				ss2 << min;
				std::string minutes = ss2.str();
				std::ostringstream ss3;
				ss3 << sec;
				std::string second = ss3.str();
		    if(option == 1) {
				cout << "[Server] - [ConvertTime] Reached option 1" << endl;
				string time = minutes + ":" + second;
				cout << "[Server] - [ConvertTime] Converted Time is: " << time << endl;
				return time;
			} else if (option == 2) {
				cout << "[Server] - [ConvertTime] Reached option 2" << endl;
				string time = hours+ ":" + minutes + ":" + second;
				cout << "[Server] - [ConvertTime] Converted Time is: " << time << endl;
				//cout << time << endl;
				return time;
			} else {
				string nu = "0";
				return nu;
			}
			} catch (std::exception & e) {
				//newData["rank"]  = "NULL rank";
				 std::cout << e.what() << std::endl;
				 string nu = "0";
				 return nu;
			}
			string nu = "0";
		    return nu;
}


	
	/**
	 * This is similar to getResult in Java where finalized data is going to send to server and 
	 * receive from the server
	 * this program will take received data as json then manipulate it
	 * */
	Json::Value getResult(char *char_ar, int echoStringLen, string ip, int port) {
		cout << "Echo: " << endl;
		const int RCVBUFSIZE = 4096;    // Size of receive buffer
		Json::Value result;
		Json::Value finalRes;
		try {
	   // Establish connection with the echo server
			TCPSocket sock(ip, port);
			// Send the string to the echo server
			sock.send(char_ar, echoStringLen);
			char echoBuffer[RCVBUFSIZE + 1];    // Buffer for echo string + \0
			int bytesReceived = 0;              // Bytes read on each recv()
			int totalBytesReceived = 0;         // Total bytes read
			// Receive the same string back from the server
			cout << "[Received]: " << endl;               // Setup to print the echoed string
			while (totalBytesReceived < echoStringLen) {
			  // Receive up to the buffer size bytes from the sender
			  if ((bytesReceived = (sock.recv(echoBuffer, RCVBUFSIZE))) <= 0) {
				cerr << "Unable to read";
				exit(1);
			  }
			  totalBytesReceived += bytesReceived;     // Keep tally of total bytes
			  echoBuffer[bytesReceived] = '\0';        // Terminate the string!
			//  cout << echoBuffer;                      // Print the echo buffer
			
			  result = echoBuffer;
			  
			  cout << "TEST: "<<result  << endl;
			  string opt =  "{\"id\":0,\"jsonrpc\":\"2.0\"}";
			  string opt2 = "{\"result\":[],\"id\":0,\"jsonrpc\":\"2.0\"}";
			  string opt3 = "{\"result\":{},\"id\":0,\"jsonrpc\":\"2.0\"}";
			  string opt4 = "{}";
			  if(result.compare(opt) == 0 || result.compare(opt2) == 0 || result.compare(opt3) == 0 || result.compare(opt4) == 0) {
				  if(result.compare(opt3) == 0 || result.compare(opt4) == 0) {
					  cout << "HIT - opt 3" << endl;
					finalRes["none"] = "nothing";
					return finalRes;
				  } else {
					cout << "HIT - other opt" << endl;
					//finalRes["none"] = "nothing";
					return finalRes;
				  }
			  } else {
				  string text = result.asCString();
				  Json::Value root;
				  Json::Reader reader;
				  bool parsingSuccessful = reader.parse( text, root );
				  if ( !parsingSuccessful )
				  {
					cout << "Error parsing the string" << endl;
				  }
				  for( Json::Value::const_iterator outer = root.begin() ; outer != root.end() ; outer++ )
				  {
					for( Json::Value::const_iterator inner = (*outer).begin() ; inner!= (*outer).end() ; inner++ )
					{
					 finalRes[inner.key().asString()] = *inner;
					}
				  }
				  return finalRes;
				}
			}
		   } catch (JsonRpcException e) {
				cout << e.what() << endl;
		   }
		   return finalRes;
	}
	
	/**
	 * This is similar to getResult in Java where finalized data is going to send to server and 
	 * receive from the server
	 * this program will take received data as json then manipulate it
	 * Differ: there will be no received data becuase methods call this getResult are boolean methods 
	 * */
	void getResult_NonJson(char *char_ar, int echoStringLen, string ip, unsigned short port) {
		try {
	   // Establish connection with the echo server
			TCPSocket sock(ip, port);
			// Send the string to the echo server
			sock.send(char_ar, echoStringLen);
		   } catch (JsonRpcException e) {
				cout << e.what() << endl;
		   }

	}
};

int main(int argc, char*argv[]) {
   //string host = "http://127.0.0.1:8080";
   //string port = "8080";
   const int RCVBUFSIZE = 4096;    // Size of receive buffer
   std::string host = (argc > 1) ? argv[1]: "192.168.1.1";
   std::string portStr = (argc > 2) ? argv[2]: "8080";
   std::string developer = (argc > 3) ? argv[3] : "Duc.Nguyen";
   std::string lastfmkey = (argc > 4) ? argv[4] : "eab1524872bc511168bc279f219c9d04";
   std::string windowTitle = developer + "'s Music Browser";
	
   int port = std::stoi(portStr);
   
   cout << "Host: " << host << endl;
   cout << "Port: " << port  << endl;
   cout << "Name: " << developer << endl;
   cout << "Key: " << lastfmkey << endl;
   
   string url = "http://" + host; // + ":" + port;
   try {
			MediaClient cm(url, developer, lastfmkey, host ,port);
		
		return (Fl::run());
   } catch (JsonRpcException e) {
		cout << e.what() << endl;
   }	

}



