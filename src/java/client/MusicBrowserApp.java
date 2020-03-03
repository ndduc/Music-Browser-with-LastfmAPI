package ser321.tcpjsonrpc;

import java.util.concurrent.TimeUnit;
import javax.swing.*;
import java.nio.charset.Charset;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.URLConnection;
import java.time.Duration;
import javax.swing.JOptionPane;
import javax.swing.border.Border;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;
import ser321.assign2.lindquis.MediaLibraryGui;

import ser321.tcpjsonrpc.server.Album;
import ser321.tcpjsonrpc.server.AlbumImpl;
import ser321.tcpjsonrpc.server.Track;
import ser321.tcpjsonrpc.server.TrackImpl;
import ser321.tcpjsonrpc.server.MusicLibrary;
import ser321.tcpjsonrpc.server.MusicLibraryImpl;
import ser321.tcpjsonrpc.MusicTcpProxy;
/**
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
 * Purpose: 
 * 
 * MusicBrowserApp is modified based from MediaLibrary App, it ultilized TreeList and Java Swing components
 * Allows user to interact with the system 
 *  Main Features:
 *      Allow user to search through API provided by LasfFM.
 *          Require parameters are Album and Artist Name
 *      Allow user to delete track and album as will
 *      Allow user to add a track if album is found.
 * 
 *  Update 02/26/2020
 * 		add communication feature with proxy
 * 		enable client - server communicate
 *
 *
 * @author Duc Nguyen
 * @version 3 02/26/20202
 */
public class MusicBrowserApp extends MediaLibraryGui implements
                                                       TreeWillExpandListener,
                                       ActionListener,
                                   TreeSelectionListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final boolean debugOn = true;
    private static final String cher = "";// "https://lastfm.freetls.fastly.net/i/u/300x300/3b54885952161aaea4ce2965b2db1638.png";
    private static final String pre = "http://ws.audioscrobbler.com/2.0/?method=album.getinfo&artist=";
    private String url;
    private boolean stopPlaying;         //shared, but not synchronized with playing thread.
   // private MusicLibrary library;
    private String lastFMKey;
    private String host;
    private String port;
    ArrayList<Object> tmp = new ArrayList<>(); //Helper array
    
    
    /**
     * Connstructor 
     * 	initate neccesary varaible and functionalities
     * */
    public MusicBrowserApp(String author, String authorKey,String host, String port) {
          super(author);
          this.lastFMKey = authorKey;
          this.host = host;
          this.port = port;
          trackJTF.setEditable(false);
          timeJTF.setEditable(false);
          albumJTF.setEditable(false);
          authorJTF.setEditable(false);
          rankJTF.setEditable(false);
          fileNameJTF.setEditable(false);
          summaryJTA.setEditable(false);
        //  library = new MusicLibraryImpl();
          stopPlaying = false;
          for(int i=0; i<userMenuItems.length; i++){
             for(int j=0; j<userMenuItems[i].length; j++){
                userMenuItems[i][j].addActionListener(this);
             }
          }
          searchJButt.addActionListener(this);
          try{
             tree.addTreeSelectionListener(this);
             rebuildTree();
          }catch (Exception ex){
             JOptionPane.showMessageDialog(this,"Handling "+
                                           " constructor exception: " + ex.getMessage());
          }
          try{
             setAlbumImage(cher);
          }catch(Exception ex){
             System.out.println("unable to open Cher png");
          }
          setVisible(true);
   }
   /**
    * A method to facilite printing debugging messages during development, but which can be
    * turned off as desired.
    *
    **/
   private void debug(String message) {
      if (debugOn)
         System.out.println("debug: "+message);
   }
   /**
    * Create and initialize nodes in the JTree of the left pane.
    * buildInitialTree is called by MediaLibraryGui to initialize the JTree.
    * Classes that extend MediaLibraryGui should override this method to 
    * perform initialization actions specific to the extended class.
    * The default functionality is to set base as the label of root.
    * In your solution, you will probably want to initialize by deserializing
    * your library and displaying the categories and subcategories in the
    * tree.
    * @param root Is the root node of the tree to be initialized.
    * @param base Is the string that is the root node of the tree.
    */
   public void buildInitialTree(DefaultMutableTreeNode root, String base){
      try{
         root.setUserObject(base);
      }catch (Exception ex){
         JOptionPane.showMessageDialog(this,"exception initial tree:"+ex);
         ex.printStackTrace();
      }
   }
   
   /**
    * 	This allow client to create a connection with with server
    * 
    * */
   public MusicTcpProxy getConenction() {
       MusicTcpProxy sc = (MusicTcpProxy)new MusicTcpProxy(getHost(), Integer.parseInt(getPort()));
       //String[] test =  sc.getTitle_EXIST();
       /*System.out.println("TEST PROXY");
       for(int i = 0; i < test.length;i++ ) {
		   System.out.println(test[i]);
	   }*/
       return sc;
   }
   
   /**
    * 	Rebuilding tree
    * 	method send request to server and received data if request is valid
    * 	data include track and album title
    * 	tree will use these received data to genrate tree list
    * */
   public void rebuildTree() throws Exception{
      tree.removeTreeSelectionListener(this);
      DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
      DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
      clearTree(root, model);
      DefaultMutableTreeNode musicNode = new DefaultMutableTreeNode("Music");
      model.insertNodeInto(musicNode, root, model.getChildCount(root));
     // library = new MusicLibraryImpl();
      
      String[] musicList = getConenction().getTitle_EXIST();
      for (int i = 0; i<musicList.length; i++){

		 JSONObject test = getConenction().add_TREE(musicList[i]).getData();
		 System.out.println("TEST: " + test);
         Track tr = getConenction().add_TREE(musicList[i]);

         String aMTitle = musicList[i];
         DefaultMutableTreeNode toAdd = new DefaultMutableTreeNode(aMTitle);
         DefaultMutableTreeNode subNode = getSubLabelled(musicNode,tr.getAlbumString());
         if(subNode!=null){ 
            model.insertNodeInto(toAdd, subNode,
                                 model.getChildCount(subNode));
         }else{ 
            DefaultMutableTreeNode anAlbumNode =
               new DefaultMutableTreeNode(tr.getAlbumString());
            model.insertNodeInto(anAlbumNode, musicNode,
                                 model.getChildCount(musicNode));
            DefaultMutableTreeNode aSubCatNode = 
               new DefaultMutableTreeNode("aSubCat");
            model.insertNodeInto(toAdd,anAlbumNode,
                                 model.getChildCount(anAlbumNode));
         }
      }
      for(int r =0; r < tree.getRowCount(); r++){
         tree.expandRow(r);
      }
      tree.addTreeSelectionListener(this);
   }
   
   /**
    * 	this method will alow the program clear the current tree list
    * 	the intention is to refresh the tree in search dialog
    * */
   private void clearTree(DefaultMutableTreeNode root, DefaultTreeModel model){
      try{
         DefaultMutableTreeNode next = null;
         int subs = model.getChildCount(root);
         for(int k=subs-1; k>=0; k--){
            next = (DefaultMutableTreeNode)model.getChild(root,k);
         //   debug("removing node labelled:"+(String)next.getUserObject());
            model.removeNodeFromParent(next);
         }
      }catch (Exception ex) {
         System.out.println("Exception while trying to clear tree:");
         ex.printStackTrace();
      }
   }
   
   /**
    * Tree list helper
    * return mutatablenode
    * */
   private DefaultMutableTreeNode getSubLabelled(DefaultMutableTreeNode root,
                                                 String label){
      DefaultMutableTreeNode ret = null;
      DefaultMutableTreeNode next = null;
      boolean found = false;
      for(Enumeration<TreeNode> e = root.children();
          e.hasMoreElements();){
         next = (DefaultMutableTreeNode)e.nextElement();
       //  debug("sub with label: "+(String)next.getUserObject());
         if (((String)next.getUserObject()).equals(label)){
         //   debug("found sub with label: "+label);
            found = true;
            break;
         }
      }
      if(found)
         ret = next;
      else
         ret = null;
      return (DefaultMutableTreeNode)ret;
   }
   
   /**
    * 	tree collapse method
    * */
   public void treeWillCollapse(TreeExpansionEvent tee) {
   //   debug("In treeWillCollapse with path: "+tee.getPath());
      tree.setSelectionPath(tee.getPath());
   }
   
   /**
    * 	Value changed method
    * 	Affected: Tree
    * 	corresponding data will be pulled from server upon a click on tree item
    * 	assume click on ablum name then a corresponded data such as image summary and duration will be sent to client from server and display on textfield
    * */
   public void valueChanged(TreeSelectionEvent e) {
      try{
         tree.removeTreeSelectionListener(this);
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)
            tree.getLastSelectedPathComponent();
         if(node!=null){
            String nodeLabel = (String)node.getUserObject();
            //debug("In valueChanged. Selected node labelled: "+nodeLabel);
            // is this a terminal node?
            if(node.getChildCount() != 0) {
				 try {
                 Album tr = getConenction().add_TREE_COL(nodeLabel);
                 String[] trList = getConenction().add_TREE_COL_genre(nodeLabel);
                 trackJTF.setText("");
                 authorJTF.setText(tr.getAuthor());
                 albumJTF.setText(nodeLabel);
                 fileNameJTF.setText("");
                 rankJTF.setText("");
                 String min = String.format("%d,%d", 
                         TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(tr.getDuration()).longValue()),
                         TimeUnit.MILLISECONDS.toSeconds(Long.valueOf(tr.getDuration()).longValue()) - 
                         TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(tr.getDuration()).longValue()))
                         );
                 timeJTF.setText(min);
                 summaryJTA.setText(tr.getSummary());
                 setAlbumImage(tr.getImage());
                 if(tmp.size() == 0) {
                     for(int i = 0; i < trList.length; i++) {
                         tmp.add(trList[i].toString());
                         genreJCB.addItem(trList[i].toString());
                         }
                 } else {
                     tmp.clear();
                     genreJCB.removeAllItems();
                     for(int i = 0; i < trList.length; i++) {
                         tmp.add(trList[i].toString());
                         genreJCB.addItem(trList[i].toString());
                     }
                 }
				 } catch (Exception eix) {
					System.out.println("[Exception: ] clicked NULL");
					trackJTF.setText("");
					authorJTF.setText("");
					albumJTF.setText("");
					fileNameJTF.setText("");
					rankJTF.setText("");
					timeJTF.setText("");
					summaryJTA.setText("");
					setAlbumImage("");
					genreJCB.removeAllItems();
					genreJCB.addItem("Nothing");
				 } finally {
					
				 }
            }
            if(node.getChildCount()==0 &&
                   (node != (DefaultMutableTreeNode)tree.getModel().getRoot())){
                    Track tr = getConenction().add_TREE(nodeLabel);
                    String[] trList = getConenction().add_TREE_genre(nodeLabel);
                    trackJTF.setText(nodeLabel);
                    authorJTF.setText(tr.getAuthorString());
                    albumJTF.setText(tr.getAlbumString());
                    fileNameJTF.setText("mp3");
                    rankJTF.setText(tr.getRankString());
                    String min = String.format("%d,%d", 
                            TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(tr.getRuntime()).longValue()),
                            TimeUnit.MILLISECONDS.toSeconds(Long.valueOf(tr.getRuntime()).longValue()) - 
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(Long.valueOf(tr.getRuntime()).longValue()))
                            );
                    timeJTF.setText(min);
                    summaryJTA.setText(tr.getSummary());
                    setAlbumImage(tr.getImageString());
                    if(tmp.size() == 0) {
                        for(int i = 0; i < trList.length; i++) {
                            tmp.add(trList[i].toString());
                            genreJCB.addItem(trList[i].toString());
                            }
                    } else {    
                        tmp.clear();
                        genreJCB.removeAllItems();
                        for(int i = 0; i < trList.length; i++) {
                            tmp.add(trList[i].toString());
                            genreJCB.addItem(trList[i].toString());
                        }
                    }
            }
        }
      }catch (Exception ex){
         ex.printStackTrace();
      }
      tree.addTreeSelectionListener(this);
   }
   
   
   /**
    * 	Action perform
    * 	Exit
    * 		exit the program
    * 	Restore
    * 		similar function to tree refresh
    * 	Save 
    * 		Simialr function to tree refresh
    * 	AlbumAdd
    * 		add entire album - an additional dialog will show up then user can proceed futher
    * 	TrackAdd
    * 		add single track - an additional dialog will show up then user can proceed futher
    * 	Search
    * 		perform search - an additional dialog will show up then user can proceed futher
    * 	TreeFresh
    * 		Update tree list - pulling existing data from server and display it on tree list
    * 	Track Remove / Album Remove
    * 		Remove track and album
    * 	
    * 	
    * */
   public void actionPerformed(ActionEvent e) {
        tree.removeTreeSelectionListener(this);
        if(e.getActionCommand().equals("Exit")) {
            System.exit(0);
        }else if(e.getActionCommand().equals("Save")) {
            boolean savRes = false;
        }else if(e.getActionCommand().equals("Restore")) {
            boolean resRes = false;
            try {
                rebuildTree();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            this.revalidate();
            this.repaint();
        }else if(e.getActionCommand().equals("AlbumAdd")) {
            this.setEnabled(false);
            addAlbumDialog(this, lastFMKey);
            try {
                rebuildTree();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            this.revalidate();
            this.repaint();
          }else if(e.getActionCommand().equals("TrackAdd")) {
                addAlbumDialog(this, lastFMKey);
                try {
                    rebuildTree();
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                this.revalidate();
                this.repaint();
          }else if(e.getActionCommand().equals("Search")) {
				addAlbumHelper(artistSearchJTF.getText(), albumSearchJTF.getText());
				addAlbumDialog(this, lastFMKey);
			
            
          }else if(e.getActionCommand().equals("Tree Refresh")) {
            try {
                rebuildTree();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
          }else if(e.getActionCommand().equals("TrackRemove")) {
            int selectedOption = JOptionPane.showConfirmDialog(null, 
                                      "Do you want to remove Track\n" + trackJTF.getText() + "?", 
                                      "Remove Track", 
                                      JOptionPane.YES_NO_OPTION); 
            if (selectedOption == JOptionPane.YES_OPTION) {
                try{
                    getConenction().removeTrack(trackJTF.getText());
                    rebuildTree();
                    this.revalidate();
                    this.repaint();
                } catch (Exception ei){
                    ei.printStackTrace();
                }
            }
        }else if(e.getActionCommand().equals("AlbumRemove")) {
            int selectedOption = JOptionPane.showConfirmDialog(null, 
                                      "Do you want to remove Album\n" + albumJTF.getText() + "?", 
                                      "Remove Album", 
                                      JOptionPane.YES_NO_OPTION); 
            if (selectedOption == JOptionPane.YES_OPTION) {
                try{
                    getConenction().removeAlbum(albumJTF.getText());
                   // getConenction().removeAlbumCol(albumJTF.getText());
                    rebuildTree();
                    this.revalidate();
                    this.repaint();
                } catch (Exception ei) {
                    ei.printStackTrace();
                }
            }
         }
        tree.addTreeSelectionListener(this);
   }
   /**
    *
    * A method to do asynchronous url request printing the result to System.out
    * @param aUrl the String indicating the query url for the lastFM api search
    *
    **/
   public void fetchAsyncURL(String aUrl){
      try{
         HttpClient client = HttpClient.newHttpClient();
         HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(aUrl))
            .timeout(Duration.ofMinutes(1))
            .build();
         client.sendAsync(request, BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(System.out::println)
            .join();
      }catch(Exception ex){
         System.out.println("Exception in fetchAsyncUrl request: "+ex.getMessage());
      }
   }
   /**
    *
    * a method to make a web request. Note that this method will block execution
    * for up to 20 seconds while the request is being satisfied. Better to use a
    * non-blocking request.
    * @param aUrl the String indicating the query url for the lastFM api search
    * @return the String result of the http request.
    *
    **/
   public String fetchURL(String aUrl) {
      StringBuilder sb = new StringBuilder();
      URLConnection conn = null;
      InputStreamReader in = null;
      try {
         URL url = new URL(aUrl);
         conn = url.openConnection();
         if (conn != null)
            conn.setReadTimeout(20 * 1000); // timeout in 20 seconds
         if (conn != null && conn.getInputStream() != null) {
            in = new InputStreamReader(conn.getInputStream(),
                                       Charset.defaultCharset());
            BufferedReader br = new BufferedReader(in);
            if (br != null) {
               int ch;
               // read the next character until end of reader
               while ((ch = br.read()) != -1) {
                  sb.append((char)ch);
               }
               br.close();
            }
         }
         in.close();
      } catch (Exception ex) {
         System.out.println("Exception in url request:"+ ex.getMessage());
      } 
      return sb.toString();
   }
   public boolean sezToStop(){
      return stopPlaying;
   }
   
   
   /**
    * This is main method
    * */
   public static void main(String args[]) {
      String name = "first.last";
      String key = "eab1524872bc511168bc279f219c9d04";
      String host = "10.0.2.15";
      String port = "8080";
      if (args.length >= 1){
         host = args[0];
         port = args[1];
         name = args[2];
         key = args[3];
      }
      System.out.println("User: " + name);
      System.out.println("Host: " + host);
      System.out.println("Port: " + port);
      System.out.println("Key: " + key);
      String url = "http://"+host+":"+port+"/";
      System.out.println("Opening connection to: "+url);
      MusicTcpProxy sc = (MusicTcpProxy)new MusicTcpProxy(host, Integer.parseInt(port));
      String[] musicList = sc.getTitle_EXIST();
      for(int i = 0; i < musicList.length; i++) {
		System.out.println(musicList[i]);
		Track tr = sc.add_TREE(musicList[i]);
		System.out.println("Client: " + tr.getSummary());
	  }
	  
	  
      try{
        MusicBrowserApp mla = new MusicBrowserApp(name,key, host, port);
      }catch (Exception ex){
         ex.printStackTrace();
      }
   }
   
  
/**
* Voided method
* */
private  String readAll(Reader rd) throws IOException {
StringBuilder sb = new StringBuilder();
int cp;
while ((cp = rd.read()) != -1) {
  sb.append((char) cp);
}
return sb.toString();
}
public JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
InputStream is = new URL(url).openStream();
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
* Voided method
* */
public void main2(String url) {
JSONObject json;
try {
	json = readJsonFromUrl(url);
} catch (JSONException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} catch (IOException e) {
	System.out.println("Error detected");
	return;
}
}
    ///Extra Frame convert to Dialog
  Border blackline = BorderFactory.createLineBorder(Color.black);
  String pnlapi;
  String pnlartist;
  String pnlalbum;
  public void addAlbumHelper(String artist, String album) {
      this.pnlartist = artist;
      this.pnlalbum = album;
  }
  //Track alHelper = null;  
  /**
  This is a search Dialog which pop up when user click on search button
  @param mainFrame as this frame
  @param api as lastfm key
  */
  public void addAlbumDialog(JFrame mainFrame, String api) {
      //JFrame frameABC = new JFrame("Add Album");
      JDialog d = new JDialog(mainFrame , "Dialog Example", true);
      this.pnlapi = api;
      JButton btnClose = new JButton("Close");
      btnClose.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              // TODO Auto-generated method stub
              if(e.getSource() == btnClose) {
          int input = JOptionPane.showConfirmDialog(null, 
                  "Please refresh Tree Lish manually to load new track and album", "Confirm", JOptionPane.DEFAULT_OPTION);
                      d.dispose();
                      mainFrame.setEnabled(true);
          //mainFrame.rebuildTree();
                      mainFrame.revalidate();
                      mainFrame.repaint();
              }
          }
      });
      JPanel content = new JPanel();
      content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
      JScrollPane scroll;
      scroll = new JScrollPane();
      scroll.getViewport().add(content);
      scroll.setPreferredSize(new Dimension(375,200));
      d.setResizable(false);
      d.setPreferredSize(new Dimension(400,400));
      d.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      JLabel lblInfo = new JLabel("Please enter require information");
      JLabel lblKey = new JLabel("API KEY: ");
      JLabel lblArtist = new JLabel("Artist: ");
      JLabel lblAlbum = new JLabel("Album: ");
      JTextField txtKey = new JTextField(30);
      JTextField txtArtist = new JTextField(20);
      JTextField txtAlbum = new JTextField(20);
      JButton btnSearch = new JButton();
      JButton btnAddAll = new JButton("Add Album");
      btnAddAll.setEnabled(false);
      
      /**
       * 	Add All action - connect to proxy with add_ALL
       * */
      btnAddAll.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              // TODO Auto-generated method stub
              if(e.getSource() == btnAddAll) {
                  //alHelper.addEntireAlbum();
                  try {
                      // getConenction().add_ALBUMCOL(txtAlbum.getText());
                      getConenction().add_ALL(txtAlbum.getText(), txtArtist.getText(), txtKey.getText());		//PROXY
                  } catch (Exception e1) {
                      // TODO Auto-generated catch block
                      e1.printStackTrace();
                  }
              }
          }
      });
      
      ///Search Event
      btnSearch.setText("Search");
      btnSearch.addActionListener(new ActionListener() {
      int checker = 0;
          @Override
          public void actionPerformed(ActionEvent e) {
              // TODO Auto-generated method stub
              if(e.getSource() == btnSearch) {
                  if(checker == 1) {
                      content.removeAll();
                      content.revalidate();
                      content.repaint();
                      d.validate();
                      checker = 0;
                      btnSearch.setText("Search");
                      btnAddAll.setEnabled(false);
                  } else if (checker == 0 && (!txtArtist.getText().equalsIgnoreCase("") && !txtAlbum.getText().equalsIgnoreCase(""))) {
                      try {
                          boolean test = getConenction().add_SEARCH(txtArtist.getText(), txtAlbum.getText() , txtKey.getText());
                          System.out.println("TREST: " + test);
						     debugString("txtArtist.get", txtArtist.getText());
						      debugString("txtAlbum.get", txtAlbum.getText());
						  if(test) 
						  {
							  for(int i = 0; i < getConenction().getTrackList().length; i++) {
								  JButton btnTmp = new JButton();
								  btnTmp.setText(getConenction().getTrackList()[i]);
								  btnTmp.addActionListener(new ActionListener() {
									  @Override
									  public void actionPerformed(ActionEvent e) {
										  // TODO Auto-generated method stub
										  if(e.getSource() == btnTmp) {
											//  System.out.println(btnTmp.getText());
											  JSONObject tmpMap = 
													  getConenction().add_TRACKDIALOG(txtArtist.getText(), btnTmp.getText() , txtKey.getText(), txtAlbum.getText());
											  addTrackDialog(txtKey.getText(), btnTmp.getText(), tmpMap, d, mainFrame);
											  d.setVisible(false);
										  }
									  }
								  });
								  content.add(btnTmp);
								  d.validate();
							  }
						  }
						  btnAddAll.setEnabled(true);/*
						  txtArtist.setText(""); txtAlbum.setText("");*/
						  btnSearch.setText("Clean Table");
						  checker = 1;
                      } catch (Exception ei ) {
                          ei.printStackTrace();
                      }
                  }
              }
          }
      });
      txtKey.setText(api);
      txtKey.setEditable(false);
      txtArtist.setText(this.pnlartist);
  txtAlbum.setText(this.pnlalbum);
      JPanel pnl1 = new JPanel();
      pnl1.setLayout(new FlowLayout(FlowLayout.LEFT));
      pnl1.add(lblInfo);
      pnl1.add(Box.createRigidArea(new Dimension(150,0)));
      pnl1.add(lblKey);
      pnl1.add(txtKey);
      pnl1.setBorder(blackline);
      pnl1.add(lblArtist);
      pnl1.add(txtArtist);
      pnl1.add(Box.createRigidArea(new Dimension(100,0)));
      pnl1.add(lblAlbum);
      pnl1.add(txtAlbum);
      pnl1.add(Box.createRigidArea(new Dimension(150,0)));
      pnl1.add(btnSearch);
      pnl1.add(btnAddAll);
      pnl1.add(btnClose);
      pnl1.add(scroll);
      d.add(pnl1);
      d.pack();
      d.setVisible(true);
  }
  
  
  public void debugString(String what, String test) {
	System.out.println("[DEBUG]: " + "[" + what + "]:\t\t"  + test);
  }
  
/**
  Add Track Dialog
  This is frame after search is successfully fired. The program return a list of available track so user can choose one to add to the library. 
  Tracks are represented as a list of multiple button.
  Click on a button to fire add track event
  @param key as lastfm key
  @param track as track name
  @param al as Track
  @param albumFrame as album dialog frame
  @param mainFrame as Main Frame aka this because class extended frame
*/
  public void addTrackDialog(String key, String track, JSONObject dataMap, JDialog albumFrame, JFrame mainFrame) {
      //JFrame frameABC = new JFrame("Add Track");
      JSONObject dataMap2 = dataMap.getJSONObject("result");			//Removed String "result" from json 
      debugString("addTrackDialog", "HIT");
      debugString("addTrackDialog", dataMap2.toString());
      try {
      JDialog d = new JDialog(mainFrame , "Dialog Example", true);
      d.setResizable(false);
      d.setPreferredSize(new Dimension(400,400));
      d.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      //al.getTrackData(track);
      JLabel lblInfo = new JLabel("Track Information");
      JLabel lblTrack= new JLabel("Track: ");
      JLabel lblArtist = new JLabel("Artist: ");
      JLabel lblAlbum = new JLabel("Album: ");
      JLabel lblFn = new JLabel("File Name: ");
      JLabel lblDuration = new JLabel("Duration: ");
      JLabel lblSum = new JLabel("Summary");
      JLabel lblImage = new JLabel("Image: ");
      JLabel lblGenre = new JLabel("Genre: ");
      JTextField txtTrack = new JTextField(20); 
      JTextField txtArtist = new JTextField(20); 
      JTextField txtAlbum = new JTextField(20); 
      JTextField txtFn = new JTextField(20); 
      JTextField txtDuration = new JTextField(20); 
      JTextField txtSum = new JTextField(20); 
      JTextField txtImage = new JTextField(20); 
      JTextField txtGenre = new JTextField(20); 
      JButton btnSave = new JButton("Save");
      btnSave.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              // TODO Auto-generated method stub
              if (e.getSource() == btnSave) {
              try {
                  System.out.println("Track Save Click");
                  getConenction().add_SINGLE(
                  txtTrack.getText(), txtArtist.getText(), txtAlbum.getText(), lastFMKey);
              } catch (Exception ei) {
                  ei.printStackTrace();
              }
              mainFrame.setEnabled(true);
              }
          }
      });
      JButton btnClose = new JButton("Close");
      btnClose.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
              // TODO Auto-generated method stub
              if (e.getSource() == btnClose){
                  albumFrame.setVisible(true);
                  d.setVisible(false);
                  d.setEnabled(false);
              }
          }
      });
      txtTrack.setEditable(false);
      txtArtist.setEditable(false);
      txtAlbum.setEditable(false);
      txtFn.setEditable(false);
      txtDuration.setEditable(false);
      txtSum.setEditable(false);
      txtImage.setEditable(false);
      txtGenre.setEditable(false);
      txtTrack.setText(track);
      txtArtist.setText(dataMap2.get("artist").toString());
      txtAlbum.setText(dataMap2.get("album").toString());
      txtFn.setText("FILE NOT FOUND");
      txtDuration.setText(dataMap2.get("duration").toString());
      if(getConenction().getSummary() == null) {
          txtSum.setText("Track summary not found");
      } else {
          txtSum.setText(getConenction().getSummary());
      }
      txtImage.setText(getConenction().getImage());
      txtGenre.setText(dataMap2.get("genre").toString());
      JPanel pnlBorder = new JPanel();
      pnlBorder.setLayout( new BorderLayout());
      JPanel pnl = new JPanel();
      pnl.setLayout(new BoxLayout(pnl, BoxLayout.X_AXIS));
      JPanel pnl1 = new JPanel();
      pnl1.setLayout(new BoxLayout(pnl1, BoxLayout.Y_AXIS));
      JPanel pnl2 = new JPanel();
      pnl2.setLayout(new BoxLayout(pnl2, BoxLayout.Y_AXIS));
      pnl2.add(Box.createRigidArea(new Dimension(0,30)));
      pnl1.add(Box.createRigidArea(new Dimension(0,37)));
      pnl1.add(lblAlbum);
      pnl1.add(Box.createRigidArea(new Dimension(0,18)));
      pnl2.add(txtAlbum);
      pnl2.add(Box.createRigidArea(new Dimension(0,5)));
      pnl1.add(lblArtist);
      pnl1.add(Box.createRigidArea(new Dimension(0,15)));
      pnl2.add(txtArtist);
      pnl2.add(Box.createRigidArea(new Dimension(0,5)));
      pnl1.add(lblTrack);
      pnl1.add(Box.createRigidArea(new Dimension(0,14)));
      pnl2.add(txtTrack);
      pnl2.add(Box.createRigidArea(new Dimension(0,5)));
      pnl1.add(lblDuration);
      pnl1.add(Box.createRigidArea(new Dimension(0,15)));
      pnl2.add(txtDuration);
      pnl2.add(Box.createRigidArea(new Dimension(0,5)));
      pnl1.add(lblFn);
      pnl1.add(Box.createRigidArea(new Dimension(0,15)));
      pnl2.add(txtFn);
      pnl2.add(Box.createRigidArea(new Dimension(0,5)));
      pnl1.add(lblSum);
      pnl1.add(Box.createRigidArea(new Dimension(0,15)));
      pnl2.add(txtSum);
      pnl2.add(Box.createRigidArea(new Dimension(0,5)));
      pnl1.add(lblImage);
      pnl1.add(Box.createRigidArea(new Dimension(0,15)));
      pnl2.add(txtImage);
      pnl2.add(Box.createRigidArea(new Dimension(0,5)));
      pnl1.add(lblGenre);
      pnl1.add(Box.createRigidArea(new Dimension(0,40)));
      pnl2.add(txtGenre);
      pnl2.add(Box.createRigidArea(new Dimension(0,30)));
      pnl.add(pnl1);
      pnl.add(pnl2);
      pnlBorder.add(lblInfo, BorderLayout.PAGE_START);
      pnlBorder.add(pnl, BorderLayout.CENTER);
      JPanel pnl3 = new JPanel(new FlowLayout(FlowLayout.CENTER));
      pnl3.add(btnSave);
      pnl3.add(btnClose);
      pnlBorder.add(pnl3, BorderLayout.PAGE_END);
      d.add(pnlBorder);
      d.pack();
      d.setVisible(true);
      } catch (Exception ei ) {
          ei.printStackTrace();
      }
  }
    @Override
    public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
        // TODO Auto-generated method stub
    }
    public String getLastFMKey() {
        return lastFMKey;
    }
    public String getHost() {
        return host;
    }
    public String getPort() {
        return port;
    }

    
}
