import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.BevelBorder;
import javax.swing.border.LineBorder;
import javax.swing.JToggleButton;
import javax.swing.JList;
import java.awt.Canvas;
import javax.swing.border.TitledBorder;



public class GamePanel extends JPanel {
	/**
	 * 
	 */
	private JTextField txtInput;
	private String UserName;
	private JButton btnSend;
	private Socket socket; // �������
	private JTextPane textArea;
	private JPanel panel_1;
	public Board board;
	private Graphics gc;
	private JLabel lblMouseEvent;
	private MainFrame frame;

	/**
	 * Create the frame.
	 */
	public GamePanel(MainFrame frame) {
		this.frame = frame;
		setVisible(true);
		setBounds(100, 100, 944, 630);
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(499, 248, 352, 237);
		add(scrollPane);

		textArea = new JTextPane();
		textArea.setEditable(true);
		textArea.setFont(new Font("����ü", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.setBounds(555, 495, 209, 40);
		add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setBounds(782, 495, 69, 40);
		btnSend.setFont(new Font("����", Font.PLAIN, 14));
		add(btnSend);
		
		TextSendAction action = new TextSendAction();
		btnSend.addActionListener(action);
		txtInput.addActionListener(action);
		txtInput.requestFocus();
		
		
		JButton btnNewButton = new JButton("�� ��");
		btnNewButton.setBounds(782, 545, 69, 40);
		btnNewButton.setFont(new Font("����", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(UserName, "400", "Bye");
				frame.SendObject(msg);
				System.exit(0);
			}
		});
		add(btnNewButton);

		
		board = new Board();
		board.setBounds(10, 10, 480, 480);
		board.setBorder(new LineBorder(new Color(0, 0, 0)));
		board.setBackground(Color.WHITE);
		add(board);

		
//		MyMouseEvent mouse = new MyMouseEvent();
//		panel.addMouseMotionListener(mouse);
//		panel.addMouseListener(mouse);
		
//		try {
//			
//			socket = new Socket(ip_addr, Integer.parseInt(port_no));
//
//			oos = new ObjectOutputStream(socket.getOutputStream());
//			oos.flush();
//			ois = new ObjectInputStream(socket.getInputStream());
//
//			// SendMessage("/login " + UserName);
//			ChatMsg obcm = new ChatMsg(UserName, "100", "Hello");
//			SendObject(obcm);
//
//			ListenNetwork net = new ListenNetwork();
//			net.start();
//			txtInput.requestFocus();
//
//		} catch (NumberFormatException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			AppendText("connect error");
//		}
		

	}

	// Server Message�� �����ؼ� ȭ�鿡 ǥ��
//	class ListenNetwork extends Thread {
//		public void run() {
//			panel.ResetBoard();
//			while (true) {
//				try {
//					Object obcm = null;
//					String msg = null;
//					ChatMsg cm;
//					try {
//						obcm = ois.readObject();
//					} catch (ClassNotFoundException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//						break;
//					}
//					if (obcm == null)
//						break;
//					if (obcm instanceof ChatMsg) {
//						cm = (ChatMsg) obcm;
//						msg = String.format("[%s] %s", cm.UserName, cm.data);
//					} else
//						continue;
//					switch (cm.code) {
//					case "200": // chat message
//						AppendText(msg);
//						break;
//					case "600":
//					}
//				} catch (IOException e) {
//					AppendText("ois.readObject() error");
//					try {
//						ois.close();
//						oos.close();
//						socket.close();
//
//						break;
//					} catch (Exception ee) {
//						break;
//					} // catch�� ��
//				} // �ٱ� catch����
//
//			}
//		}
//	}

	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button�� �����ų� �޽��� �Է��ϰ� Enter key ġ��
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				frame.SendMessage(msg);
				txtInput.setText(""); // �޼����� ������ ���� �޼��� ����â�� ����.
				txtInput.requestFocus(); // �޼����� ������ Ŀ���� �ٽ� �ؽ�Ʈ �ʵ�� ��ġ��Ų��
				if (msg.contains("/exit")) // ���� ó��
					System.exit(0);
			}
		}
	}
	
//	class MyMouseEvent implements MouseListener, MouseMotionListener {
//
//		@Override
//		public void mouseDragged(MouseEvent e) {
//			// TODO Auto-generated method stub
//			int x = e.getX();
//			int y = e.getY();
//			//panel.AddGameprogress(x, y, 1);
//		}
//
//		@Override
//		public void mouseMoved(MouseEvent e) {
//			// TODO Auto-generated method stub
//		}
//
//		@Override
//		public void mouseClicked(MouseEvent e) {
//			// TODO Auto-generated method stub
//			int x = e.getX();
//			int y = e.getY();
//			panel.AddGameprogress(x, y, 1);
//		}
//
//		@Override
//		public void mouseEntered(MouseEvent arg0) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void mouseExited(MouseEvent arg0) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void mousePressed(MouseEvent e) {
//			// TODO Auto-generated method stub
//			int x = e.getX();
//			int y = e.getY();
//			panel.AddGameprogress(x, y, 0);
//		}
//
//		@Override
//		public void mouseReleased(MouseEvent arg0) {
//			// TODO Auto-generated method stub
//		}
//		
//	}
//
	public void AppendText(String msg) {
		msg = msg.trim(); // �յ� blank�� \n�� �����Ѵ�.
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection(msg + "\n");
	}
//
//	// Windows ó�� message ������ ������ �κ��� NULL �� ����� ���� �Լ�
//	public byte[] MakePacket(String msg) {
//		byte[] packet = new byte[BUF_LEN];
//		byte[] bb = null;
//		int i;
//		for (i = 0; i < BUF_LEN; i++)
//			packet[i] = 0;
//		try {
//			bb = msg.getBytes("euc-kr");
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.exit(0);
//		}
//		for (i = 0; i < bb.length; i++)
//			packet[i] = bb[i];
//		return packet;
//	}
//
//
//	public void SendObject(Object ob) { // ������ �޼����� ������ �޼ҵ�
//		try {
//			oos.writeObject(ob);
//		} catch (IOException e) {
//			// textArea.append("�޼��� �۽� ����!!\n");
//			AppendText("SendObject Error");
//		}
//	}
}
