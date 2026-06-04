package find.duplicates;

public class FindDuplicates {
   public static void main(String[] args) {
	   javax.swing.SwingUtilities.invokeLater(() -> {
           try {
        	   MainForm mainform = new MainForm();
//               new MainForm(mainform);
           } catch (Exception e) {
               e.printStackTrace(); // show errors in Console
           }
       });
   }
}