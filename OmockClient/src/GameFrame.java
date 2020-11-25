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
import javax.swing.JProgressBar;



public class GameFrame extends JFrame{

	private JPanel contentPane;
	private JTextField txtInput;
	private JButton btnSend;
	private Socket socket; // 연결소켓
	private JTextPane textArea;
	private JPanel panel_1;
	public Board board;
	private Graphics gc;
	private JLabel lblMouseEvent;
	private MainFrame frame;
	private Image panelImage = null;
	private Graphics gc2 = null;
	public int roomnumber;
	private String role;
	public int stonecolor;
	public int turn;
	public JLabel player1label,player2label;
	private JButton btnNewButton_1;
	private JButton btnNewButton_2;

	public GameFrame(MainFrame frame, int roomnum, String role) {
		this.frame = frame;
		this.roomnumber = roomnum;
		this.role = role;
		
		setTitle(String.format("%s님의 GameFrame\t%150d번방",frame.UserName ,roomnum));
		setVisible(true);
		setBounds(100, 100, 944, 630);
		
		contentPane = new JPanel() {
			public void paintComponent(Graphics g) {
            ImageIcon backgroundimage = new ImageIcon("src/background1.png");
			g.drawImage(backgroundimage.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
            setOpaque(false); //그림을 표시하게 설정,투명하게 조절
            super.paintComponent(g);
			}
        };
        setContentPane(contentPane);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(499, 248, 352, 237);
		contentPane.add(scrollPane);

		textArea = new JTextPane();
		textArea.setEditable(true);
		textArea.setFont(new Font("굴림체", Font.PLAIN, 14));
		scrollPane.setViewportView(textArea);

		txtInput = new JTextField();
		txtInput.setBounds(555, 495, 209, 40);
		contentPane.add(txtInput);
		txtInput.setColumns(10);

		btnSend = new JButton("Send");
		btnSend.setBounds(782, 495, 69, 40);
		btnSend.setFont(new Font("굴림", Font.PLAIN, 14));
		contentPane.add(btnSend);
		
		TextSendAction action = new TextSendAction();
		btnSend.addActionListener(action);
		txtInput.addActionListener(action);
		txtInput.requestFocus();
		
		
		JButton btnNewButton = new JButton("종 료");
		btnNewButton.setBounds(782, 545, 69, 40);
		btnNewButton.setFont(new Font("굴림", Font.PLAIN, 14));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ChatMsg msg = new ChatMsg(frame.UserName, "400", "Bye");
				frame.SendObject(msg);
				System.exit(0);
			}
		});
		contentPane.add(btnNewButton);

		
		board = new Board(this);
		board.setBounds(10, 10, 480, 480);
		board.setBorder(new LineBorder(new Color(0, 0, 0)));
		board.setBackground(Color.WHITE);
		MyMouseEvent mouse = new MyMouseEvent();
		if(role.matches("player1")) {
			stonecolor = COLOR.BLACK;
			board.addMouseMotionListener(mouse);
			board.addMouseListener(mouse);
		}
		else if(role.matches("player2")) {
			stonecolor = COLOR.WHITE;
			board.addMouseMotionListener(mouse);
			board.addMouseListener(mouse);			
		}
		
		contentPane.add(board);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(Color.WHITE);
		panel_2.setBounds(686, 169, 165, 69);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		player2label = new JLabel("");
		player2label.setBounds(0, 32, 165, 14);
		player2label.setHorizontalAlignment(SwingConstants.CENTER);
		player2label.setFont(new Font("나눔고딕 ExtraBold", Font.PLAIN, 12));
		panel_2.add(player2label);
		
		JLabel lblNewLabel_2 = new JLabel("Player2");
		lblNewLabel_2.setBackground(new Color(245, 222, 179));
		lblNewLabel_2.setBounds(0, 0, 165, 18);
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 15));
		panel_2.add(lblNewLabel_2);
		
		JPanel panel_2_1 = new JPanel();
		panel_2_1.setBackground(Color.WHITE);
		panel_2_1.setBounds(499, 169, 165, 69);
		contentPane.add(panel_2_1);
		panel_2_1.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Player1");
		lblNewLabel.setBackground(new Color(238, 232, 170));
		lblNewLabel.setFont(new Font("나눔고딕 ExtraBold", Font.BOLD, 15));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(0, 0, 165, 15);
		panel_2_1.add(lblNewLabel);
		
		player1label = new JLabel("");
		player1label.setHorizontalAlignment(SwingConstants.CENTER);
		player1label.setFont(new Font("나눔고딕 ExtraBold", Font.PLAIN, 12));
		player1label.setBounds(0, 32, 165, 15);
		panel_2_1.add(player1label);
		
		btnNewButton_1 = new JButton("무르기");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton_1.setBounds(50, 528, 162, 40);
		contentPane.add(btnNewButton_1);
		
		btnNewButton_2 = new JButton("무승부 신청");
		btnNewButton_2.setBounds(284, 528, 165, 40);
		contentPane.add(btnNewButton_2);
		
	}
	

	class TextSendAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Send button을 누르거나 메시지 입력하고 Enter key 치면
			if (e.getSource() == btnSend || e.getSource() == txtInput) {
				String msg = null;
				// msg = String.format("[%s] %s\n", UserName, txtInput.getText());
				msg = txtInput.getText();
				frame.SendMessage(msg);
				txtInput.setText(""); // 메세지를 보내고 나면 메세지 쓰는창을 비운다.
				txtInput.requestFocus(); // 메세지를 보내고 커서를 다시 텍스트 필드로 위치시킨다
				if (msg.contains("/exit")) // 종료 처리
					System.exit(0);
			}
		}
	}
	
	class MyMouseEvent implements MouseListener, MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			// TODO Auto-generated method stub
			if(turn==1) {
				int x = (int)Math.round(e.getX()/25);
				int y = (int)Math.round(e.getY()/25);
				if(board.check(x, y)) {
					board.movex = x;
					board.movey = y;
					board.repaint();
				}
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if(turn==1) {
				int x = (int)Math.round(e.getX()/25);
				int y = (int)Math.round(e.getY()/25);
				if(board.check(x, y)) {
					GameMsg gm = new GameMsg(frame.UserName,"302",roomnumber, x, y, stonecolor);
					frame.SendObject(gm);
					turn = 0;
				}
			}
			else {
				AppendText("당신의 차례가 아닙니다.");
			}
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
		}
		
	}

	public void AppendText(String msg) {
		msg = msg.trim(); // 앞뒤 blank와 \n을 제거한다.
		int len = textArea.getDocument().getLength();
		textArea.setCaretPosition(len);
		textArea.replaceSelection(msg + "\n");
	}
}
