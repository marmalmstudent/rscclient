package mopar;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class XmlConverter implements ActionListener, KeyListener {
   private String[] itemName = new String[10000];
   private String[] itemID = new String[10000];
   private String fileName;
   private JTextArea namePanel;
   private JTextArea idPanel;
   private JTextArea searchPanel;

   public static void main(String[] args) {
      new XmlConverter("./all_IDs/Objects.xml");
   }

   public XmlConverter(URL xmlURL) {
      try {
         URLConnection e = xmlURL.openConnection();
         e.setRequestProperty("User-Agent", "Mozilla");
         e.connect();
         this.readStream(e.getInputStream());
      } catch (IOException var3) {
         System.out.println("Couldn\'t find the file " + this.fileName);
      }

      this.initUI();
   }

   public XmlConverter(String fileName) {
      File file = new File(fileName);
      this.fileName = file.getName();

      try {
         FileInputStream e = new FileInputStream(file);
         this.readStream(e);
      } catch (IOException var4) {
         System.out.println("Couldn\'t find the file " + fileName);
      }

      this.initUI();
   }

   private void readStream(InputStream inputStream) throws IOException {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      int linenumber = 0;

      String[] clientline;
      for(clientline = new String[30000]; (clientline[linenumber] = reader.readLine()) != null; ++linenumber) {
         ;
      }

      inputStream.close();
      int y = 0;

      for(int x = 0; x < clientline.length && clientline[x] != null; ++x) {
         if(clientline[x].contains("name=")) {
            String name = clientline[x].substring(clientline[x].indexOf("name=") + 6).replaceAll("\".*", "");
            String id = clientline[x].substring(clientline[x].indexOf("type=") + 6).replaceAll("\".*", "");
            this.itemName[y] = name;
            this.itemID[y] = id;
            ++y;
         }
      }

   }

   private void initUI() {
      JFrame.setDefaultLookAndFeelDecorated(true);
      JFrame frame = new JFrame(this.fileName);
      frame.setDefaultCloseOperation(2);
      frame.getContentPane().setLayout(new BorderLayout());
      this.namePanel = new JTextArea();
      this.namePanel.setEditable(false);
      this.idPanel = new JTextArea();
      this.idPanel.setEditable(false);
      JPanel infoPane = new JPanel(new FlowLayout());
      infoPane.add(this.namePanel);
      infoPane.add(this.idPanel);
      JScrollPane scroll = new JScrollPane(infoPane, 22, 31);
      scroll.setPreferredSize(new Dimension(240, 503));
      boolean foundOne = false;
      String newLine = "\n";

      for(int search = 0; search < this.itemName.length && this.itemName[search] != null; ++search) {
         if(foundOne) {
            this.namePanel.append(newLine + this.itemName[search]);
            this.idPanel.append(newLine + this.itemID[search]);
         } else {
            foundOne = true;
            this.namePanel.append(this.itemName[search]);
            this.idPanel.append(this.itemID[search]);
         }
      }

      JButton var8 = new JButton("Search");
      var8.addActionListener(this);
      this.searchPanel = new JTextArea();
      this.searchPanel.addKeyListener(this);
      this.searchPanel.setLineWrap(false);
      this.searchPanel.setRows(1);
      this.searchPanel.setColumns(9);
      JPanel searchPane = new JPanel(new FlowLayout());
      searchPane.add(this.searchPanel);
      searchPane.add(var8);
      frame.getContentPane().add(scroll, "Center");
      frame.getContentPane().add(searchPane, "South");
      frame.pack();
      frame.setVisible(true);
      this.searchPanel.requestFocus();
   }

   private void search() {
      String substring = this.searchPanel.getText();
      this.namePanel.setText("");
      this.idPanel.setText("");
      boolean foundOne = false;
      String newLine = "\n";

      for(int x = 0; x < this.itemName.length && this.itemName[x] != null; ++x) {
         if(this.itemName[x].toLowerCase().contains(substring.toLowerCase())) {
            if(foundOne) {
               this.namePanel.append(newLine + this.itemName[x]);
               this.idPanel.append(newLine + this.itemID[x]);
            } else {
               foundOne = true;
               this.namePanel.append(this.itemName[x]);
               this.idPanel.append(this.itemID[x]);
            }
         }
      }

      if(this.namePanel.getText().equals("")) {
         this.namePanel.setText("No Results Found");
      }

   }

   public void actionPerformed(ActionEvent evt) {
      this.search();
   }

   public void keyPressed(KeyEvent evt) {
      if(evt.getKeyCode() == 10) {
         this.search();
      }

   }

   public void keyReleased(KeyEvent evt) {
      if(evt.getKeyCode() == 10) {
         this.searchPanel.setText(this.searchPanel.getText().replace("\n", ""));
      }

   }

   public void keyTyped(KeyEvent evt) {
   }
}
