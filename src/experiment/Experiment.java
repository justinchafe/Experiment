/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiment;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.runtime.x.LibXUtil;
/**
 *
 * @author jchafe
 */
public class Experiment  {
    
    public final static int NUM_WORDS = 40;
    public final static int NUM_VAS = 4;   
    public final static int NUM_VAS_QUESTIONS = 6;
    public final static int NUM_TS_YN_QUESTIONS = 1;
    public final static int NUM_MOVIE_ONE_YN_QUESTIONS = 2;
    public final static int NUM_MOVIE_TWO_YN_QUESTIONS = 2;
    public final static int NUM_TS_SCALE_QUESTIONS = 2;
    public final static int NUM_MOVIE_ONE_SCALE_QUESTIONS = 3;
    public final static int NUM_MOVIE_TWO_SCALE_QUESTIONS = 3;
    
    
    private Timer dfTimer, tsTimer, rwTimer, dTimer;
    final String WORDLIST_FILENAME = "CSV/words.csv";
    final String INTRO_FILENAME = "HTML/intro.html";
    final String DF_TRIAL_INSTR_FILENAME  = "HTML/dfTrialInstr.html";
    final String DF_PRAC_INSTR_FILENAME = "HTML/dfPracInstr.html";
    final String DFMID_INSTR_FILENAME = "HTML/dfMidinstructions.html";
    final String DISTRACT_INSTR_FILENAME = "HTML/distractInstr.html";
    final String VAS_INSTR_FILENAME = "HTML/vasInstructions.html";
    final String OPEN_QUES_FILENAME = "HTML/openEndQues.html";
    final String RECALL_INSTR_FILENAME = "HTML/recallInstr.html";
    final String MOVIE_ONE_INSTR_FILENAME = "HTML/movieOneInstr.html";
    final String MOVIE_TWO_INSTR_FILENAME = "HTML/movieTwoInstr.html";
    final String TS_INSTR_A_FILENAME = "HTML/tsInstrA.html";
    final String TS_INSTR_AA_FILENAME = "HTML/tsInstrAA.html";
    final String TS_INSTR_B_FILENAME = "HTML/tsInstrB.html";
    final String TS_INSTR_BB_FILENAME = "HTML/tsInstrBB.html";
    
    final String VAS_TEXT_FILENAME = "CSV/VASText.csv"; 
    final String YN_MOVIE_TEXT_FILENAME = "CSV/ynMovieText.csv";
    final String YN_TS_TEXT_FILENAME = "CSV/ynTSText.csv";
    final String TS_SCALE_TEXT_FILENAME = "CSV/tsScaleText.csv";
    final String MOVIE_SCALE_TEXT_FILENAME = "CSV/movieScaleText.csv";
    final String DF_SCALE_TEXT_FILENAME = "CSV/dfScaleText.csv";
    final String RAND_PNUM_FILENAME = "CSV/Dissertation_randomnumbergenerator.csv";
    
    String SAD_MOVIE_LOCATION;
    String HAPPY_MOVIE_LOCATION;
    
    final int TABLE_ROW_HEIGHT;
    int TS_TIME, RW_TIME, DF_TIME, D_TIME;
   
    final String outputFile;
    String COUNTDOWN_TEXT, TABLE_HEADER;
   
    
    private int tsChoice;
    private int numSpacePress;
    WordList list; //THIS SHOULD BE WordList listA, listB.
    ActionListener ratingScaleTask, movieQuestionsTask, instrButtonTask, dFTimerTask, tsTimerTask, rwTimerTask, distractTimerTask;
    FocusListener focusTask;
    MediaPlayerEventAdapter mediaListen;
    Action tsTask;
    String[][] vasText;
    String[] ynTSText;
    String[] ynMovieOneText;
    String[] ynMovieTwoText;
    String[][] tsScaleText;
    String[][] movieOneScaleText;
    String[][] movieTwoScaleText;
    String[][] dfScaleText;
    DisplayManager dm;
    private boolean tsTaskMid;
    Participant p;
    
    
     public Experiment() throws URISyntaxException {
         //Later these values will be culled from an XML or text file if possible, with these defaults if unavailable.
         
         COUNTDOWN_TEXT = "What number did you stop at?";
         TABLE_HEADER = "Please Enter any Words you can recall. Write one word per line: ";
         dm = DisplayManager.getInstance();
         try {
             loadSettings(this.getClass().getResourceAsStream("settings.txt"));

         }catch (IOException e) {
           dm.showError("Error Loading the file settings.txt - using Default Settings");
           DF_TIME = 3000;
           TS_TIME = 300000;
           RW_TIME = 300000;
           D_TIME = 120000;
        }
         TABLE_ROW_HEIGHT = 25;
         numSpacePress = 0;
         
        String newPath; 
        
        try {
            java.io.File f = new java.io.File(Experiment.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            System.out.println("HERE IS THE VIDEO PATH" + f.getPath());
            newPath = f.getPath();
            newPath = newPath.trim();
            System.out.println("Video path location: " + newPath);
            SAD_MOVIE_LOCATION = newPath + "\\Video\\1.wmv";
            HAPPY_MOVIE_LOCATION = newPath + "\\Video\\2.wmv";
            System.out.println ("THE VIDEO LOCATIONS ARE: " + SAD_MOVIE_LOCATION + ", " + HAPPY_MOVIE_LOCATION);
        }catch (URISyntaxException e) {
            e.printStackTrace();
        }
        
       //TEST REMOVE THIS LATER:
      
         
         loadVASText(this.getClass().getResourceAsStream(VAS_TEXT_FILENAME));
         ynTSText = loadYorNText(this.getClass().getResourceAsStream(YN_TS_TEXT_FILENAME), NUM_TS_YN_QUESTIONS);
         ynMovieOneText = loadYorNText(this.getClass().getResourceAsStream(YN_MOVIE_TEXT_FILENAME), NUM_MOVIE_ONE_YN_QUESTIONS);
         ynMovieTwoText = loadYorNText(this.getClass().getResourceAsStream(YN_MOVIE_TEXT_FILENAME), NUM_MOVIE_TWO_YN_QUESTIONS);
         tsScaleText = loadScaleText(this.getClass().getResourceAsStream(TS_SCALE_TEXT_FILENAME), NUM_TS_SCALE_QUESTIONS);
         movieOneScaleText = loadScaleText(this.getClass().getResourceAsStream(MOVIE_SCALE_TEXT_FILENAME),NUM_MOVIE_ONE_SCALE_QUESTIONS );
         movieTwoScaleText = loadScaleText(this.getClass().getResourceAsStream(MOVIE_SCALE_TEXT_FILENAME), NUM_MOVIE_TWO_SCALE_QUESTIONS );
         loadDFScaleText(this.getClass().getResourceAsStream(DF_SCALE_TEXT_FILENAME));
        
        //REM - remove test code for getting the current working path from within a jar file, usefull for HTML/CSV/settings.txt???
        java.io.File f = new java.io.File(Experiment.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        System.out.println(f.getPath());
        
         //setup our event handlers:
        tsTaskMid = false; //needed to let us know if we should display the midpoint instructions. 
        createTSTimerTask();
        createTSTask();
        createDistractTimerTask();
        createRWTimerTask();
        createFocusTask();
        createDFTimerTask();
        createInstrButtonTask();
        createDFRatingScaleTask();
        createMediaListenerTask();

        outputFile = "OUTPUT";
       // final String q = s[0];
        list = new WordList(this.getClass().getResourceAsStream(WORDLIST_FILENAME));
       
        
        p = new Participant(dm.getNumber("Enter your Participant Code: ", "Please Enter Digits" ),outputFile);
        setParticipantValues();
        
        //FOR TESTING PARTICIPANT VALUES:
        /*
         int i,j,k;
         j = k = 0;
        int[] randListA = new int[108];
        int[] randListB = new int[108];
        for (i = 0; i < 216; i++) {
           if (getDFListOrder(this.getClass().getResourceAsStream(RAND_PNUM_FILENAME),i+1) == 0) {
               randListA[k++] = i;
           }else {
               randListB[j++] = i;
          
               
           }
           
          
        }
        
         dm.showError("Rand List A index: " + k + ", Rand List B index: " + j);
        */
        dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(INTRO_FILENAME)));
        dm.instrButton.setActionCommand("intro");
        dm.instrButton.addActionListener(instrButtonTask);
        dm.startDisplay();
       
        
    } 
     
    private String loadText(InputStream stream) {
        BufferedReader in;
	StringBuilder sb;
	String line;
	try {
            in= new BufferedReader(new InputStreamReader(stream));
            line = null;
            sb = new StringBuilder();
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }//end while
            in.close();
            return sb.toString();
	}catch (IOException e) {
            dm.showError("Error Loading Text - using defaults");
            return "error loading!";
	}
    }	
    
    //0 = A, 1 = B, 2 = ERROR (2 will exit to preserve data)
   private int getDFListOrder(InputStream stream, int pNum) {
        BufferedReader in;
	String line;
        int nlines, posfound = 0;

        try {
            in= new BufferedReader(new InputStreamReader(stream));
            nlines = 0;
            while ((line = in.readLine()) != null) {
                nlines++;    
                if (Integer.parseInt(line) == pNum) {
                        System.out.println("FOUND Participant Number: value of pNUM = " + pNum + ", value from file = " + line + "value found at position = " + nlines);
                        posfound = nlines;
                }
            }
            in.close();
            
            if (posfound == 0) {
                DisplayManager.getInstance().showError("Subject number not found in random list!");
                System.exit(1);
                return 2;
            }
            
            if (nlines%2 != 0) {
                DisplayManager.getInstance().showError("Error random list is not even!");
                System.exit(1);
                return 2;
            }   
                
            if (posfound <= nlines/2) {
                return 0;
            }else {
               return 1;
            }
      
       
	}catch (IOException e) {
            dm.showError("Error Loading Text - DFListOrder - using defaults");
            System.exit(1);
            return 2;
	}
    }	

    private void createInstrButtonTask() {
        instrButtonTask = new ActionListener() {
            // @Override
            public void actionPerformed(ActionEvent evt) {
                if (evt.getActionCommand().equals("intro")) {
                    
                    //Load VAS1:
                    dm.clearPanel();
                    dm.setVasPanel(NUM_VAS_QUESTIONS,vasText, loadText(this.getClass().getResourceAsStream(VAS_INSTR_FILENAME)));
                    dm.instrButton.setActionCommand("vasButton1");
                    dm.instrButton.addActionListener(instrButtonTask);
                    dm.showPanel();
                 
                       
                      //Load TS Instructions
                    //    if (tsChoice == 0) {
                      //      dm.clearPanel();
                        //    dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(TS_INSTR_A_FILENAME)));
                          //  dm.instrButton.setActionCommand("tsInstrA");
                            //dm.instrButton.addActionListener(instrButtonTask);
                           // dm.showPanel();
                            
                        //}else {
                         //   dm.clearPanel();
                          //  dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(TS_INSTR_B_FILENAME)));
                           // dm.instrButton.setActionCommand("tsInstrB");
                            //dm.instrButton.addActionListener(instrButtonTask);
                            //dm.showPanel();
                        //}
                    
                    //Movie question panel Code:
                    //THIS IS OLD:
             
                   //String[] quesText = {"Have you seen this movie before?", "Did this clip remind you of any past experiences?"};
                   //String[] rText = {"How much did you enjoy this film clip?", "How interested are you in watching the full movie?", "How believable did you find the main characters?"};
                    //String lhText[][] = new String[3][2];
                    //lhText[0][0] = "Not at all Sad";
                    //lhText[0][1] = "Extremely Sad";
                    //lhText[1][0] = "Not at all Curious";
                    //lhText[1][1] = "Extremely Curious";
                    //lhText[2][0] = "Super Sad";
                    //lhText[2][1] = "Super Mad and Bad";
                    
                    //Movie Questions Code:
                  // dm.setMovieQuestionsPanel(2, quesText, 3, 10, lhText, rText);
                   //dm.clearPanel();
                   //dm.setQuestionsPanel(2, ynMovieOneText, 3, 10, movieOneScaleText); //lhText, rText);
                   //dm.instrButton.setActionCommand("movieOneQuesButton");
                   //dm.instrButton.addActionListener(instrButtonTask);
                   //dm.showPanel();
                    
                    //tsQuestionsCode:
                    //dm.clearPanel();
                    //dm.setQuestionsPanel(1, ynTSText, 2, 10, tsScaleText); //lhText, rText);
                    //dm.instrButton.setActionCommand("tsQuesButton");
                    //dm.instrButton.addActionListener(instrButtonTask);
                    //dm.showPanel();
                    
                    //Open Ended Question code:
                    //String test = "<html><font size = 40>Hello this is our question</font></html>";
                   // dm.clearPanel();
                    //dm.setOpenEndedQuesPanel(loadText(this.getClass().getResourceAsStream(OPEN_QUES_FILENAME)));
                    //dm.instrButton.setActionCommand("openEndedQues");
                    //dm.instrButton.addActionListener(instrButtonTask);
                    //dm.showPanel();
                    
                    //Distraction Countdown Code:
                    //After instructions have been shown....
                   // dm.clearPanel();
                    //dm.setBlankPanel();
                   // dTimer = new Timer(D_TIME, distractTimerTask);
                   // dm.showPanel();
                    //dTimer.start();
                     
                    //TS code:
                    //dm.clearPanel();
                    //dm.setBlankPanel();
                    //dm.mainPanel.addFocusListener(focusTask);
                    //dm.mainPanel.getInputMap().put(KeyStroke.getKeyStroke("released SPACE"), "pressed");
                    //dm.mainPanel.getActionMap().put("pressed", tsTask);
                    //tsTimer = new Timer(TS_TIME,tsTimerTask);
                    //tsTimer.start();
                    //dm.showPanel();
                    //dm.mainPanel.requestFocusInWindow(); 
                    
                    //Table code
                    /*
                    dm.clearPanel();
                    dm.setTable(TABLE_HEADER,TABLE_ROW_HEIGHT);
                    rwTimer = new Timer(RW_TIME,rwTimerTask);
                    dm.showPanel();
                    rwTimer.start();
                   */
                    
                   //Slider code:
                  
                   //dm.setSingleVasPanel("vas low", "vas high", 100, 10, 500, 0);
                    
                    //dm.clearPanel();
                    //dm.setVasPanel(6,vasText, loadText(this.getClass().getResourceAsStream(VAS_INSTR_FILENAME)));
                    //dm.instrButton.setActionCommand("vasButton1");
                    //dm.instrButton.addActionListener(instrButtonTask);
                    //dm.showPanel();
                   
                    
                   //DF code
                   
                   // dfTimer = new Timer(DF_TIME, dFTimerTask); 
                   // dm.clearPanel();
                   // dm.setDirForgetPanel(list.getNextWord());
                   // dm.showPanel();
                   // dfTimer.start();
                    
                }else if (evt.getActionCommand().equals("vasButton1")) {
                    dm.clearPanel();
                    int i;
                    int[] vAnswers;
                    vAnswers = new int[dm.mVas.length];
                    for (i=0; i < dm.mVas.length; i++) {
                        System.out.println("Vas" + i + ": " + dm.mVas[i].getValue());
                        vAnswers[i] = dm.mVas[i].getValue();
                    }
                    p.addVAS(0,vAnswers);
                    
                    //load dfPrac Instructions:
                    dm.clearPanel();
                    dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(DF_PRAC_INSTR_FILENAME)));
                    dm.instrButton.setActionCommand("dfPrac");
                    dm.instrButton.addActionListener(instrButtonTask);
                    dm.showPanel();
               
                }else if (evt.getActionCommand().equals("dfPrac")) {
                    //load the first df List and begin:
                    dfTimer = new Timer(DF_TIME, dFTimerTask);
                    dm.clearPanel();
                    dm.setDirForgetPanel(list.getNextWord());
                    dm.showPanel();
                    dfTimer.start(); 
                    
                }else if (evt.getActionCommand().equals("dFMidInstr")) {
                    dm.clearPanel();
                    dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(DFMID_INSTR_FILENAME)));
                    dm.clearPanel();
                    dm.setDirForgetPanel(list.getNextWord());
                    dm.showPanel();
                    dfTimer.start();
                    
                   
                }else if (evt.getActionCommand().equals("distractInstr")) {
                    dm.clearPanel();
                    dm.setBlankPanel();
                    dTimer = new Timer(D_TIME, distractTimerTask);
                    dm.showPanel();
                    dTimer.start();
                
                }else if (evt.getActionCommand().equals("timeSubmit")) {
                    if (!(dm.getTime.getText() == null) && (dm.getTime.getText().matches("\\d+")) ) {
                        p.addCountdownAnswer(dm.getTime.getText());
                        dm.clearPanel();
                        //load the next panel...which is recall words
                        dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(RECALL_INSTR_FILENAME)));
                        dm.instrButton.setActionCommand("recallInstr");
                        dm.instrButton.addActionListener(instrButtonTask);
                        dm.showPanel();
                        
                    }else {
                        dm.getTime.setText(null);
                        dm.showError("Please Enter Only Digits");
                    }
                
                }else if (evt.getActionCommand().equals("recallInstr")) {
                    dm.clearPanel();
                    dm.setTable(TABLE_HEADER,TABLE_ROW_HEIGHT);
                    rwTimer = new Timer(RW_TIME,rwTimerTask);
                    dm.showPanel();
                    rwTimer.start();
                    
                    // dm.remInstructionPanel();
                    // THIS APPEARS TO BE TS CODE:
                    /*
                    dm.setBlankPanel();
                    dm.mainPanel.addFocusListener(focusTask);
                    dm.mainPanel.getInputMap().put(KeyStroke.getKeyStroke("released SPACE"), "pressed");
                    dm.mainPanel.getActionMap().put("pressed", tsTask);
                    tsTimer = new Timer(TS_TIME,tsTimerTask);
                    dm.showPanel();
                    tsTimer.start();
                    dm.mainPanel.requestFocusInWindow(); 
                
                    */
           
                   
                }else if (evt.getActionCommand().equals("playMovieOne")) {
                    dm.mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(mediaListen);
                    dm.showMovie(SAD_MOVIE_LOCATION);
                    
                    
                }else if (evt.getActionCommand().equals("vasButton2")) {
                    dm.clearPanel();
                    int i;
                    int[] vAnswers;
                    vAnswers = new int[dm.mVas.length];
                    for (i=0; i < dm.mVas.length; i++) {
                        System.out.println("Vas" + i + ": " + dm.mVas[i].getValue());
                        vAnswers[i] = dm.mVas[i].getValue();
                    }
                    p.addVAS(1,vAnswers);
                    
                    //load Movie Questions:
                    dm.clearPanel();
                    dm.setQuestionsPanel(NUM_MOVIE_ONE_YN_QUESTIONS, ynMovieOneText, NUM_MOVIE_ONE_SCALE_QUESTIONS, 10, movieOneScaleText); //lhText, rText);
                    dm.instrButton.setActionCommand("movieOneQuesButton");
                    dm.instrButton.addActionListener(instrButtonTask);
                    dm.showPanel();
                    
                }else if (evt.getActionCommand().equals("movieOneQuesButton")) {
                  
                    //get the button values
                    int i,k;
                    boolean allRadioSelected = true;
                    
                    
                    String[] msAns = new String[dm.rsButtons.length];
                    for (i =0;i<dm.rsButtons.length; i++ ) {
                        for (k=0; k<dm.rsButtons[i].length;k++) {
                            if (dm.rsButtons[i][k].isSelected()) {
                                System.out.println("Scale " + i + "= " + (k+1));
                                msAns[i] = Integer.toString(k+1);
                                k = dm.rsButtons[i].length+1;
                                //break;//break the loop
                            }
                            
                            if (k+1 == dm.rsButtons[i].length) {
                                allRadioSelected = false;
                            }
                            
                        }
                    }
                    
                    if (!allRadioSelected) {
                        dm.showError("You did not select a rating on one of the scales.  Please select a rating for them all.");
                    
                    } else {
                        //END CODE AND SETUP NEXT
                        String[] ynAns = new String[dm.yn.length];
                        for (k=0; k<dm.yn.length;k++) {
                            System.out.println("Y or N [" + k + "] = "  + dm.yn[k].getSelectedItem());
                            ynAns[k] = dm.yn[k].getSelectedItem().toString();
                        }
                        p.addYNAnswer(ynAns, 1);
                        p.addScaleAnswer(msAns, 1);
                        
                      //Load TS Instructions
                        if (tsChoice == 0) {
                            dm.clearPanel();
                            dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(TS_INSTR_A_FILENAME)));
                            dm.instrButton.setActionCommand("tsInstrA");
                            dm.instrButton.addActionListener(instrButtonTask);
                            dm.showPanel();
                            
                        }else {
                            dm.clearPanel();
                            dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(TS_INSTR_B_FILENAME)));
                            dm.instrButton.setActionCommand("tsInstrB");
                            dm.instrButton.addActionListener(instrButtonTask);
                            dm.showPanel();
                        }
                        
                    }
                    
                }else if (evt.getActionCommand().equals("tsInstrA")) {
                    dm.clearPanel();
                    dm.setBlankPanel();
                    dm.mainPanel.addFocusListener(focusTask);
                    dm.mainPanel.getInputMap().put(KeyStroke.getKeyStroke("released SPACE"), "pressed");
                    dm.mainPanel.getActionMap().put("pressed", tsTask);
                    tsTimer = new Timer(TS_TIME,tsTimerTask);
                    tsTimer.start();
                    dm.showPanel();
                    dm.mainPanel.requestFocusInWindow();
                    
                }else if (evt.getActionCommand().equals("tsInstrB")) {
                    dm.clearPanel();
                    dm.setBlankPanel();
                    dm.mainPanel.addFocusListener(focusTask);
                    dm.mainPanel.getInputMap().put(KeyStroke.getKeyStroke("released SPACE"), "pressed");
                    dm.mainPanel.getActionMap().put("pressed", tsTask);
                    tsTimer = new Timer(TS_TIME,tsTimerTask);
                    tsTimer.start();
                    dm.showPanel();
                    dm.mainPanel.requestFocusInWindow();
                    
                    
                }else if (evt.getActionCommand().equals("vasButton3")) {
                    dm.clearPanel();
                    int i;
                    int[] vAnswers;
                    vAnswers = new int[dm.mVas.length];
                    for (i=0; i < dm.mVas.length; i++) {
                        System.out.println("Vas" + i + ": " + dm.mVas[i].getValue());
                        vAnswers[i] = dm.mVas[i].getValue();
                    }
                    p.addVAS(2,vAnswers);
                    dm.setQuestionsPanel(NUM_TS_YN_QUESTIONS, ynTSText, NUM_TS_SCALE_QUESTIONS, 10, tsScaleText); //lhText, rText);
                    dm.instrButton.setActionCommand("tsQuesButton");
                    dm.instrButton.addActionListener(instrButtonTask);
                    dm.showPanel();
               
                    
                }else if (evt.getActionCommand().equals("tsQuesButton")) {
                   // dm.clearPanel();
                    //get the button values
                    int i,k;
                    boolean allRadioSelected = true;
                   
                    
                    String[] tsAns = new String[dm.rsButtons.length];
                    for (i =0;i<dm.rsButtons.length; i++ ) {
                        for (k=0; k<dm.rsButtons[i].length;k++) {
                            if (dm.rsButtons[i][k].isSelected()) {
                                System.out.println("Scale " + i + "= " + (k+1));
                                tsAns[i] = Integer.toString(k+1);
                                k = dm.rsButtons[i].length+1;
                                //break;//break the loop
                            }
                            
                            if (k+1 == dm.rsButtons[i].length) {
                                allRadioSelected = false;
                            }
                            
                        }
                    }
                    if (!allRadioSelected) {
                        dm.showError("You did not select a rating on one of the scales.  Please select a rating for them all.");
                    } else {
                        //END CODE AND SETUP NEXT
                        String[] ynAns = new String[dm.yn.length];
                        for (k=0; k<dm.yn.length;k++) {
                            System.out.println("Y or N [" + k + "] = "  + dm.yn[k].getSelectedItem());
                            ynAns[k] = dm.yn[k].getSelectedItem().toString();
                        }
                        p.addYNAnswer(ynAns, 3);
                        p.addScaleAnswer(tsAns, 3);
                        dm.clearPanel();
                        dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(MOVIE_TWO_INSTR_FILENAME)));
                        dm.instrButton.setActionCommand("playMovieTwo");
                        dm.instrButton.addActionListener(instrButtonTask);
                        dm.showPanel();

                    }
                    
                
                }else if (evt.getActionCommand().equals("playMovieTwo")) {
                    dm.mediaPlayerComponent.getMediaPlayer().addMediaPlayerEventListener(mediaListen);
                    dm.showMovie(HAPPY_MOVIE_LOCATION);
                
             
                }else if (evt.getActionCommand().equals("vasButton4")) {
                    dm.clearPanel();
                    int i;
                    int[] vAnswers;
                    vAnswers = new int[dm.mVas.length];
                    for (i=0; i < dm.mVas.length; i++) {
                        System.out.println("Vas" + i + ": " + dm.mVas[i].getValue());
                        vAnswers[i] = dm.mVas[i].getValue();
                    }
                    p.addVAS(3,vAnswers);
                    dm.setQuestionsPanel(NUM_MOVIE_TWO_YN_QUESTIONS, ynMovieTwoText, NUM_MOVIE_TWO_SCALE_QUESTIONS, 10, movieTwoScaleText); //lhText, rText);
                    dm.instrButton.setActionCommand("movieTwoQuesButton");
                    dm.instrButton.addActionListener(instrButtonTask);
                    dm.showPanel();
                    
                }else if (evt.getActionCommand().equals("movieTwoQuesButton")) {
                      //get the button values
                    int i,k;
                    boolean allRadioSelected = true;
                   
                    
                    String[] msAns = new String[dm.rsButtons.length];
                    for (i =0;i<dm.rsButtons.length; i++ ) {
                        for (k=0; k<dm.rsButtons[i].length;k++) {
                            if (dm.rsButtons[i][k].isSelected()) {
                                System.out.println("Scale " + i + "= " + (k+1));
                                msAns[i] = Integer.toString(k+1);
                                k = dm.rsButtons[i].length+1;
                                //break;//break the loop
                            }
                            
                            if (k+1 == dm.rsButtons[i].length) {
                                allRadioSelected = false;
                            }
                            
                        }
                    }
                    if (!allRadioSelected) {
                        dm.showError("You did not select a rating on one of the scales.  Please select a rating for them all.");
                    } else {
                        //END CODE AND SETUP NEXT
                        String[] ynAns = new String[dm.yn.length];
                        for (k=0; k<dm.yn.length;k++) {
                            System.out.println("Y or N [" + k + "] = "  + dm.yn[k].getSelectedItem());
                            ynAns[k] = dm.yn[k].getSelectedItem().toString();
                        }
                        p.addYNAnswer(ynAns, 2);
                        p.addScaleAnswer(msAns, 2);
                        dm.clearPanel();
                        dm.setOpenEndedQuesPanel(loadText(this.getClass().getResourceAsStream(OPEN_QUES_FILENAME)));
                        dm.instrButton.setActionCommand("openEndQues");
                        dm.instrButton.addActionListener(instrButtonTask);
                        dm.showPanel();
                }
                
                }else if (evt.getActionCommand().equals("openEndQues")) {
                    dm.clearPanel();
                    if (!(dm.openEnd.getText() == null) ) {
                        p.addOpenEndedAnswer(dm.openEnd.getText());
                        dm.clearPanel();
                        p.writeParticipantInfo();
                        //Outro instructions?
                        //cleanup movie and stuff.
                        System.exit(0);
                    }else {
                        dm.showError("Please Enter Text");
                 }
             }       
        }};
    }
    
    
      private void createDFTimerTask() {
        dFTimerTask = new ActionListener() {
        // @Override
            public void actionPerformed(ActionEvent evt) {
                System.out.println("heartbeat");
                dfTimer.stop();
                dm.clearPanel();
                dm.setRatingScalePanel(5,dfScaleText[0][1], dfScaleText[0][2], dfScaleText[0][0]);
                int i;
                for (i=0; i<dm.rsButtons[0].length;i++) {
                    dm.rsButtons[0][i].addActionListener(ratingScaleTask);
                }
                dm.showPanel();
              
           }
         };
    }
    
    
    private void createMediaListenerTask() {
        mediaListen = new MediaPlayerEventAdapter() {
               @Override
              public void finished(MediaPlayer mediaPlayer) {
                   System.out.println("MEDIA FINISHED EVENT FIRED");
                   System.out.println("Nothing" + dm.mediaPlayerComponent.getMediaPlayer().getMediaMeta().getTitle());
                  
                   
                   if (dm.mediaPlayerComponent.getMediaPlayer().getMediaMeta().getTitle().equals("1.wmv")) {
                        dm.clearMovie();
                        dm.clearPanel();
                        dm.setVasPanel(NUM_VAS_QUESTIONS,vasText, loadText(this.getClass().getResourceAsStream(VAS_INSTR_FILENAME)));
                        dm.instrButton.setActionCommand("vasButton2");
                        dm.instrButton.addActionListener(instrButtonTask);
                        dm.showPanel();
                        
                   }else if (dm.mediaPlayerComponent.getMediaPlayer().getMediaMeta().getTitle().equals("2.wmv")) {
                        dm.clearMovie();
                        dm.mediaPlayerComponent.release(); //SECOND VIDEO, we should release it's stuff.
                        dm.clearPanel();
                        dm.setVasPanel(NUM_VAS_QUESTIONS,vasText, loadText(this.getClass().getResourceAsStream(VAS_INSTR_FILENAME)));
                        dm.instrButton.setActionCommand("vasButton4");
                        dm.instrButton.addActionListener(instrButtonTask);
                        dm.showPanel();
                   }
                       
        }}; 
    }
    
    private void createDFRatingScaleTask() {
        ratingScaleTask = new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent evt) {
                
                int i;
                for (i=0;i < dm.SCALE_LIMIT; i++) {
                    if (evt.getActionCommand().equals(new Integer(i).toString())) {
                        p.addDFResponse(i, list.getCurrentWord(),list.currentList);
                        //dm.showError("The Response chosen was: " + i + ", word: " + list.getCurrentWord() + ",the Current List: " + list.currentList); //THIS IS FOR TESTING REMOVE!!!
                     }
                }
                
                if (!list.areBothListsDone()) {
                    if (list.listHasMoreWords()) {
                        dm.clearPanel();
                        //dm.remRatingScalePanel();
                        dm.setDirForgetPanel(list.getNextWord());
                        dm.showPanel();
                        dfTimer.start();
                    }else {
                        list.currentList ^= 1; //switch to the opposite list!
                        dm.clearPanel();
                        //dm.remRatingScalePanel();
                        dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(DFMID_INSTR_FILENAME)));
                        dm.instrButton.setActionCommand("dFMidInstr");
                        dm.instrButton.addActionListener(instrButtonTask);
                        dm.showPanel();
                    }
                        
                }else {
                    dm.clearPanel();
                    
                    //no it's the distract task next. so load distract text....
                    dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(DISTRACT_INSTR_FILENAME))); //TODO CHANGE HTML FILE
                    dm.instrButton.setActionCommand("distractInstr");
                    dm.instrButton.addActionListener(instrButtonTask);
                    dm.showPanel();
                }     
                    
               
               
            }
        };
    }
    
    
    
    
    private void createFocusTask() {
        focusTask = new FocusListener() {
            public void focusGained(FocusEvent e) {
                return;
            }
            public void focusLost(FocusEvent e) {
                dm.mainPanel.requestFocusInWindow(); 
            }
        };   
    }
    
    
    private final void createTSTask() {
      class tsTask extends AbstractAction {
            public tsTask() {
                super();
                //putValue(SHORT_DESCRIPTION, desc);
                //putValue(MNEMONIC_KEY, mnemonic);
            }
    
            public void actionPerformed(ActionEvent evt) {
                System.out.println("Get the key pressed: " + evt);
                System.out.println(numSpacePress++);
            }
        }
      tsTask = new tsTask();
      
       /* KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
        @Override
            public boolean dispatchKeyEvent(KeyEvent evt) {
                System.out.println("Got key event!");
                if (evt.getKeyChar() == 'a') {
                    System.out.println(numSpacePress++);
                }
                return false;
                };
          });
          */
    }
    
    
     private void createTSTimerTask() {
        tsTimerTask = new ActionListener() {
        // @Override
            public void actionPerformed(ActionEvent evt) {
                System.out.println("heartbeat");
                tsTimer.stop();
                dm.clearPanel();
                //dm.remBlankPanel();
                if (!tsTaskMid) {
                   switch(tsChoice) {
                        case 0:
                            p.addTsAnswers(0, "A", numSpacePress);
                            numSpacePress = 0;
                            tsTaskMid = true;
                            dm.clearPanel();
                            dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(TS_INSTR_AA_FILENAME)));
                            dm.instrButton.setActionCommand("tsInstrA");
                            dm.instrButton.addActionListener(instrButtonTask);
                            dm.showPanel();
                            break;                               
                          
                        case 1:
                            p.addTsAnswers(0, "B", numSpacePress);
                            numSpacePress = 0;
                            tsTaskMid = true;
                            dm.clearPanel();
                            dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(TS_INSTR_BB_FILENAME)));
                            dm.instrButton.setActionCommand("tsInstrB");
                            dm.instrButton.addActionListener(instrButtonTask);
                            dm.showPanel();
                            break;
                    }       
                    
                }else {
                    switch(tsChoice) {
                        case 0:
                            p.addTsAnswers(1, "AA", numSpacePress);
                            numSpacePress = 0;
                           
                           // dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(TS_INSTR_AA_FILENAME)));
                            //dm.instrButton.setActionCommand("tsMidInstrAA");
                            //dm.instrButton.addActionListener(instrButtonTask);
                            break;                               
                          
                        case 1:
                            p.addTsAnswers(1, "BB", numSpacePress);
                            numSpacePress = 0;
                          
                           // dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(TS_INSTR_BB_FILENAME)));
                           // dm.instrButton.setActionCommand("tsMidInstrBB");
                           // dm.instrButton.addActionListener(instrButtonTask);
                            break;
                    }    
                    //load vas3
                   dm.clearPanel();
                   dm.setVasPanel(NUM_VAS_QUESTIONS,vasText, loadText(this.getClass().getResourceAsStream(VAS_INSTR_FILENAME)));
                   dm.instrButton.setActionCommand("vasButton3");
                   dm.instrButton.addActionListener(instrButtonTask);
                   dm.showPanel();
                    
                }
            }};
 
    }
     
     private void createRWTimerTask() {
         rwTimerTask = new ActionListener() {
             public void actionPerformed(ActionEvent evt) {
                 rwTimer.stop();
                 int i;
                 String[] rw;
                 rw = new String[NUM_WORDS];
                 if (dm.table.isEditing()) {
                    dm.table.getCellEditor().stopCellEditing();
                 }
                 
                for (i=0;i<NUM_WORDS;i++) {
                    if (dm.table.getModel().getValueAt(i, 0) !=  null) {
                        System.out.println(dm.table.getModel().getValueAt(i, 0));
                        rw[i]=dm.table.getModel().getValueAt(i,0).toString();
                    }
                }
                list.setRWords(rw);
                p.addMatches(list.matchWords(),list.matchMetaphone());
                p.addRWords(rw);
                dm.clearPanel();
                dm.setInstructionPanel(loadText(this.getClass().getResourceAsStream(MOVIE_ONE_INSTR_FILENAME)));
                dm.instrButton.setActionCommand("playMovieOne");
                dm.instrButton.addActionListener(instrButtonTask);
                dm.showPanel();
                //dm.remTable();
         //p.printRWords();
         //System.out.println("NUMBER OF MATCHED WORDS: ");// + p.matchWords());
        // p.addResponses();
        // p.matchMetaphone();
        // p.addEMatches();
        // p.addMMatches();
         //p.writeXmlFile();
         //p.writeDataToFile();
         
            }
         };
         
    }
     
     private void createDistractTimerTask() {
          distractTimerTask = new ActionListener() {
             public void actionPerformed(ActionEvent evt) {
                dTimer.stop();
                dm.clearPanel();
                dm.setSingleQuesPanel(COUNTDOWN_TEXT);
                dm.instrButton.setActionCommand("timeSubmit");
                dm.instrButton.addActionListener(instrButtonTask);
                dm.showPanel();
                int i;
                for (i=0;i<dm.mainPanel.getComponentCount();i++) {
                    if (dm.mainPanel.getComponent(i) instanceof javax.swing.JTextField) {
                        dm.mainPanel.getComponent(i).requestFocusInWindow();
                    }
                 }
                
         }
         
        };  
     }
     
     
    private void loadVASText(InputStream stream) {
        BufferedReader in;
	StringTokenizer tokens;
	String line;
        int i = 0;
	try {
            vasText = new String[NUM_VAS_QUESTIONS][2];
            in= new BufferedReader(new InputStreamReader(stream));
            line = null;
            while ((line = in.readLine()) != null) {
                tokens = new StringTokenizer(line, ",");
                while (tokens.hasMoreTokens()) {
                    vasText[i][0] = tokens.nextToken();
                    vasText[i][1] = tokens.nextToken();    
                    i++;
                }
              
               
            }//end while
            in.close();
       
	}catch (IOException e) {
           dm.showError("Error Loading Text - using defaults");
           vasText = new String[6][2];
           vasText[0][0] = "NOT AT ALL SAD";
           vasText[0][1] = "EXTREMELY SAD";
           vasText[1][0] = "NOT AT ALL CURIOUS";
           vasText[1][1] = "EXTREMELY CURIOUS";
           vasText[2][0] = "NOT AT ALL HAPPY";
           vasText[2][1] = "EXTREMELY HAPPY";
           vasText[3][0] = "NOT AT ALL DEPRESSED";
           vasText[3][1] = "EXTREMELY DEPRESSED";
           vasText[4][0] = "NOT AT ALL LAZY";
           vasText[4][1] = "EXTREMELY LAZY";
           vasText[5][0] = "NOT AT ALL SPONTANEOUS";
           vasText[5][1] = "EXTREMELY SPONTANEOUS";
	}
    }	
     
    
    private String[] loadYorNText(InputStream stream, int numQuestions ) {
        BufferedReader in;
	StringTokenizer tokens;
	String line;
        String[] ynText;
        ynText = new String[numQuestions];
        int i;
        i = 0;
	try {
            in= new BufferedReader(new InputStreamReader(stream));
            line = null;
            while ((line = in.readLine()) != null) {
                tokens = new StringTokenizer(line, ",");
                while (tokens.hasMoreTokens()) {
                   ynText[i++] = tokens.nextToken();
                 }
               
           }//end while
            in.close();
            return ynText;
            
	}catch (IOException e) {
           dm.showError("Error Loading Text - using defaults");
           //DEFAULTS GO HERE
           return ynText;
	}
    }	
        
        
 private String[][] loadScaleText(InputStream stream, int numQuestions) {
     
        BufferedReader in;
	StringTokenizer tokens;
	String line;
        String[][] scaleText;
        scaleText = new String[numQuestions][3];
        int i = 0;
	try {
           
            in= new BufferedReader(new InputStreamReader(stream));
            line = null;
            while ((line = in.readLine()) != null) {
                tokens = new StringTokenizer(line, ",");
                while (tokens.hasMoreTokens()) {
                    scaleText[i][0] = tokens.nextToken();
                    scaleText[i][1] = tokens.nextToken();
                    scaleText[i][2] = tokens.nextToken();
                    i++;
                }
              
               
            }//end while
            in.close();
            for (i=0;i<numQuestions;i++) {
                System.out.println(scaleText[i][0] + ", " + scaleText[i][1] + ", " + scaleText[i][2]);
            }
            return scaleText;
            
	}catch (IOException e) {
           dm.showError("Error Loading Text - using defaults");
           return scaleText;
          //LOAD DEFAULTS HERE!;
	}
    }	
 
 private void loadDFScaleText(InputStream stream) {
       
        BufferedReader in;
	StringTokenizer tokens;
	String line;
        int i = 0;
	try {
            dfScaleText = new String[1][3];
            in= new BufferedReader(new InputStreamReader(stream));
            line = null;
            while ((line = in.readLine()) != null) {
               tokens = new StringTokenizer(line, ",");
               dfScaleText[0][0] = tokens.nextToken();
               dfScaleText[0][1] = tokens.nextToken(); 
               dfScaleText[0][2] = tokens.nextToken();
               
              
               
            }//end while
            in.close();
            for (i=0;i<1;i++) {
                System.out.println("DF SCALE TEXT: " + dfScaleText[i][0] + dfScaleText[i][1] + dfScaleText[i][2]);
            }
	}catch (IOException e) {
           dm.showError("Error Loading Text - using defaults");
           dfScaleText[i][0] = "Please rate";
           dfScaleText[i][1] = "not at all characteristic of me";
           dfScaleText[i][2] = "completely characteristic of me";
	}
    }
 
    private void setParticipantValues() {
       
        if (p.pNum % 2 > 0) {
            tsChoice = 0; //A->AA Supress = odd
        }else {
            tsChoice = 1;//B->BB Control = even
        }
    
       p.addFirstList(list.currentList = getDFListOrder(this.getClass().getResourceAsStream(RAND_PNUM_FILENAME), p.pNum));
       System.out.println("List choosen was: " + list.currentList);
       //this is jsut -> p.addFirstList(list.currentList);
       //DisplayManager.getInstance().showError("The Word list used is: " + list.currentList); //for testing
     
      
    }
    private void loadSettings(InputStream stream) throws IOException {//InputStream stream) {
        BufferedReader in;
	StringTokenizer tokens;
	String line, currToken;
        in = new BufferedReader(new InputStreamReader(stream));
        line = null;
        while ((line = in.readLine()) != null) {
            tokens = new StringTokenizer(line, "=");
            currToken = tokens.nextToken();
            if (currToken.equals("DF_TIME")) {
                currToken = tokens.nextToken();
                this.DF_TIME = Integer.parseInt(currToken);					
            }else if (currToken.equals("TS_TIME")) {
                currToken = tokens.nextToken();
                this.TS_TIME = Integer.parseInt(currToken);;
            }else if (currToken.equals("RW_TIME")) {
                currToken = tokens.nextToken();
                this.RW_TIME = Integer.parseInt(currToken);
            }else if (currToken.equals("D_TIME")) {
                currToken = tokens.nextToken();
                this.D_TIME = Integer.parseInt(currToken);
            }
                line = null;
        }//end while
			
        in.close();
     
    }
 	
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Why nothing?");
        // Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
       // final String[] s;
       // s = args;
       // System.out.println(s);
       // System.out.println(System.getProperty("jna.library.path"));
        //System.out.println(args[0]);
         //System.out.println("jdk version:  " + System.getProperty("sun.arch.data.model") + " bits.");
        try{
            if (System.getProperty("jna.nosys") == null) {
                System.setProperty("jna.nosys", "true");
            }
       }catch(IllegalStateException ise){
            System.out.println("caught :" + ise);
        }
       
        //System.out.println(System.getProperty("jna.library.path"));
        //System.out.println(System.getProperty("user.dir"));
        //System.out.println("USER DIR: " + System.getProperty("user.dir") + "\\src\\experiment");
        //NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files (x86)\\VideoLAN\\VLC" );
       // NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),System.getProperty("user.dir") + "\\src\\experiment");
        try {
            java.io.File f = new java.io.File(Experiment.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
            URL url = Experiment.class.getProtectionDomain().getCodeSource().getLocation();
            URL urlNew =  Experiment.class.getClassLoader().getResource("null");
            System.out.println("url: " + url + '\n' + "urlNew: " +  urlNew);
           //System.out.println("HERE IS THE PATH" + f.getPath());
            //String newPath =  f.getPath().replace("\\NicolaExperiment.jar", ""); //no longer needed, we are not using JAR files.
            String newPath = Experiment.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            System.out.println("newPath init: " + newPath);
            String jarName = newPath.substring(newPath.lastIndexOf("/") + 1);
            System.out.println("JAR Name: " + jarName);
          //  newPath = f.getPath().replace("\\Experiment.jar", "");

            if (!jarName.trim().isEmpty()) {
                newPath = newPath.replace(jarName, "vlc");
                newPath = newPath.trim();

            }else {
                newPath = newPath + "vlc";
            }
            //newPath = newPath + "\\vlc";
            System.out.println("newpath: " + newPath);
            System.out.println("new Path location: " + newPath);
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),newPath);
        }catch (URISyntaxException e) {
            System.out.println("URI or MalformedURL: " + e.getMessage());
            e.printStackTrace();

        }
       System.out.println("RuntimeUtil.getLibVlcLibraryName(): " + RuntimeUtil.getLibVlcLibraryName());
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        LibXUtil.initialise();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                
                try {
                    Experiment experiment = new Experiment();

                }catch (URISyntaxException e) {
                    System.out.println("URISyntaxException: " + e.getMessage());
                    e.printStackTrace();

                }
            }
        });
    }//End main method
    
    
    
    
}//End Experiment Class

