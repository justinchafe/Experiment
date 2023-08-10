/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package experiment;

/**
 *
 * @author jchafe
 */



public class Participant {
    
   // private String[] wordList;
    private String[] rWords;
    private String[][] dFAnswers; //{{word,1-5 rating}, ....}
    private int exactMatches; 
    private int metaMatches;
    //private String[] recalledWords;
    private String dFFirstList; //which list was chosen first 
    private String AorB;//List A or B choosen for thought supression task
    private String[][] vasAnswers;//{{x,x,x,x,x,x},{x,x,x,x,x,x}....}
    private String[] ynTSAnswers;
    private String[] ynMovieOneAnswers; //{{y,n},{n,n}...}
    private String[] ynMovieTwoAnswers;
    private String countDownAnswer;//what number did you arrive at
    private String[][] tsAnswers;//{{listAorB, numPress},{listAAorBB,numPress}}
    private String[] tsScaleAnswers;//{,...}
    private String[] movieOneScaleAnswers;
    private String[] movieTwoScaleAnswers;
    private String openEndedAnswer;
    
    private int numDFResp;
    private FileManager fm;
    public int pNum;
    public int sNum;
   
    
    /*
    public Participant(String pNum, InputStream is, String outputFile) {
       // wordList = new String[WordRecall.NUM_WORDS]; //no longer any place for word list!!!
        rWords = new String[WordRecall.NUM_WORDS];
        this.pNum = Integer.parseInt(pNum);
        String xmlFilename = outputFile + "_" + pNum + ".xml";
        String csvFilename = outputFile + "_" + pNum + ".csv";
        fm = new FileManager(xmlFilename,csvFilename, "Participant");
        System.out.println("THIS IS PNUM: " + pNum);
        fm.writeXmlFile();
        //createFile(csvFilename);
        //loadWords(is);
        //printWordList();
     
    }
    */
    
     public Participant(String pNum,String outputFile) {
        //wordList = new String[WordRecall.NUM_WORDS];
       
        numDFResp = 0;
        rWords = new String[Experiment.NUM_WORDS];
        dFAnswers = new String[Experiment.NUM_WORDS][2];
        tsAnswers = new String[2][2];
        vasAnswers = new String[Experiment.NUM_VAS][Experiment.NUM_VAS_QUESTIONS];
        ynTSAnswers = new String[Experiment.NUM_TS_YN_QUESTIONS];
        tsScaleAnswers = new String[Experiment.NUM_TS_SCALE_QUESTIONS];
        ynMovieOneAnswers = new String[Experiment.NUM_MOVIE_ONE_YN_QUESTIONS];
        movieOneScaleAnswers = new String[Experiment.NUM_MOVIE_ONE_SCALE_QUESTIONS];
        ynMovieTwoAnswers = new String[Experiment.NUM_MOVIE_TWO_YN_QUESTIONS];
        movieTwoScaleAnswers = new String[Experiment.NUM_MOVIE_TWO_SCALE_QUESTIONS];
      
        
        this.pNum = Integer.parseInt(pNum);
        String xmlFilename = outputFile + "_" + pNum + ".xml";
        String csvFilename = outputFile + "_" + pNum + ".csv";
        fm = new FileManager(xmlFilename,csvFilename, "Participant");
        FileManager.writeHeaders();
        setXmlFile();
      
   }
     
     private void setXmlFile() {
      
        fm.addSingleResponse(pNum, "pNum");
        fm.addElement("DFRatings");
        fm.addSubElement("DFRatings", "listA");
        fm.addSubElement("DFRatings", "listB");
        fm.addElement("RWords");
        fm.addElement("vasAnswers");
        fm.addElement("tsAnswers");
        fm.writeXmlFile();
     }
     
     public void addFirstList(int firstList) {
         if (firstList == 0) {
            this.dFFirstList = "A";
         }else {
             this.dFFirstList = "B";
         }
         fm.addSingleResponse(this.dFFirstList, "firstList");
         fm.writeXmlFile();
     }

     public void addDFResponse(int rating, String word, int list) {
         dFAnswers[numDFResp][0] = word;
         dFAnswers[numDFResp++][1] = Integer.toString(rating);
         String listType;
         if (list == 0) {
             listType = "listA";
         }else {
             listType = "listB";
         }
         fm.addSingleResponse(listType,rating,word);
         fm.writeXmlFile();
     }
     
     public void addMatches(int numExact, int numMeta) {
        this.exactMatches = numExact;
        this.metaMatches = numMeta;
       
        fm.addSingleResponse("RWords", this.exactMatches, "exactMatches");
        fm.addSingleResponse("RWords", this.metaMatches, "metaMatches");
        fm.writeXmlFile();
     }
     
     public void addRWords(String [] rWords) {
         this.rWords = rWords;
         fm.addMultipleResponses(this.rWords, "RWords", "recalledWord");
         fm.writeXmlFile();
     }
    
     public void addVAS(int vasNum, int[] vAnswers) {
         int i;
         fm.addSubElement("vasAnswers", "vas" + Integer.toString(vasNum));
         for (i=0;i<vAnswers.length; i++) {
             this.vasAnswers[vasNum][i] = Integer.toString(vAnswers[i]);
             fm.addSingleResponse("vas" + Integer.toString(vasNum),vAnswers[i], "vAns" + Integer.toString(i));             
            }
         fm.writeXmlFile();
     }
         
      /*
     public void addYNTSAnswer(String[] ans) {
         int i;
         fm.addElement("ynTSAnswers");
         for (i=0; i < ans.length; i++) {
             ynTSAnswers[i] = ans[i];
             fm.addSingleResponse("ynTSAnswers",ynTSAnswers[i],"ynAns" + Integer.toString(i));
         }    
         fm.writeXmlFile();
         
     }
     */
     /*
     public void addTSScaleAnswer(String[] ans) {
         int i;
         fm.addElement("tsScaleAnswers");
         for (i=0; i < ans.length; i++) {
             tsScaleAnswers[i] = ans[i];
             fm.addSingleResponse("tsScaleAnswers",tsScaleAnswers[i],"scaleAns" + Integer.toString(i));
         }    
         fm.writeXmlFile();
         
     }
     
     */
           
     public void addYNAnswer(String[] ans, int whichOne) {
         int i;
         String title;
         switch (whichOne) {
            case 1: 
                title = "ynMovieOneAnswers";
                fm.addElement(title); 
                for (i=0; i < ans.length; i++) {
                    ynMovieOneAnswers[i] = ans[i];
                    fm.addSingleResponse(title,ynMovieOneAnswers[i],"ynAns" + Integer.toString(i));
                }    
                fm.writeXmlFile();
                break;
                
            case 2:
                title = "ynMovieTwoAnswers";
                fm.addElement(title);
                  for (i=0; i < ans.length; i++) {
                    ynMovieTwoAnswers[i] = ans[i];
                    fm.addSingleResponse(title,ynMovieTwoAnswers[i],"ynAns" + Integer.toString(i));
                }    
                fm.writeXmlFile();
                break;
                
            case 3:
                title =  "ynTSAnswers";
                fm.addElement(title);
                for (i=0; i < ans.length; i++) {
                    ynTSAnswers[i] = ans[i];
                    fm.addSingleResponse(title,ynTSAnswers[i],"ynAns" + Integer.toString(i));
                }    
                fm.writeXmlFile();
                break;
                   
                
         }
        
         
     }
     
     
      public void addScaleAnswer(String[] ans, int whichOne) {
         int i;
         String title;
         
         switch (whichOne) {
             case 1:
                 title = "movieOneScaleAnswers";
                 fm.addElement(title);
                 for (i=0; i < ans.length; i++) {
                    movieOneScaleAnswers[i] = ans[i];
                    fm.addSingleResponse(title,movieOneScaleAnswers[i],"scaleAns" + Integer.toString(i));
                   }
                 fm.writeXmlFile();
                 break;
                 
             case 2:
                 title = "movieTwoScaleAnswers";
                 fm.addElement(title);
                 for (i=0; i < ans.length; i++) {
                    movieTwoScaleAnswers[i] = ans[i];
                    fm.addSingleResponse(title,movieTwoScaleAnswers[i],"scaleAns" + Integer.toString(i));
                   }
                 fm.writeXmlFile();
                 break;
                 
             case 3:
                 title = "tsScaleAnswers";
                 fm.addElement(title);
                 for (i=0; i < ans.length; i++) {
                    tsScaleAnswers[i] = ans[i];
                    fm.addSingleResponse(title,tsScaleAnswers[i],"scaleAns" + Integer.toString(i));
                }    
                fm.writeXmlFile();
                 
            }
        
     }
     
      public void addCountdownAnswer(String num) {
          this.countDownAnswer = num;
          fm.addSingleResponse(num, "countDownAnswer");
          fm.writeXmlFile();
      }
      
      
      public void addOpenEndedAnswer(String resp) {
          resp = resp.replace("\r\n", " ").replace("\n", " "); //remove pesky line breaks!
          this.openEndedAnswer = resp;
          fm.addSingleResponse(resp, "openEndedAnswer");
          fm.writeXmlFile();
      }
      
      
     public void addTsAnswers(int pos, String listType,int numSpacePressed) {
         switch (pos) {
             case 0:
                tsAnswers[0][0] = listType;
                tsAnswers[0][1] = Integer.toString(numSpacePressed);
                fm.addSingleResponse("tsAnswers",Integer.toString(numSpacePressed), listType);
                fm.writeXmlFile();
                break;
             case 1:
                tsAnswers[1][0] = listType;
                tsAnswers[1][1] = Integer.toString(numSpacePressed);
                fm.addSingleResponse("tsAnswers",Integer.toString(numSpacePressed), listType);
                fm.writeXmlFile(); 
                 
         }
     }
    
     public String[] getRWords() {
         return rWords;
     }
     
     public String[][] getDFAnswers() {
         return dFAnswers;
     }
     
    public int getExactMatches() {
        return exactMatches;
    } 
    
    public int getMetaMatches() {
        return metaMatches;
    }
    
    public String[] getRecalledWords() {
        return rWords;
    }
    
    public String getDFFirstList() {
        return dFFirstList;
    }
    
    public String getAorB() {
        return AorB;
    }//List A or B choosen for thought supression task
    
    public String[][] getVasAnswers() {//{{x,x,x,x,x,x},{x,x,x,x,x,x}....}
        return vasAnswers;
    }

    public String[] getYNTSAnswers() {
        return ynTSAnswers;
    }
    
    public String[] getYNMovieOneAnswers() {//{{y,n},{n,n}...}
        return ynMovieOneAnswers;
    }
    
    public String[] getYNMovieTwoAnswers() {
        return ynMovieTwoAnswers;
    }
   
    public String getCountDownAnswer() {//what number did you arrive at
        return countDownAnswer;
   }
    
    public String[][] getTSAnswers() {//{{listAorB, numPress},{listAAorBB,numPress}}
        return tsAnswers;
    }
    
    public String[] getTSScaleAnswers() {//{,...}
        return tsScaleAnswers;
    }
    
    public String[] getMovieOneScaleAnswers() {
        return movieOneScaleAnswers;
    }
    
    public String[] getMovieTwoScaleAnswers() {
        return movieTwoScaleAnswers;
    }
    
    public String getOpenEndedAnswer() {
        return openEndedAnswer;
    }
    
    public void writeParticipantInfo() {
        fm.writeCSVInfo(this.toString());
    }
    
    
    @Override
    public String toString() {
        int i;
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(pNum).append(", ");
        sb.append(dFFirstList).append(", ");
        
        //vas1
        sb.append(writeResponses(vasAnswers[0]));
        
        //dfAnswers
        for (i = 0; i <dFAnswers.length; i++) {
            sb.append(dFAnswers[i][0]).append(", ");
            sb.append(dFAnswers[i][1]).append(", ");
        }
        
        sb.append(countDownAnswer).append(", ");
        sb.append(exactMatches).append(", ");
        sb.append(metaMatches).append(", ");

        //recalled words
        sb.append(writeResponses(rWords));
             
        //vas 2
        sb.append(writeResponses(vasAnswers[1]));
        
        //ynMovieOne Answrs:
        sb.append(writeResponses(ynMovieOneAnswers));
                       
       //movieOneScale Answers:
        sb.append(writeResponses(movieOneScaleAnswers));
        
        //write TSAnswers:
        for (i=0; i < tsAnswers.length; i++) {
            sb.append(tsAnswers[i][0]).append(", ");
            sb.append(tsAnswers[i][1]).append(", ");
        }
        
        //vas 3
        sb.append(writeResponses(vasAnswers[2]));
        
        //YNTSAnswers:
        sb.append(writeResponses(ynTSAnswers));
           
        //TSScale Answers:
        sb.append(writeResponses(tsScaleAnswers));
           
        //vas 4
        sb.append(writeResponses(vasAnswers[3]));
        
        //ynMovieTwo Answrs:
        sb.append(writeResponses(ynMovieTwoAnswers));
         
        //movieTwoScale Answers:
        sb.append(writeResponses(movieTwoScaleAnswers));
        sb.append(openEndedAnswer);
        return sb.toString();      			
    }
    
     private String writeResponses(String[] arr) {
           int i;
           StringBuilder resp = new StringBuilder();
            for (i=0; i < arr.length; i++) {
                resp.append(arr[i]).append(", ");
            }
            
            return resp.toString();
        }
     
         
}


   

    

