   /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.StringTokenizer;
import org.apache.commons.codec.language.Metaphone;

/**
 *
 * @author jchafe
 * This class contains the list of words used by dirForget portion of the experiment as well as the recalled words portion
 * 
*/

public class WordList {
     private String[] wordListFull;
     private String[] wordListA;
     private String[] wordListB;
     private String[] rWords;
     public int currentList;
     private int currentAWord;
     private int currentBWord;
     private int totalWords;
     
    public WordList(InputStream wlFile) {
        currentAWord = 0;
        currentBWord = 0;
        currentList = 0;
        totalWords = 0;
        
        wordListFull = new String[Experiment.NUM_WORDS];
        loadWords(wlFile);
        setWordListAB();
        printWordList();
        printWordListAB();
        randomizeWordList(0);
        randomizeWordList(1);
        printWordListAB();
        //TESTING REMOVE LATER:
        String[] testWordsA = new String[20];
        loadWords(this.getClass().getResourceAsStream("CSV/wordsB.csv"),testWordsA);
        System.out.println("Does TestWordList B = WordList B: " + testWordLists(testWordsA, wordListB));
        
    }  
    
    
    private String getNextWordListB() {
        if (currentBWord < wordListB.length) {
            totalWords++;
            return wordListB[currentBWord++];
        }else
            return null; //throw error!!
    }
    
    private String getNextWordListA() {
        if (currentAWord < wordListA.length) {
            totalWords++;
            return wordListA[currentAWord++];
        }else
            return null; //throw error!!
    }
    
    
    public String getNextWord() {
        if (currentList == 0) {
            return getNextWordListA();
            
        }else {
            return getNextWordListB();
        }    
    }       
    
    public String getCurrentWord() {
        if (currentList == 0) {
            return wordListA[currentAWord-1];
        }else
            return wordListB[currentBWord-1];
    }
    
    public int getCurrentWordPos() {
        if (currentList == 0) {
            return currentAWord;
            
        }else
            return currentBWord;
    }
    
    public boolean listHasMoreWords() {
        if ((currentList == 0 && currentAWord < wordListA.length) || (currentList == 1 && currentBWord < wordListB.length) )  {
            return true;
            
        }else {
            return false;
        }    
    }
     
    public boolean areBothListsDone() {
        if (totalWords == wordListFull.length) {
            return true;
        }else {
            return false;
        }    
    }
    
    private void setWordListAB() {
        int i;
        wordListA = new String[wordListFull.length/2];
        wordListB = new String[wordListFull.length/2];
        for (i=0;i<wordListFull.length/2;i++) {
            wordListA[i] = wordListFull[i];
            wordListB[i] = wordListFull[i+wordListFull.length/2];
        }
        
    }
    
    
    public void printWordListAB() {
        int i;
        for (i=0;i<wordListA.length;i++) {
            System.out.println("wordListA[" + i + "] = " + wordListA[i]);
       }
        for (i=0;i<wordListB.length;i++) {
            System.out.println("wordListB[" + i + "] = " + wordListB[i]);
      }
    }

    public void setRWords(String[] words) {
        rWords = words;
    }
    
    public String[] getRWords() {
        return rWords;
    }
    
    public void printRWords() {
        int i;
        for (i=0;i<rWords.length;i++) {
            if (rWords[i] != null ) {
                System.out.println(rWords[i]);
            }
        }
    }
    
    public void printWordList() {
        int i;
        for (i=0;i<wordListFull.length;i++) {
            System.out.println(wordListFull[i]);
        }
    }
    
    public void loadWordList() {
        
    }
    
    private void randomizeWordList(int type) {
        int i, randomPos;
        
        String temp;
    
        Random rgen = new Random();
        if (type==0) {
            for (i = 0; i < wordListA.length; i++ ) {
                randomPos = rgen.nextInt(wordListA.length);
                temp = wordListA[i];
                wordListA[i] = wordListA[randomPos];
                wordListA[randomPos] = temp;
            }
        }else {
            //randomize wordlistB;
             for (i = 0; i < wordListB.length; i++ ) {
                randomPos = rgen.nextInt(wordListB.length);
                temp = wordListB[i];
                wordListB[i] = wordListB[randomPos];
                wordListB[randomPos] = temp;
            }
        } 
    }
    

    
    public int matchWords() {
        int i,j, match;
        match = j = 0;     
        boolean found;
        for (i=0;i<rWords.length;i++) {
            found = false;
             j = 0;
            while (rWords[i]!=null & !found & j<wordListFull.length) {
                if (rWords[i].equalsIgnoreCase(wordListFull[j++])) {
                    match++;
                    found = true;
                }
            }
                    
                    
         }
        return match;
     }
    
    public int matchMetaphone() {
        Metaphone m = new Metaphone();
        int i,j, match;
        match = j = 0;     
        boolean found;
        for (i=0;i<rWords.length;i++) {
            found = false;
             j = 0;
            while (rWords[i]!=null & !found & j<wordListFull.length) {
                if (m.isMetaphoneEqual( rWords[i],wordListFull[j++])) {
                   System.out.println("****************************METAPHONE MATCH: " + rWords[i] + ", " + wordListFull[j-1]);
                   match++;
                   found = true;
                }
            }
                
         }
     
       return match;
   
    }
    
    private void loadWords(InputStream stream) {
        BufferedReader in;
        StringTokenizer tokens;
        String line, currToken;
        int i = 0;
        
        try {
            in = new BufferedReader(new InputStreamReader(stream));
            line = null;
            while ((line = in.readLine()) != null) {
                tokens = new StringTokenizer(line, ",");
		while(tokens.hasMoreTokens() & i < wordListFull.length) {
                    currToken = tokens.nextToken();
                    wordListFull[i++] = currToken;
                }
               line = null;
            }//end while
			
            in.close();
	
        }catch (IOException e) {
           System.err.println("Error loading words");
	}
    }	
    
        //For testing!
        private void loadWords(InputStream stream, String[] words) {
        BufferedReader in;
        StringTokenizer tokens;
        String line, currToken;
        int i = 0;
        
        try {
            in = new BufferedReader(new InputStreamReader(stream));
            line = null;
              while ((line = in.readLine()) != null) {
                tokens = new StringTokenizer(line, ",");
		while(tokens.hasMoreTokens() & i < words.length) {
                    currToken = tokens.nextToken();
                    words[i++] = currToken;
                }
                
               line = null;
            }//end while
			
            in.close();
	
        }catch (IOException e) {
           System.err.println("Error loading words");
	}
    }	
        
        private boolean testWordLists(String[] tWords, String[] words) {
            int i, j;
            boolean [] matched = new boolean[tWords.length];
            for (i=0; i < tWords.length; i++) {
                for (j =0; j < words.length; j++) { 
                    if (tWords[i].equals(words[j])) {
                        matched[i] = true;
                    }
                }
            }
            for (i = 0; i < matched.length; i++) {
                if (!matched[i]) {
                    return false;
                }
            }
            return true;
        
        }
        
}
