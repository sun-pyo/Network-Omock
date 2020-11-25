import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CreateGameRoom extends JFrame {
	private JPanel contentPane;
	public JTextField textField;
	private JTextField textField_1;
	public JButton btnNewButton;
	private MainFrame frame;
	
	public CreateGameRoom() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 374, 249);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("°üÀü Çã¿ë");
		rdbtnNewRadioButton_1.setFont(new Font("³ª´®°íµñ", Font.BOLD, 14));
		rdbtnNewRadioButton_1.setBounds(23, 133, 113, 39);
		contentPane.add(rdbtnNewRadioButton_1);
		
		textField = new JTextField();
		textField.setBounds(95, 66, 212, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("¹æÁ¦¸ñ");
		lblNewLabel.setFont(new Font("³ª´®°íµñ", Font.BOLD, 14));
		lblNewLabel.setBounds(23, 60, 50, 30);
		contentPane.add(lblNewLabel);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(95, 106, 212, 21);
		contentPane.add(textField_1);
		
		JLabel lblNewLabel_1 = new JLabel("½Ã°£ ¼³Á¤");
		lblNewLabel_1.setFont(new Font("³ª´®°íµñ", Font.BOLD, 14));
		lblNewLabel_1.setBounds(23, 100, 60, 30);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("¹æ ¸¸µé±â");
		lblNewLabel_2.setFont(new Font("³ª´®°íµñ ExtraBold", Font.PLAIN, 17));
		lblNewLabel_2.setBounds(149, 10, 130, 30);
		contentPane.add(lblNewLabel_2);
		
		btnNewButton = new JButton("»ý¼ºÇÏ±â");
		
		
		btnNewButton.setBounds(143, 179, 91, 23);
		contentPane.add(btnNewButton);
		setVisible(true);
	}
}
