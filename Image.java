import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class Image extends JFrame implements ActionListener {
	JButton button1;
	JButton button2;
	JButton button3;
	JPanel panel1, panel2;
	JLabel label_1, label_2;

	public Image() throws IOException {
		ImageIcon image = null;

		System.out.println("Lese Bild von Festplatte");
		try {
			image = new ImageIcon("salzburg.jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JFrame frame = new JFrame("Image Processing"); // Fenster benennen
		frame.setBounds(100, 100, 686, 462);
		frame.setLayout(new FlowLayout());
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);

		JMenu file = new JMenu("Datei");
		menuBar.add(file);
		JMenuItem mi = new JMenuItem("Open..");
		file.add(mi);

		mi.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				int result = fileChooser.showOpenDialog(null);
				if(result == JFileChooser.APPROVE_OPTION){
					File file = fileChooser.getSelectedFile();
					
				}
					
			}
		});

		// Groesse des Fensters anhand der Bilddatei
		// Position des Fensters immer zentrieren
		frame.setSize(image.getIconWidth(), image.getIconHeight() + 150);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height
				/ 2 - frame.getSize().height / 2);

		label_1 = new JLabel();
		label_1.setIcon(image);
		frame.add(label_1);
		frame.setVisible(true);

		label_2 = new JLabel();
		// Drei Buttons werden erstellt
		button1 = new JButton("Color Model");
		button2 = new JButton("Contrast enchancement");
		button3 = new JButton("Image_Normalization");

		panel1 = new JPanel();
		panel1.setBackground(Color.ORANGE);
		panel2 = new JPanel();
		panel2.setBackground(Color.DARK_GRAY);
		panel1.add(button1);
		panel1.add(button2);
		panel1.add(button3);
		panel2.add(label_2);
		button1.addActionListener(this);
		button2.addActionListener(this);
		button3.addActionListener(this);

		frame.getContentPane().add(panel1);
		frame.getContentPane().add(panel2);
		// frame.getContentPane().add(label_2);

		// frame.setBackground(new Color(240, 56, 64));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	public static void main(String[] args) throws IOException {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Image myimage = new Image();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});

		System.out.println("EOF");

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == this.button1) {
			label_2.setText(("Button 1 wurde betätigt"));
		} else if (e.getSource() == this.button2) {
			label_2.setText("Button 2 wurde betätigt");
		} else if (e.getSource() == this.button3) {
			label_2.setText(("Button 3 wurde betätigt"));
		}
	}

}
