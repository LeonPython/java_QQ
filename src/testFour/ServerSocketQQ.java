package testFour;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JOptionPane;

public class ServerSocketQQ implements Runnable{
	private ServerSocket sst = null;
	private Socket st = null;
	private static int port = 10108;
	private boolean flag = true;
	
	public void setFlag(){
		if(sst != null)
			try {
				sst.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		flag = false;
	}
	
	public static int getPort(){
		return port;
	}
	
	public ServerSocketQQ(){
		//���������
		try {
			sst = new ServerSocket(port);
		} catch (IOException e) {
			port = -1;
			JOptionPane.showMessageDialog(null, "ServerSocket�˿ڰ󶨴���", "�󶨴���", JOptionPane.OK_CANCEL_OPTION);
		}
		
	}
	
	public void run(){
		if(sst == null)  return ;
		while(true){
			try{
				//���������ܵ��˷����׽��ֵ����ӡ��˷����ڽ�������֮ǰһֱ������ �������׽��� 
				st = sst.accept();
				//�õ��ͻ��˴����������
				new Thread(new Upload(st)).start();//����һ���µ��߳̽��д���
		   }catch(IOException e){
			   e.printStackTrace();
		   }
		}
	 }
	
}
