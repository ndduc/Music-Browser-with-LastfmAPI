#include <FL/Fl.H>
#include <FL/Fl_Window.H>
#include <FL/Fl_Button.H>
#include <FL/Fl_Output.H>
#include <FL/Fl_Tree.H>
#include <FL/Fl_Multiline_Input.H>
#include <FL/Fl_Tree_Item.H>
#include <FL/Fl_Menu_Bar.H>
#include <FL/Fl_Choice.H>
#include <FL/Fl_Text_Display.H>
#include <FL/Fl_Text_Buffer.H>
#include <FL/Fl_Box.H>
#include <FL/Fl_PNG_Image.H>
#include <FL/Fl_Image.H>
#include <stdio.h>
#include <iostream>
#include <stdlib.h>
#include <vector>
#include <jsonrpccpp/client/connectors/httpclient.h>
using namespace std;

/**
 * Copyright (c) 2020 Duc Nguyen,
 * Software Engineering,
 * Arizona State University at the Polytechnic campus
 * <p/>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation version 2
 * of the License.
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but without any warranty or fitness for a particular purpose.
 * <p/>
 * Please review the GNU General Public License at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 * see also: https://www.gnu.org/licenses/gpl-faq.html
 * so you are aware of the terms and your rights with regard to this software.
 * Or, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,USA
 * <p/>
 * Purpose: Sample C++ FLTK view class. MediaClientGui constructs the view
 * for media app. This class is extended by the client controller which is
 * the MediaClient class. MediaClient defines the call-backs for UI controls.
 * It contains sample control functions that respond to button clicks and tree
 * selects.
 * This software is meant to run on Debian Wheezy Linux
 * <p/>
 * Ser321 Principles of Distributed Software Systems
 * see http://pooh.poly.asu.edu/Ser321
 * @author Duc Nguyen - Developed based on Linquist Design
 * @file    MediaClientGui.cpp
 * @date    02/08/2020
 **/
class MediaClientGui : public Fl_Window {
protected:
	vector<string> genreData;
	
	
	Fl_Window* msgDialog;
	Fl_Button* btnConfirm;
	//Message Dialog
	
	
	//These are component for Second Search Dialog
	Fl_Window* dialog;
	Fl_Input * alInputD;
	Fl_Input * arInputD;
	Fl_Button * btnSearchD;
	Fl_Button * btnExitD, *btnAddAlbumD;
	Fl_Tree * treeD;
	
	//These are component for Track Dialog
	/********************/
	Fl_Window* dialogTrack;
	Fl_Input * alInputTrack, *arInputTrack, * duInputTrack, *trInputTrack, *rkInputTrack, *fnInputTrack, *geInputTrack;
	Fl_Button * btnAddTrack;
	Fl_Button * btnExitTrack;
	

   Fl_Box * box;
   Fl_PNG_Image * png;

   /**
    * tree is the Fl_Tree object that occupies the left side of the window.
    * this tree control provides the ability to add and remove items and to
    * manipulate and query the tree when an exception occurs.
    */
   Fl_Tree * tree;

   /**
    * titleInput is the Fl_Input object labelled Title
    * Its to display, or for the user to enter
    * the album's track tile.
    */
   Fl_Input * trackInput;
   

   /**
    * albumInput is the Fl_Input object labelled Album
    * Its for the user to enter the media album. For videos, genre is
    * used to organize video, and album is ignored.
    */
   Fl_Input * albumInput;

   /**
    * authorInput is the Fl_Input object labelled Artist
    * Its for the user to enter the artist of the track/album
    */
   Fl_Input * authorInput;

   /**
    * rankInput is the Fl_Input object labelled Rank
    * Its for the display and entry of a track's rank within the album.
    */
   Fl_Input * rankInput;

   /**
    * timeInput is the Fl_Input object labelled Time.
    * It provides the display of album or track play time in form: hh:mm:ss
    */
   Fl_Input * timeInput;

   /**
    * genreChoice is the Fl_Choice object labelled Genre.
    * It provides for the display of the album genre.
    */
   Fl_Choice * genreChoice;

   /**
    * summaryMLI is the Fl_Multiline_Input object in the lower right panel.
    * It provides for the display and changing an album's summary.
    */
   Fl_Multiline_Input * summaryMLI;

   /**
    * fileNameInput is the Fl_Input object labelled File Name.
    * It provides the display or entry of mp3 file associated with a track.
    */
   Fl_Input * fileNameInput;

   /**
    * albSrchInput is the Fl_Input object labelled Album in the left panel.
    * It provides entry for album last.fm search.
    */
   Fl_Input * albSrchInput;

   /**
    * artSrchInput is the Fl_Input object labelled Artist.
    * It provides the display or entry of artist for last.fm search
    */
   Fl_Input * artSrchInput;

   /**
    * searchButt is the Fl_Button object labelled Search.
    * After the user enters album (albSrchInput) and artist (artSrchInput)
    * information, the searchButt is clicked to initiate a last.fm album
    * search. The results of the search are parsed and displayed in the tree.
    */
   Fl_Button * searchButt;

   /**
    * menubar is the Fl_Menu_bar object with menus: File,Album,Track
    */
   Fl_Menu_Bar *menubar;

public:
   //MediaClientGui(const char * name = "Ser321") : Fl_Window(635,350,name) {
   MediaClientGui(const char * name = "Ser321") : Fl_Window(980,500,name) {
      begin();

      menubar = new Fl_Menu_Bar(0, 0, this->w(), 25);
      menubar->add("File/Save");
      menubar->add("File/Restore");
      menubar->add("File/Tree Refresh");
      menubar->add("File/Exit");
      menubar->add("Album/Add");
      menubar->add("Album/Remove");
      menubar->add("Album/Play");
      menubar->add("Track/Add");
      menubar->add("Track/Remove");
      menubar->add("Track/Play");

      albSrchInput = new Fl_Input(55, 35, 220, 25);
      albSrchInput->label("");
      albSrchInput->value("");

      artSrchInput = new Fl_Input(50, 80, 150, 25);
      artSrchInput->label("");
      artSrchInput->value("");

      searchButt = new Fl_Button(220, 80, 90, 25,"Search");

      // create a tree control at position x=10, y=10. Its 150 pixels wide
      // and window height less 20 pixels high. Add some sample tree nodes.
      tree = new Fl_Tree(10, 120, 325, this->h()-135);
      tree->add("Flintstones/Fred");
      tree->add("Flintstones/Wilma");
      tree->close("/Flintstones");

      /*
       * add a text input control at x=250, y=35 of width 200 pixels and
       * height of 25 pixels. Initialize it contents to media title.
       */
      /*
       * add a text input control at x=250, y=35 of width 200 pixels and
       * height of 25 pixels. Initialize it contents to media title.
       */
      albumInput = new Fl_Input(385, 35, 265, 25);
      albumInput->label("Album");
      albumInput->value("");

      trackInput = new Fl_Input(700, 35, 265, 25);
      trackInput->label("Track");
      trackInput->value("");

      authorInput = new Fl_Input(385, 80, 220, 25);
      authorInput->label("Artist");
      authorInput->value("");

      rankInput = new Fl_Input(680, 80, 60, 25);
      rankInput->label("Rank");
      rankInput->value("");

      timeInput = new Fl_Input(815, 80, 100, 25);
      timeInput->label("Time");
      timeInput->value("");
      
      fileNameInput = new Fl_Input(420, 130, 310, 25);
      fileNameInput->label("File Name");
      fileNameInput->value("");
      
      // create the media genre drop-down (input_choice)
      genreChoice = new Fl_Choice(815, 130, 100, 25, "Genre");
      genreChoice->value(0); // set the control initially to rock

      box = new Fl_Box(350,180,320-20,320-20);     
      png = new Fl_PNG_Image("");      
      box->image(png);

      summaryMLI = new Fl_Multiline_Input(665,180,300,300,0);
	  summaryMLI->align(FL_ALIGN_WRAP);
	  summaryMLI->wrap(1);    
      end();
      show();
      /*************Dialog Panel*******************/
		dialog = new Fl_Window(400, 400);
	   
		dialog->begin();
	   
		alInputD = new Fl_Input(79, 20, 220, 25);
		arInputD = new Fl_Input(70, 70, 150, 25);
		btnSearchD = new Fl_Button(20, 110, 120,30, "Search Album");
		btnAddAlbumD = new Fl_Button(150,110,100,30, "Add Album");
		btnExitD = new Fl_Button(260,110,60,30, "Close");

		alInputD->label("Album: ");
		alInputD->value("");
		
		treeD = new Fl_Tree(10, 160, 380, 200);
		
		arInputD->label("Artist: ");
		arInputD->value("");
		dialog->end();
		dialog->set_modal();
	//	dialog->show();			//Commented out when finish
		while (dialog->shown()) Fl::wait();
		
		
		/**********************************************/
		dialogTrack = new Fl_Window(400, 350);
		dialogTrack->begin();
		alInputTrack = new Fl_Input(79, 20, 220, 25);
		arInputTrack = new Fl_Input(70, 55, 220, 25);
		duInputTrack = new Fl_Input(100, 90, 220, 25);
		trInputTrack = new Fl_Input(70, 125, 220, 25);
		rkInputTrack = new Fl_Input(70, 160, 220, 25);
		fnInputTrack = new Fl_Input(100, 195, 220, 25);
		geInputTrack = new Fl_Input(70, 230, 220, 25);
		
		alInputTrack->label("Album: ");
		arInputTrack->label("Artist: ");
		duInputTrack ->label("Duration: ");
		trInputTrack ->label("Track: ");
		rkInputTrack ->label("Rank: ");
		fnInputTrack ->label("File Name: ");
		geInputTrack ->label("Genre: ");
		
		
		btnAddTrack = new Fl_Button(20, 265, 100,30, "Add Track");
		btnExitTrack = new Fl_Button(130, 265, 60,30, "Cancel");
		//* duInputTrack, *trInputTrack, *rkInputTrack, *fnInputTrack, *geInputTrack;
		dialogTrack->end();
		dialogTrack->set_modal();
		while (dialog->shown()) Fl::wait();
		//dialogTrack->show();
	//duInputTrack;
	//btnAddTrack;
	//btnExitTrack;
	
	
	
	
		/********************/
		msgDialog = new Fl_Window(400,40);
		msgDialog->begin();
		btnConfirm = new Fl_Button(0, 0, 400 ,40, "Please, refresh tree list to update tree. Confirm Me!");
		msgDialog->end();
		msgDialog->set_modal();
		while(dialog->shown()) Fl::wait();
   }
   
   
   


};
