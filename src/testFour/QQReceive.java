package testFour;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

public class QQReceive implements Runnable{
	private QQFrame fm = null;
	private static DatagramSocket ds = null;
	private static int port = 10111;
	private boolean flag = true;
	
	public void setFlag(){
		if(ds != null)
			ds.close();
		flag = false;
	}
	public QQReceive(){
		try {
			//����udpͨ�ŷ�ʽ
			ds = new DatagramSocket(port);
		 } catch (SocketException e1) {
			 port = -1;
			 JOptionPane.showMessageDialog(null, "DatagramSocket�˿ڰ󶨴���", "�󶨴���", JOptionPane.OK_CANCEL_OPTION);
		}
	}
	
	public static DatagramSocket getUdpSocket(){
		return ds;
	} 
	
	public static int getPort(){
		return port;
	}
	
	public DatagramSocket getDS(){
		return ds;
	}
	
	public void run(){
		if(ds == null) return ;
		byte[] bt = new byte[10024*10]; 
		DatagramPacket dp = new DatagramPacket(bt, bt.length); 
		String name = null;
		while(flag){
			int flagx = 0;
			try {
					//ds.setReceiveBufferSize(size);
					ds.receive(dp);
					String ip = new String(dp.getData(), 0, dp.getLength());
					ds.receive(dp);
					String tag = new String(dp.getData(), 0, dp.getLength());
					
					ds.receive(dp);
					Map<String, QQFrame>mp = QQDialog.getMap();
					Set<String>  set = QQDialog.getSet();
					fm = mp.get(ip);
					name = null;
					if( set.contains(ip) ){
						Map<String, String> nameToIP = QQDialog.getNameToIP();
						for(String ss : nameToIP.keySet())
				        	 if( ip.equals(nameToIP.get(ss)) ){
				        		   name = ss;
				        		   break;
				        	 }
					}
					
					if(fm == null && name != null){//������ڸú��ѣ����ǲ����ڶ�Ӧ�ĶԻ�����
						 //�Զ������Ի�����
						 String[] ipStr = ip.split("\\.");//���ַ����е����ַ���
				         byte[] ipBuf = new byte[4];//�洢IP��byte����
				         for(int i = 0; i < 4; i++){
				             ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
				         }
				         
						 fm = new QQFrame(name, ipBuf);
						 mp.put(ip, fm);
					}else if(fm != null)
						 fm.requestFocus();
					
					if( tag.equals("FILE") )
						flagx = QQ.FILE;
					else if( tag.equals("PICUTER"))
						flagx = QQ.PICUTER;
					else if( tag.equals("PARAGRAPH"))
						flagx = QQ.PARAGRAPH;
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			synchronized(QQ.class){
				if(fm == null) continue;
				byte[] x = new String(name + " : ").getBytes();
				QQ.setTextPane(fm.getReceive(), x, x.length, QQ.PARAGRAPH, QQ.RECEIVE);
				QQ.setTextPane(fm.getReceive(), dp.getData(), dp.getLength(), flagx, QQ.RECEIVE*3);
			}
		}
	}
}
