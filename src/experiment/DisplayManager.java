/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package experiment;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.util.Hashtable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 *
 * @author jchafe
 */

public class DisplayManager extends JFrame {
     public final EmbeddedMediaPlayerComponent mediaPlayerComponent;
     
     public final int SCALE_LIMIT = 20;
     private static final DisplayManager instance = new DisplayManager();
     private JEditorPane jep;
     private JPanel content;
     private JLabel dFLabel;
     private JLabel htmlLabel;
     
     public final DefaultTableModel tableModel = new DefaultTableModel(Experiment.NUM_WORDS, 1);
     public JSlider vas;
     public JSlider[] mVas;
     public JTable table;
     public JButton instrButton;
     public JPanel mainPanel; 
     public JRadioButton[][] rsButtons;
     
     public JTextField getTime;
     public JTextArea openEnd;
     public JComboBox[] yn;   
     
     public String font;
     private int headerFontSize; //header font size (for JLables
     private int secFontSize; //secondary font size (JLabels)
     private int iPageFontSize; //instruction font size (HTML)
     private int iHeaderFontSize; //instruction font size above other items (HTML)
     private int dfFontSize; //the directed forgetting word font size

    
    Dimension dim;
     
     
    private DisplayManager() {
        font = "Arial";
        Toolkit toolkit =  Toolkit.getDefaultToolkit();
        dim = toolkit.getScreenSize();
        content = new JPanel();
        //content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.setLayout(new MigLayout("align 50% 50%"));
        this.setContentPane(content);
        this.setUndecorated(true); //remove decorations e.g. x in top right
        this.setAlwaysOnTop(true);
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        setFontSizes();
    }
    
    //adds HTML to a string of text, with support adjustable font Size. (pass in text (can have all formatting ready besides open/end tags).  i.e. label.setText(createHTML(someText, iFontSize));
    private String createHTML(String text, int fontSize) {
          StringBuilder sb;
          sb = new StringBuilder();
          sb.append("<html><p align = \"center\"><font size = " + fontSize + ">" );
          sb.append(text);
          sb.append("</font></p></html>");
          return sb.toString();
    }
    
    //Adjustable font sizes.
    private void setFontSizes() {
        Double w = getCurrentResolution().getWidth();
        System.out.println("Screen Width: " + w);
        if (w >= 1920) {
            iPageFontSize = 8; //HTML Font Attribute Sizing
            iHeaderFontSize = 6;
            headerFontSize = 16;
            secFontSize = 14;
        }else if (w <=1440 && w > 1280) {
            iPageFontSize = 7;
            iHeaderFontSize = 5;
            headerFontSize = 14;
            secFontSize = 12;
        }else if (w <= 1280 && w > 1024) {
            iPageFontSize = 6;
            iHeaderFontSize = 4;
            headerFontSize = 12;
            secFontSize = 10;
        }else {
            iPageFontSize = 5;
            iHeaderFontSize = 3;
            headerFontSize = 11;
            secFontSize = 9;
        }
        
        dfFontSize = 45;
        
    }

    public void setFont(String fontName) {
       font = fontName;
   } 
   
    public Dimension getCurrentResolution() {
       // Get the default toolkit
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        return toolkit.getScreenSize();
    }
    
    public static DisplayManager getInstance() {
        return instance;
    }
     
    public void showMovie(String filePath) {
        this.setContentPane(mediaPlayerComponent);
        this.setExtendedState(MAXIMIZED_BOTH); //maximise window
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
        mediaPlayerComponent.getMediaPlayer().playMedia(filePath);

     }

    public void clearMovie() {
        mediaPlayerComponent.getMediaPlayer().stop();
       // mediaPlayerComponent.release(); //maybe not...
    }
    
    public String getNumber(String dialogText, String errorText) {
        String num;
        while ( (num = (String)JOptionPane.showInputDialog(this,dialogText)) != null && !(num.matches("\\d+")) ) {
            JOptionPane.showMessageDialog(this,errorText);
	}
	if (num == null) {
            System.exit(0);
	}
        return num;
    }
    
    public void setTable(String header, int rHeight) {
       
        table = new JTable(tableModel);
        JTableHeader th = table.getTableHeader();
        TableColumnModel tcm = th.getColumnModel();
        TableColumn tc = tcm.getColumn(0);
        tc.setHeaderValue(header);
        table.setRowHeight(rHeight);
        
        JScrollPane tScroll = new JScrollPane(table);
        tScroll.setMinimumSize(new Dimension(800,600));
        content.add(tScroll, "align center");
        //content.add(tScroll, "gapleft " + (content.getWidth()/2 - tScroll.getWidth()/2));
        validate();
        //content.add(tScroll, "gapleft " + (content.getWidth()/2 - tScroll.getWidth()/2) + ", gaptop " + (content.getHeight()/2 - tScroll.getHeight()/2));
   }
     
    public void setSingleVasPanel(String lowText, String highText, int length, int spacing, int minSize, int initPos) {
       vas = new JSlider(JSlider.HORIZONTAL,0, length,initPos);
       vas.setMajorTickSpacing(spacing);
       vas.setPaintTicks(true);
       Hashtable labelTable = new Hashtable();
       labelTable.put( new Integer( 0 ), new JLabel("low") );
       labelTable.put( new Integer( length ), new JLabel("High") );
       vas.setLabelTable( labelTable );
       vas.setPaintLabels(true);
       vas.setMinimumSize(new Dimension(minSize,25));
       content.add(vas);
       content.revalidate();
   }
   
    public void setVasPanel(int numVas, String[][] lhText, String topText) {
       int i;
       JScrollPane scroller;
     
       JEditorPane vasJep = new JEditorPane();
       vasJep.setBackground(this.getBackground());
       vasJep.setEditable(false);   
       vasJep.setContentType("text/html");
       vasJep.setText(createHTML(topText,iHeaderFontSize));
       vasJep.setMaximumSize(new Dimension(500,500));
       int length = 100;
       int minSize = 500;

       mVas = new JSlider[numVas];
       mainPanel = new JPanel(new MigLayout());
       instrButton = new JButton("Submit");
       
       mainPanel.add(vasJep, "span 3, gapBottom 3%, align center, wrap");

       for (i =0; i<mVas.length; i++) {
           mVas[i] = new JSlider(JSlider.HORIZONTAL, 0, length, 50);
           //mVas[i].setValue(50);
           //vas.setMajorTickSpacing(10);
           //vas.setPaintTicks(true);
           Hashtable labelTable;
           labelTable = new Hashtable();
           labelTable.put( new Integer( 0 ), new JLabel("0"));
           labelTable.put( new Integer( length ), new JLabel("100"));
           mVas[i].setLabelTable(labelTable);
           mVas[i].setPaintLabels(true);
           mVas[i].setMinimumSize(new Dimension(minSize,30));
           
           //mVas[i].setMaximumSize(new Dimension(minSize,25));
           //mVas[i].setPreferredSize(new Dimension(minSize,25));
           //p[i] = new JPanel();
           //p[i].add(mVas[i]); 
            JLabel low = new JLabel(lhText[i][0]);
            JLabel high = new JLabel(lhText[i][1]);
            low.setFont(new Font(font,1,secFontSize));
            high.setFont(new Font(font,1,secFontSize));
            mainPanel.add(low, "gaptop 2%, align center");
            mainPanel.add(mVas[i], "gaptop 2%");
            mainPanel.add(high, "gaptop 2%, align center, wrap");
        }
       
       mainPanel.add(instrButton, "span 3, gapTop 3%, align center");
       
       //content.setVisible(false);
       scroller = new JScrollPane(mainPanel);
       scroller.setBorder(BorderFactory.createEmptyBorder());
       scroller.setMinimumSize(new Dimension(600,600));
       //content.add(scroller, "50% 50%");
       //content.add(scroller, "gaptop 3%, gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", align center");
       content.add(scroller, "align center");
        content.validate();
      // content.add(scroller, "gaptop 3%, gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", align center");
       
     /*
       content.add(mainPanel, "gaptop " + (content.getHeight()/2 - mainPanel.getHeight()/2 ) +  ", gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", align center");
       content.validate();
       content.add(mainPanel, "gaptop " + (content.getHeight()/2 - mainPanel.getHeight()/2 ) +  ", gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", align center");
      */
      
       //content.setVisible(true);
       validate();
   }

    public void setDirForgetPanel(String word) {
        
        if (word == null) {
            word = "NULL WORD";
        }
        mainPanel = new JPanel();
        dFLabel = new JLabel();
        dFLabel.setFont(new Font("Arial",1,dfFontSize));
        mainPanel.add(dFLabel); 
        dFLabel.setText(word);
        System.out.println(dFLabel.getText());
        Graphics g = this.getGraphics();
        FontMetrics met = g.getFontMetrics(dFLabel.getFont());
        int height = met.getHeight();
        int width = met.stringWidth(dFLabel.getText());
        content.add(mainPanel, "align center");
        //content.add(mainPanel, "gaptop " + (content.getHeight()/2 - height/2 ) +  ", gapleft " + (content.getWidth()/2 - width/2) + ", align center");
        //dFLabel.validate();
        //mainPanel.validate();
       // content.setVisible(true);
        validate();
  }

    public void setInstructionPanel(String text) {
        int gap = 10;
        int bGap = 5;
       // mainPanel = new JPanel(new MigLayout()); //2023
        instrButton = new JButton("ok");
        instrButton.setMinimumSize(new Dimension(60,60));
        jep = new JEditorPane();
        jep.setBackground(this.getBackground());
        jep.setEditable(false);   
        jep.setContentType("text/html");
        jep.setText(createHTML(text, iPageFontSize));
      //  mainPanel.add(jep); //2023
      //  mainPanel.add(instrButton,"align center"); //2023
        // content.add(jep, "gaptop 3%, gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", align center"); //FAIL
      //  content.add(jep, "gaptop" + gap + "%, gapleft" + gap + "%, gapright" + gap + "%, align center, wrap"); //TODO!!!
      //  content.add(instrButton, "gaptop" + bGap + "%, align center");
        content.add(jep, "align center, wrap");
        content.add(instrButton, "align center");
       // content.add(mainPanel,"gaptop" + (content.getHeight()/2 - mainPanel.getHeight()/2) +  ", gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", wrap");
       // content.validate();
       // content.add(mainPanel,"gaptop" + (content.getHeight()/2 - mainPanel.getHeight()/2) +  ", gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", wrap");

        validate();
       //Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
       // content.setLocation(dim.width/2-content.getSize().width/2, dim.height/2-content.getSize().height/2);

        
     }

    public void setRatingScalePanel(int scaleNum, String rsLowText, String rsHighText, String ratingText) {
        int i;
    
        mainPanel = new JPanel(new MigLayout());
        mainPanel.setPreferredSize(new Dimension(435,80));
        rsButtons = new JRadioButton[1][scaleNum];
        
       
        //ratingLabel = new JLabel(ratingText);
        //rsLow = new JLabel(rsLowText);
        //rsHigh = new JLabel(rsHighText);
       // rsLow.setFont(new Font(font,1,rbLabelSize)); 
       // rsHigh.setFont(new Font(font,1,rbLabelSize)); 
        //ratingLabel.setFont(new Font(font,1,labelSize));  
        
        createRatingScale(0,5, rsLowText,rsHighText, ratingText);
       
        //Graphics g = this.getGraphics();
        //FontMetrics met = g.getFontMetrics(ratingLabel.getFont());
        //int height = met.getHeight();
        //int width = met.stringWidth(ratingLabel.getText());
       // content.add(mainPanel, "gaptop " + (content.getHeight()/2 ) +  ", gapleft " + (content.getWidth()/2-100) + ", wrap");
        content.add(mainPanel, "align center");
        validate();
        //This is necessary because without setPreferredSize() the main Panel has no dimensions set when not yet rendered.... 
        //content.add(mainPanel, "gaptop " + (content.getHeight()/2 - mainPanel.getHeight()/2) +  ", gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", wrap");
        System.out.println("the width is: " + mainPanel.getWidth()/2);
        System.out.println("the height is: " + mainPanel.getHeight()/2);
        //content.setVisible(true);
   
      }
    
    public void setBlankPanel() {
         mainPanel = new JPanel();
         content.add(mainPanel);
         validate();
     }

    public void setSingleQuesPanel(String qText) {
         mainPanel = new JPanel(new MigLayout());
         JLabel ques = new JLabel(qText);
         ques.setFont(new Font(font,1,headerFontSize));
         getTime = new JTextField();
         getTime.setMinimumSize(new Dimension(60,30));
         instrButton = new JButton("Submit");
         mainPanel.add(ques);
         mainPanel.add(getTime, "wrap");
         mainPanel.add(instrButton, "span 2, align center");
         content.add(mainPanel, "align center");
        // content.add(mainPanel,"gaptop" + (content.getHeight()/2 - mainPanel.getHeight()/2) +  ", gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", wrap");
         //content.validate();
         //content.add(mainPanel,"gaptop" + (content.getHeight()/2 - mainPanel.getHeight()/2) +  ", gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", wrap");
         validate();
         
      }
     
    public void setOpenEndedQuesPanel(String qText) {
        mainPanel = new JPanel(new MigLayout());
        jep = new JEditorPane();
        jep.setBackground(this.getBackground());
        jep.setEditable(false);   
        jep.setContentType("text/html");
        jep.setText(createHTML(qText,iHeaderFontSize));
       
        openEnd = new JTextArea(10,60);
        JScrollPane scrollPane = new JScrollPane(openEnd);
        openEnd.setEditable(true);
        openEnd.setLineWrap(true);
        openEnd.setWrapStyleWord(true);        
        instrButton = new JButton("Submit");
        mainPanel.add(jep, "wrap");
        mainPanel.add(scrollPane, "wrap");
        mainPanel.add(instrButton, "align center");
        content.add(mainPanel, "align center");
        //content.add(mainPanel,"gaptop" + (content.getHeight()/2 - mainPanel.getHeight()/2) +  ", gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", wrap");
        //content.validate();
        //content.add(mainPanel,"gaptop" + (content.getHeight()/2 - mainPanel.getHeight()/2) +  ", gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", wrap");
        validate();      
        
     }
     
    private void createRatingScale(int index, int scaleNum, String rsLowText, String rsHighText, String ratingText) {
         int i;
         //int labelSize = 14;
         //int rbLabelSize = 10;
         int s = 10; //inset size
         JLabel ratingLabel, rsLow, rsHigh;
         ButtonGroup ratingScaleGroup;
         
       
         ratingScaleGroup = new ButtonGroup();
         for (i=0; i<rsButtons[index].length; i++) {
             //System.out.println("LENGTH: " + rsButtons[index].length);
             rsButtons[index][i] = new JRadioButton(new Integer(i+1).toString());
             rsButtons[index][i].setActionCommand(new Integer(i+1).toString());
             rsButtons[index][i].setMargin(new Insets(s,s,s,s));
             ratingScaleGroup.add(rsButtons[index][i]);
         }
             
      
        
        ratingLabel = new JLabel(ratingText);
        rsLow = new JLabel(rsLowText);
        rsHigh = new JLabel(rsHighText);
        rsLow.setFont(new Font(font,1,secFontSize)); 
        rsHigh.setFont(new Font(font,1,secFontSize)); 
        ratingLabel.setFont(new Font(font,1,headerFontSize));  
        
           
        mainPanel.add(ratingLabel, "span " + scaleNum + 2 + ", align center, gaptop 3%, wrap");
        mainPanel.add(rsLow, "align center");
        for (i=0;i<rsButtons[index].length;i++) {
            mainPanel.add(rsButtons[index][i]);
        }
        mainPanel.add(rsHigh,"align center,wrap");

      }
    
    public void setQuestionsPanel(int numQ, String[] questionText, int numS, int scaleLength, String[][] movieScaleText) { //String[][] lhText, String[] ratingText) {
         mainPanel = new JPanel(new MigLayout());
         mainPanel.setPreferredSize(new Dimension(500,500));
         String[] responses = {"Yes", "No"};
         yn = new JComboBox[numQ];
         int i;
         instrButton = new JButton("Submit");
         rsButtons = new JRadioButton[numS][scaleLength];
         
         for (i=0;i<numQ;i++) {
            
             mainPanel.add(new JLabel(questionText[i]), "span, align center, wrap");
             yn[i] = new JComboBox(responses);  
             if (i+1 == numQ) {
                  mainPanel.add(yn[i], "gapBottom 5%, span, align center, wrap");
                 
             }else {
                  mainPanel.add(yn[i], "gapBottom 2%, span, align center, wrap");
             }
                    
            
         }
         
         for (i=0; i<mainPanel.getComponentCount(); i++) {
              if (mainPanel.getComponent(i) instanceof JLabel) {
                    mainPanel.getComponent(i).setFont(new Font(font,1,headerFontSize));
             }
         }
         
         for (i=0;i<numS;i++) {
            createRatingScale(i, scaleLength, movieScaleText[i][1],movieScaleText[i][2],movieScaleText[i][0]); //this also adds it to mainPanel
         }
         
        mainPanel.add(instrButton, "span, gaptop 5%, align center");
        
        //content.add(mainPanel, "gaptop 5%"  +  ", gapleft " + (content.getWidth()/2-100) + ", wrap");
        content.add(mainPanel, "align center");
        revalidate();
        content.add(mainPanel, "align center");
        //This is necessary because without setPreferredSize() the main Panel has no dimensions set when not yet rendered.... 
       // content.add(mainPanel, "gaptop 5% " + ", gapleft " + (content.getWidth()/2 - mainPanel.getWidth()/2) + ", wrap");
         
     }
     
    public void clearPanel() {
        
         content = new JPanel(new MigLayout("align 50% 50%"));
         this.setContentPane(content);
         content.setVisible(false);
         revalidate();
        
    }
     
    public void showPanel() {
         content.setVisible(true);
         revalidate();
     }
          
    public void startDisplay() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setExtendedState(MAXIMIZED_BOTH);
        this.pack();
        this.setVisible(true);
     }
     
    public void showError(String error) {
          JOptionPane.showMessageDialog(this, error);
     }

}
