package testFour;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.filechooser.FileFilter;



public class QQFrame extends Frame{
	 private TextArea taSend = new TextArea();
	 private JTextPane taReceive = new JTextPane();
	 private JScrollPane p = new JScrollPane(taReceive);
	 private JPanel pSend = new JPanel();
	 private JPanel pReceive = new JPanel();
	 private Label laSend = new Label("���Ͷ�.....");
	 private Label laReceive = new Label("���ն�.....");
	 private JButton FileBtn = new JButton("�����ļ�");
	 private JButton PicuterBtn = new JButton("����ͼƬ");
	 private InetAddress sendIAD = null;//��ǰ��������Ӧ�ĺ������ڻ�����IP����
	 private String QQname = null;//�öԻ�������Ӧ�ĺ��ѵ�����
	 private Socket st =null;
	 private String text;
	 private DatagramSocket ds = null;
	
	 public QQFrame(String name, byte[] ipBuf) throws RuntimeException{
		 try {
			sendIAD = InetAddress.getByAddress(ipBuf);
		 } catch (UnknownHostException e3) {
			throw new RuntimeException("IP���󣨲����ڻ��޷����ӣ���");
		 }
		 
		 ds = QQReceive.getUdpSocket();
		 if(ds == null)  throw new RuntimeException("udp Socket����");
		 
		 QQname = name;
		 text = "";
		 setSize(600, 600);
		 setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		 pSend.setLayout(new FlowLayout(FlowLayout.LEFT));
		 pSend.add(laSend);
		 pSend.add(FileBtn);
		 pSend.add(PicuterBtn);
		 pReceive.setLayout(new FlowLayout(FlowLayout.LEFT));
		 pReceive.add(laReceive);
		 
		 taReceive.setForeground(new Color(255, 0, 255));
		 add(pReceive);
		 add(p);
		 add(pSend);
		 add(taSend);
		 setTitle("�ҵĺ��� " + QQname);
		 
		 
		 taSend.setPreferredSize(new Dimension(0, 200));
		 taReceive.setPreferredSize(new Dimension(0, 400));
		 
		 taSend.setFont(new Font("����", Font.PLAIN, 20));
		 taReceive.setFont(new Font("����", Font.PLAIN, 25));
		 
		 taReceive.setEditable(false);//���ܽ����ı��ı༭�����ǿ��Խ��з���
		 
		 taSend.addKeyListener(new KeyAdapter() {
			
			 public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					 text = taSend.getText();
					 if(text == null) return;
					 text += "\n\n";
					 byte[] bt = text.getBytes();
					 DatagramPacket dp = null;
					 try {
						 //��ָ����ip�Ͷ˿ڷ�������~��
						 //��˵��һ��������˭���͹����ģ�
						 byte[] ip = InetAddress.getLocalHost().getHostAddress().getBytes();
						 dp = new DatagramPacket(ip, ip.length, sendIAD, QQReceive.getPort());
						 ds.send(dp);
						 try {
							Thread.sleep(100);
						 } catch (InterruptedException e1) {
						 }
						 
						 dp = new DatagramPacket("PARAGRAPH".getBytes(), "PARAGRAPH".getBytes().length, sendIAD, QQReceive.getPort());
						 ds.send(dp);
						 
						 try {
								Thread.sleep(100);
						 } catch (InterruptedException e1) {
							 
						 }
						 
						 dp = new DatagramPacket(bt, bt.length, sendIAD, QQReceive.getPort());
						 ds.send(dp);
					 } catch (IOException e1) {
						e1.printStackTrace();
					 }
					 
					 synchronized(QQ.class){//���Ͷ�����մ����������ʱ Ҫ �� ���ն�����մ����������ʱͬ����
						 byte[] x = null;
						 try {
							x = new String(InetAddress.getLocalHost().getHostName() + " : ").getBytes();
						 } catch (UnknownHostException e1) {
							e1.printStackTrace();
						 }
						 QQ.setTextPane(taReceive, x, x.length, QQ.PARAGRAPH, QQ.SEND);
						 x = text.getBytes();
						 QQ.setTextPane(taReceive, x, x.length, QQ.PARAGRAPH, QQ.SEND*3);
						 taSend.requestFocus();
					 }
					 
					 taSend.setText("");//���Ͷ����
					 e.consume();//��������س��ַ����������ʾ��
					 return ;
				}
			}
			 
		 });
		 
		 addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				Map<String, QQFrame>mp = QQDialog.getMap();
				mp.remove(sendIAD.getHostAddress());
				dispose();
			}
			 
		 });
		 
		 FileBtn.addMouseListener(new MouseAdapter() {//�ļ�����
				public void mouseClicked(MouseEvent e) {
					 JFileChooser jfc = new JFileChooser();
					 jfc.showOpenDialog(null);
					 File fl = jfc.getSelectedFile();
					 if(fl == null) return ;
					 try {
						st =  new Socket();//�������ӶԷ�
						st.connect(new InetSocketAddress(sendIAD, ServerSocketQQ.getPort()), 1000);
					 } catch (IOException e2) {
						st = null;
						JOptionPane.showMessageDialog(null, "����ʱ�����Ӵ���", "ServerSocket", JOptionPane.OK_CANCEL_OPTION);
					 }
					 if(st != null){
						try {
							byte[] bt = new byte[1024];
							InputStream is = st.getInputStream(); 
							OutputStream os = st.getOutputStream();
						    //��˵��һ����˭���͹����ģ�
							byte[] ip = InetAddress.getLocalHost().getHostAddress().getBytes();
							os.write(ip);
							os.flush();
							
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
							 
							}
							
							//��Է����ȷ����ļ����� Ȼ�����ļ����ݣ�
							os.write(fl.getName().getBytes());
							os.flush();
							
							try {
								Thread.sleep(100);
							} catch (InterruptedException e1) {
							 
							}
							
							int len;
							InputStream fis = new FileInputStream(fl);
							while( (len = fis.read(bt)) != -1){
								os.write(bt, 0, len);
								os.flush();
							}
							//st.shutdownOutput();//����������������һ�£�ʹ�����֪���ͻ�������Ѿ������ˣ�
							st.close();
							bt = new String(Calendar.getInstance().getTime().toString() + ":�ļ��Ѵ��䣡").getBytes();
							QQ.setTextPane(taReceive, bt, bt.length, QQ.FILE, QQ.FILEX);
						
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					 }
				}
		 });
		 
		 PicuterBtn.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					JFileChooser jfc = new JFileChooser();
					jfc.setFileFilter(new PicuterFilter());//���õ�ǰ���ļ���������
					jfc.setAcceptAllFileFilterUsed(false);//���������ļ���������ʹ�ã�
					//jfc.addChoosableFileFilter(new Filter());
					jfc.showOpenDialog(null);
					
				   //���������������淽ʽ���� ����Iterator<ImageReader> itImage�Ƿ���
				   //�ɹ��ķ���һ��ImageReader����ȷ�ϸ����ļ��Ƿ���һ��ͼƬ�ļ���
				   //��ImageReader���е�getFormatName�����õ��ļ��ĸ�ʽ��
				   //ͨ��������ͨ��ImageIcon��byte[]���캯������ImageIcon����
				   //���ͼƬ��ʾ������ϣ�
					File fl = jfc.getSelectedFile();
					if(fl == null) return ;
					try{
							 InputStream is = new FileInputStream(fl);
							 ImageInputStream iis = ImageIO.createImageInputStream(is);
							 Iterator<ImageReader> itImage = ImageIO.getImageReaders(iis);
							 if(itImage.hasNext()){
								  ImageReader reader = itImage.next();
								  byte[] imageByte = new byte[1024*64];
								  int len = iis.read(imageByte);
								  if(len > 64 * 1000){
									  JOptionPane.showMessageDialog(new Frame(), "ͼƬ����������ļ����䣡");
									  return ;
								  }
								  DatagramPacket dp = null;
								  //��˵��һ��������˭���͹����ģ�
								  byte[] ip = InetAddress.getLocalHost().getHostAddress().getBytes();
								  dp = new DatagramPacket(ip, ip.length, sendIAD, QQReceive.getPort());
								  ds.send(dp);
								  
								  try {
										Thread.sleep(100);
								  } catch (InterruptedException e1) {
								  }
								  
								  dp = new DatagramPacket("PICUTER".getBytes(), "PICUTER".getBytes().length, sendIAD, QQReceive.getPort());
								  ds.send(dp);
								  
								  try {
										Thread.sleep(100);
								  } catch (InterruptedException e1) {
								  }
								  
								  dp = new DatagramPacket(imageByte, len, sendIAD, QQReceive.getPort());
								  ds.send(dp);
								  synchronized(QQ.class){
									  byte[] name = null;
									  name = new String(InetAddress.getLocalHost().getHostName() + " : ").getBytes();
									  QQ.setTextPane(taReceive, name, name.length, QQ.PARAGRAPH, QQ.SEND);
									  QQ.setTextPane(taReceive, imageByte, len, QQ.PICUTER, 0);
								  }
							 }
							 else throw new NoPicuterException("����һ��ͼƬ��");
					}catch(IOException ex){
						ex.printStackTrace();
					}
				}
		 });
		 
		 setVisible(true);
	 }
	 
	 public void setSendIAD(String ip) throws RuntimeException{
		 String[] ipStr = ip.split("\\.");//���ַ����е����ַ���
         byte[] ipBuf = new byte[4];//�洢IP��byte����
         for(int i = 0; i < 4; i++){
             ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
         }
         try {
 			sendIAD = InetAddress.getByAddress(ipBuf);
 		 } catch (UnknownHostException e3) {
 			throw new RuntimeException("IP���󣨲����ڻ��޷����ӣ���");
 		 }
	 }
	 
	 public DatagramSocket getDs(){
		 return ds;
	 }
	 
	 public JTextPane getReceive(){
		 return taReceive;
	 }
	 
}

class PicuterFilter extends FileFilter {
	 
	public boolean accept(File file){
		   return(file.isDirectory() || file.getName().endsWith(".gif") 
				  || file.getName().endsWith(".png") || file.getName().endsWith(".bmp")
				  || file.getName().endsWith(".jpg") );
		   /* ����Ҫ��ʾ���ļ����� */
		   /*
		    *   File.isDirectory()���Դ˳���·������ʾ���ļ��Ƿ���һ��Ŀ¼
		   */
	  }
	  
	  public String getDescription() {
		  return("Picuter Files(*.gif, *.png, *.jpg, *.bmp)");                  //������ʾ�ļ����͵�����
	  }
}

class NoPicuterException extends IOException{
	public NoPicuterException(String x){
		super(x);
	}
}
