package com.finalyearproject.gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import com.finalyearproject.client.Client;
import com.finalyearproject.dao.ServerDAO;
import com.finalyearproject.ecc.core.ECC;
import com.finalyearproject.ecc.core.EllipticCurve;
import com.finalyearproject.ecc.core.KeyPair;
import com.finalyearproject.otp.LamportOTP;

import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class Main extends JFrame {

	public JPanel contentPane;
	public JTextField un_login1;
	public JTextField un_reg;
	public JPanel registerPanel = new JPanel();
	public JPanel loginPanel = new JPanel();
	public ServerDAO serverdao = new ServerDAO();
	public JTextField un_login;
	public JPasswordField passwordField;
	public JPasswordField pwd_login;
	public JPasswordField pwd_login1;
	public JLabel welcomemsg = new JLabel("You haven't logged in");
	public JPanel emptyPanel1 = new JPanel();
	public JPanel emptyPanel = new JPanel();
	public JPanel registerSuccess = new JPanel();
	public JPanel registrationFailed = new JPanel();
	public JPanel menuPanel = new JPanel();
	public JPanel homePanel = new JPanel();
	public JPanel welcomemsgpanel = new JPanel();
	public JLabel loginerror1 = new JLabel("");
	public JTextField serverIP;
	public JTextField serverPort;
	public JPanel clientStartupPanel = new JPanel();
	public JLabel clientstartmsg = new JLabel("");
	public Main frm;
	public Client client;
	public JButton startButton = new JButton("START CLIENT");
	public JPanel communicationPanel = new JPanel();
	public JPanel eccKeysPanel = new JPanel();
	public JTextField otp;
	public JTextArea currentMessage = new JTextArea();
	public JTextArea previousMessage = new JTextArea();
	public JTextArea replyMessage = new JTextArea();
	public JLabel Serverstatus = new JLabel("Not connected to any server yet");
	public JLabel serverdetails = new JLabel("");
	public JButton btnSendMessage = new JButton("Send Message");
	public JButton btnDecrypt = new JButton("Decrypt");

	public BigInteger b1 = null;
	public BigInteger b2 = null;
	public int factor;

	public ECC ecc = null;
	public LamportOTP lamportOTP = null;

	public KeyPair keyPair = null;

	
	public JTextArea xPub = new JTextArea();

	public JTextArea yPub = new JTextArea();

	public JTextArea Priv = new JTextArea();

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		setResizable(false);
		frm = this;
		setTitle("Project - Client Side");
		setBackground(Color.BLACK);
		setForeground(Color.GRAY);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 870, 620);
		contentPane = new JPanel();
		contentPane.setForeground(Color.GRAY);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblIntegratingLamportsOtp = new JLabel("Integrating Lamports OTP scheme with Elliptic Curve Cryptography");
		lblIntegratingLamportsOtp.setFont(new Font("Verdana", Font.BOLD, 16));
		lblIntegratingLamportsOtp.setBounds(71, 11, 686, 29);
		contentPane.add(lblIntegratingLamportsOtp);

		JLabel lbl = new JLabel("PES Institute of Technology");
		lbl.setFont(new Font("Verdana", Font.BOLD, 12));
		lbl.setBounds(270, 51, 377, 14);
		contentPane.add(lbl);

		JLabel lblFinalYearProject = new JLabel("Final year project");
		lblFinalYearProject.setFont(new Font("Verdana", Font.BOLD, 12));
		lblFinalYearProject.setBounds(301, 71, 377, 14);
		contentPane.add(lblFinalYearProject);

		JPanel leftPanel = new JPanel();
		leftPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		leftPanel.setBackground(new Color(255, 255, 255));
		leftPanel.setBounds(71, 128, 241, 279);
		contentPane.add(leftPanel);
		leftPanel.setLayout(new CardLayout(0, 0));

		emptyPanel1.setBackground(new Color(255, 255, 255));
		leftPanel.add(emptyPanel1);
		emptyPanel1.setLayout(null);

		JLabel lblAlreadyRegistered = new JLabel("Already Registered? ");
		lblAlreadyRegistered.setBounds(22, 27, 126, 14);
		emptyPanel1.add(lblAlreadyRegistered);

		JButton btnLoginHere = new JButton("Login");
		btnLoginHere.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				emptyPanel1.setVisible(false);
				loginPanel.setVisible(true);
			}
		});
		btnLoginHere.setBounds(22, 52, 111, 23);
		emptyPanel1.add(btnLoginHere);
		loginPanel.setBackground(new Color(255, 255, 255));

		leftPanel.add(loginPanel, "name");
		loginPanel.setLayout(null);
		final JLabel loginerror = new JLabel("");

		JButton btnNewButton = new JButton("LOGIN");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String un = un_login1.getText();
					String pwd = pwd_login1.getText();
					if (un.trim().equals("") || pwd.trim().equals("")) {
						loginerror.setText("Invalid Credentials");

					} else {
						if (serverdao.isValidUser(un, pwd)) {
							welcomemsg.setText("Welcome " + un);
							hideAllLeftPanels();
							menuPanel.setVisible(true);
							hideAllRightPanels();
							homePanel.setVisible(true);

						} else {
							loginerror.setText("Invalid Credentials");
						}
					}
				} catch (Exception e2) {
					loginerror.setText("Something went wrong. Please try again later.");
				}
			}
		});
		btnNewButton.setFont(new Font("Verdana", Font.BOLD, 14));
		btnNewButton.setBounds(10, 207, 219, 39);
		loginPanel.add(btnNewButton);

		un_login1 = new JTextField();
		un_login1.setBounds(10, 47, 219, 39);
		loginPanel.add(un_login1);
		un_login1.setColumns(10);

		JLabel lblNewLabel = new JLabel("Username");
		lblNewLabel.setBounds(10, 28, 79, 14);
		loginPanel.add(lblNewLabel);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(10, 108, 79, 14);
		loginPanel.add(lblPassword);

		pwd_login1 = new JPasswordField();
		pwd_login1.setBounds(10, 133, 219, 39);
		loginPanel.add(pwd_login1);

		loginerror.setForeground(new Color(153, 102, 0));
		loginerror.setBounds(10, 182, 219, 14);
		loginPanel.add(loginerror);

		menuPanel.setBackground(new Color(255, 255, 255));
		leftPanel.add(menuPanel);
		menuPanel.setLayout(null);

		JButton btnNewButton_1 = new JButton("CLIENT START UP");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hideAllRightPanels();
				clientStartupPanel.setVisible(true);

			}
		});
		btnNewButton_1.setBounds(10, 22, 219, 50);
		menuPanel.add(btnNewButton_1);

		JButton btnOtps = new JButton("COMMUNICATION");
		btnOtps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				hideAllRightPanels();
				communicationPanel.setVisible(true);
			}
		});
		btnOtps.setBounds(10, 83, 219, 50);
		menuPanel.add(btnOtps);

		JButton btnECCKeys = new JButton("ECC KEYS");
		btnECCKeys.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				hideAllRightPanels();
				eccKeysPanel.setVisible(true);
			}
		});
		btnECCKeys.setBounds(10, 144, 219, 50);
		menuPanel.add(btnECCKeys);

		JPanel rightPanel = new JPanel();
		rightPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		rightPanel.setBackground(new Color(255, 255, 255));
		rightPanel.setBounds(339, 128, 418, 329);
		contentPane.add(rightPanel);
		rightPanel.setLayout(new CardLayout(0, 0));

		emptyPanel.setBackground(new Color(255, 255, 255));
		rightPanel.add(emptyPanel);
		emptyPanel.setLayout(null);

		JLabel lblNewUser = new JLabel("New User?");
		lblNewUser.setBounds(30, 24, 77, 14);
		emptyPanel.add(lblNewUser);

		JButton btnRegister_1 = new JButton("Register here");
		btnRegister_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				emptyPanel.setVisible(false);
				registerPanel.setVisible(true);
			}
		});
		btnRegister_1.setBounds(30, 49, 112, 23);
		emptyPanel.add(btnRegister_1);

		rightPanel.add(communicationPanel);
		communicationPanel.setLayout(null);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(25, 151, 361, 45);
		communicationPanel.add(scrollPane_1);
		previousMessage.setEditable(false);
		previousMessage.setLineWrap(true);
		scrollPane_1.setViewportView(previousMessage);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(25, 230, 361, 45);
		communicationPanel.add(scrollPane_2);
		replyMessage.setEditable(false);
		replyMessage.setLineWrap(true);
		scrollPane_2.setViewportView(replyMessage);

		JLabel lblPreviousMessage = new JLabel("Previous message");
		lblPreviousMessage.setBounds(25, 127, 132, 14);
		communicationPanel.add(lblPreviousMessage);

		JLabel lblReply = new JLabel("Reply from server");
		lblReply.setBounds(25, 211, 180, 14);
		communicationPanel.add(lblReply);

		JLabel lblOtp = new JLabel("OTP :");
		lblOtp.setBounds(289, 127, 46, 14);
		communicationPanel.add(lblOtp);
		btnDecrypt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					byte[] ptBytes = ECC.decrypt(replyMessage.getText().getBytes("ISO-8859-1"),
							keyPair.getPrivateKey());
					replyMessage.setText(new String(ptBytes));
				} catch (Exception e4) {
					e4.printStackTrace();
				}
			}
		});

		btnDecrypt.setEnabled(false);
		btnDecrypt.setBounds(297, 286, 89, 23);
		communicationPanel.add(btnDecrypt);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(25, 49, 361, 38);
		communicationPanel.add(scrollPane);
		currentMessage.setEnabled(false);
		currentMessage.setLineWrap(true);
		scrollPane.setViewportView(currentMessage);
		btnSendMessage.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String currentmsg = currentMessage.getText();
				if (currentmsg != null || currentmsg.length() != 0) {
					try {

						if (ecc != null) {
							double newotp = lamportOTP.function();
							String currentmsg2 = String.valueOf(newotp) + "##$$" + currentmsg;
							//currentmsg = String.valueOf(newotp) + "##$$" + currentmsg;
							byte[] ctBytes = ecc.encrypt(currentmsg2.getBytes(), keyPair.getPublicKey());
							String ct = new String(ctBytes, "ISO-8859-1");
							otp.setText(String.valueOf(newotp));
							client.writeData(ct);

						} else {
							client.writeData(currentmsg);
						}						
						
						previousMessage.setText(currentmsg);
						currentMessage.setText("");
						currentMessage.setEnabled(false);
						btnSendMessage.setEnabled(false);
						replyMessage.setText("");
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				} else {

				}
			}
		});

		btnSendMessage.setEnabled(false);
		btnSendMessage.setBounds(25, 93, 148, 23);
		communicationPanel.add(btnSendMessage);

		otp = new JTextField();
		otp.setEditable(false);
		otp.setBounds(329, 124, 57, 20);
		communicationPanel.add(otp);
		otp.setColumns(10);

		JLabel lblNewMessage = new JLabel("New Message");
		lblNewMessage.setBounds(25, 31, 148, 14);
		communicationPanel.add(lblNewMessage);

		Serverstatus.setFont(new Font("Tahoma", Font.BOLD, 12));
		Serverstatus.setBounds(25, 6, 361, 14);
		communicationPanel.add(Serverstatus);

		rightPanel.add(eccKeysPanel);
		eccKeysPanel.setLayout(null);
		
		JLabel lblPublicKey = new JLabel("Public Key");
		lblPublicKey.setBounds(21, 40, 123, 14);
		eccKeysPanel.add(lblPublicKey);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(21, 66, 365, 40);
		eccKeysPanel.add(scrollPane_3);
		xPub.setEditable(false);
		xPub.setLineWrap(true);
		scrollPane_3.setViewportView(xPub);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(21, 117, 365, 40);
		eccKeysPanel.add(scrollPane_4);
		yPub.setEditable(false);
		yPub.setLineWrap(true);
		scrollPane_4.setViewportView(yPub);
		
		JLabel lblPrivateKey = new JLabel("Private Key");
		lblPrivateKey.setBounds(21, 180, 123, 14);
		eccKeysPanel.add(lblPrivateKey);
		
		JScrollPane scrollPane_5 = new JScrollPane();
		scrollPane_5.setBounds(21, 205, 365, 40);
		eccKeysPanel.add(scrollPane_5);
		Priv.setEditable(false);
		Priv.setLineWrap(true);
		scrollPane_5.setViewportView(Priv);
		registerPanel.setBackground(new Color(255, 255, 255));

		rightPanel.add(registerPanel);
		registerPanel.setLayout(null);

		JLabel label = new JLabel("Username");
		label.setBounds(104, 22, 79, 14);
		registerPanel.add(label);

		un_reg = new JTextField();
		un_reg.setColumns(10);
		un_reg.setBounds(104, 47, 219, 39);
		registerPanel.add(un_reg);

		JLabel label_1 = new JLabel("Password");
		label_1.setBounds(104, 103, 79, 14);
		registerPanel.add(label_1);

		JButton btnRegister = new JButton("REGISTER");
		final JLabel regerrormsg = new JLabel("");
		final JLabel validationfail = new JLabel("");
		final JPasswordField pwd_reg = new JPasswordField();

		btnRegister.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String un = un_reg.getText();
				String pwd = pwd_reg.getText();
				if (un.trim().equals("") || pwd.trim().equals("")) {
					validationfail.setText("Both are mandatory fields");
				} else {
					try {
						serverdao.register(un, pwd);
						registerPanel.setVisible(false);
						registerSuccess.setVisible(true);

					} catch (Exception e1) {
						e1.printStackTrace();
						regerrormsg.setText(e1.getMessage());
						regerrormsg.setVisible(true);
						registrationFailed.setVisible(true);

						registerPanel.setVisible(false);

					}
				}

			}
		});
		btnRegister.setFont(new Font("Verdana", Font.BOLD, 14));
		btnRegister.setBounds(104, 209, 219, 39);
		registerPanel.add(btnRegister);

		validationfail.setForeground(new Color(51, 102, 0));
		validationfail.setBounds(104, 184, 219, 14);
		registerPanel.add(validationfail);

		pwd_reg.setBounds(104, 128, 219, 39);
		registerPanel.add(pwd_reg);

		rightPanel.add(clientStartupPanel);
		clientStartupPanel.setLayout(null);

		JLabel lblServerIpAdress = new JLabel("Server IP Adress");
		lblServerIpAdress.setBounds(49, 35, 150, 14);
		clientStartupPanel.add(lblServerIpAdress);

		JLabel lblServerPortNumber = new JLabel("Server Port Number");
		lblServerPortNumber.setBounds(49, 70, 150, 14);
		clientStartupPanel.add(lblServerPortNumber);

		serverIP = new JTextField();
		serverIP.setBounds(191, 29, 150, 27);
		clientStartupPanel.add(serverIP);
		serverIP.setColumns(10);

		serverPort = new JTextField();
		serverPort.setColumns(10);
		serverPort.setBounds(191, 64, 150, 27);
		clientStartupPanel.add(serverPort);

		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {
					client = new Client(frm, serverIP.getText(), Integer.parseInt(serverPort.getText()));
					serverIP.setEnabled(false);
					serverPort.setEnabled(false);
					startButton.setEnabled(false);

					clientstartmsg.setText("Trying to connect to " + serverIP.getText() + ": " + serverPort.getText());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					clientstartmsg.setText("Error while client start up : " + e.getMessage());

				}
			}
		});
		startButton.setBounds(191, 114, 150, 27);
		clientStartupPanel.add(startButton);

		clientstartmsg.setBounds(49, 184, 292, 62);
		clientStartupPanel.add(clientstartmsg);

		serverdetails.setBounds(49, 259, 292, 37);
		clientStartupPanel.add(serverdetails);
		registrationFailed.setBackground(Color.WHITE);
		rightPanel.add(registrationFailed);
		registrationFailed.setLayout(null);

		JLabel lblSomethingWentWrong = new JLabel("Something went wrong while registering the user");
		lblSomethingWentWrong.setBounds(24, 29, 331, 14);
		registrationFailed.add(lblSomethingWentWrong);

		JButton btnTryAgain = new JButton("TRY AGAIN");
		btnTryAgain.setBounds(24, 224, 219, 31);
		btnTryAgain.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				registrationFailed.setVisible(false);
				registerPanel.setVisible(true);
			}
		});
		btnTryAgain.setFont(new Font("Verdana", Font.BOLD, 14));
		registrationFailed.add(btnTryAgain);

		regerrormsg.setBounds(24, 73, 331, 124);
		registrationFailed.add(regerrormsg);

		registerSuccess.setBackground(new Color(255, 255, 255));
		rightPanel.add(registerSuccess, "name_22197669727934");
		registerSuccess.setLayout(null);

		JLabel lblRegistrationSuccessful = new JLabel("Registration Successful.");
		lblRegistrationSuccessful.setBounds(24, 29, 226, 14);
		registerSuccess.add(lblRegistrationSuccessful);

		JLabel lblPleaseLoginHere = new JLabel("Please login here");
		lblPleaseLoginHere.setBounds(24, 54, 124, 14);
		registerSuccess.add(lblPleaseLoginHere);

		un_login = new JTextField();
		un_login.setColumns(10);
		un_login.setBounds(24, 98, 219, 31);
		registerSuccess.add(un_login);

		JLabel label_2 = new JLabel("Username");
		label_2.setBounds(24, 79, 79, 14);
		registerSuccess.add(label_2);

		JLabel label_3 = new JLabel("Password");
		label_3.setBounds(24, 140, 79, 14);
		registerSuccess.add(label_3);

		JButton button = new JButton("LOGIN");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String un = un_login.getText();
					String pwd = pwd_login.getText();
					if (un.trim().equals("") || pwd.trim().equals("")) {
						loginerror1.setText("Invalid Credentials");

					} else {
						if (serverdao.isValidUser(un, pwd)) {
							welcomemsg.setText("Welcome " + un);
							hideAllLeftPanels();
							menuPanel.setVisible(true);
							hideAllRightPanels();
							homePanel.setVisible(true);

						} else {
							loginerror1.setText("Invalid Credentials");
						}
					}
				} catch (Exception e2) {
					loginerror1.setText("Something went wrong. Please try again later.");
				}
			}
		});
		button.setFont(new Font("Verdana", Font.BOLD, 14));
		button.setBounds(24, 234, 219, 31);
		registerSuccess.add(button);

		pwd_login = new JPasswordField();
		pwd_login.setBounds(24, 165, 219, 31);
		registerSuccess.add(pwd_login);

		loginerror1.setForeground(new Color(153, 102, 0));
		loginerror1.setBounds(24, 209, 219, 14);
		registerSuccess.add(loginerror1);

		homePanel.setBackground(new Color(255, 255, 255));
		rightPanel.add(homePanel, "name_24373501569303");

		welcomemsgpanel.setBackground(new Color(255, 255, 255));
		welcomemsgpanel.setBounds(534, 76, 223, 41);
		contentPane.add(welcomemsgpanel);
		welcomemsgpanel.setLayout(null);

		welcomemsg.setBounds(10, 11, 203, 14);
		welcomemsgpanel.add(welcomemsg);

		
		JLabel label_4 = new JLabel("<html>\r\nAnand Kumar<br/> 1PI12IS013 <br/>");
		label_4.setBounds(71, 498, 114, 63);
		contentPane.add(label_4);
		
		JLabel label_5 = new JLabel("<html>\r\nSumit Kumar<br/> 1PI12IS114 <br/>");
		label_5.setBounds(207, 489, 146, 82);
		contentPane.add(label_5);
		
		JLabel label_6 = new JLabel("<html><b>Developers</b></html>");
		label_6.setFont(new Font("Tahoma", Font.BOLD, 12));
		label_6.setBounds(140, 489, 146, 14);
		contentPane.add(label_6);
		
		JLabel label_7 = new JLabel("<html><b>Guide Name: </b></html>");
		label_7.setFont(new Font("Dialog", Font.BOLD, 12));
		label_7.setBounds(501, 489, 146, 15);
		contentPane.add(label_7);
		
		JLabel label_8 = new JLabel("<html>\nAsst. Prof. Raj Alandkar</html>");
		label_8.setBounds(500, 507, 223, 29);
		contentPane.add(label_8);
	}

	public void hideAllLeftPanels() {
		loginPanel.setVisible(false);
		emptyPanel1.setVisible(false);
		menuPanel.setVisible(false);
	}

	public void hideAllRightPanels() {
		registerPanel.setVisible(false);
		emptyPanel.setVisible(false);
		registrationFailed.setVisible(false);
		registerSuccess.setVisible(false);
		clientStartupPanel.setVisible(false);
		homePanel.setVisible(false);
		communicationPanel.setVisible(false);
		eccKeysPanel.setVisible(false);
	}
}
