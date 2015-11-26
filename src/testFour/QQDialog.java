package testFour;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

class MyPanel extends JPanel{
	private int componentsHeight = 0;
	
	public int getComponentsHeight() {
		return componentsHeight;
	}

	public void setComponentsHeight(int componentsHeight) {
		this.componentsHeight = componentsHeight;
	}

	public void Mylayout(){
		
		if(componentsHeight >= 430)
			getComponent(getComponentCount()-1).setPreferredSize(new Dimension(300, 0));
		else 
			getComponent(getComponentCount()-1).setPreferredSize(new Dimension(300, 430-componentsHeight));
		updateUI();
	}
	
}

class MyLabel extends JLabel{
	private String groupName;
	private Map<String, QQFrame> QQmap = QQDialog.getMap();
	private Map<String, String> nameToIP = QQDialog.getNameToIP();
	private Set<String> QQset = QQDialog.getSet();
	private JPanel friendPanel;
	public void myInit(){
		
		setOpaque(true);//�����������߽��ڵ��������ء������������ܲ�������ĳЩ���������أ��Ӷ����������������͸�ӳ���
		                //��Ϊ������mouseEntered�¼���Ӧ��mouseExited�¼���Ӧ
		setBackground(new Color(255, 255, 255));
		setPreferredSize(new Dimension(250, 50));
		addMouseListener(new MouseAdapter() {
		 	Color oldC = getBackground();
		 	public void mouseEntered(MouseEvent e) {
		 		 setBackground(new Color(0, 255, 0));
		 	}
		 	
		 	public void mouseExited(MouseEvent e) {
		 		 setBackground(oldC);
		 	}
		 	
		 	public void mouseClicked(MouseEvent e) {
		 		 //��ͨ�Ŵ���
		 		 if(e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2){
		 			    String NameAndIP = getText();
				 	    String name = NameAndIP.substring(0, NameAndIP.indexOf(':'));
			 			if(QQmap .get(nameToIP.get(name)) != null){//������ѵĴ����Ѿ����ڣ�
			        		QQmap.get(nameToIP.get(name)).requestFocus();
			        	    return ;
		 				}
		 			    QQFrame fm = null;
						try{
							 String[] ipStr = nameToIP.get(name).split("\\.");//���ַ����е����ַ���
					         byte[] ipBuf = new byte[4];//�洢IP��byte����
					         for(int i = 0; i < 4; i++){
					             ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
					         }
							 fm = new QQFrame(name, ipBuf);
							 QQmap.put(nameToIP.get(name), fm);//name �� ���ڵ�ӳ�䣡
						}catch(RuntimeException ex){
							JOptionPane.showMessageDialog(null, ex.getMessage(), "������ʾ��", JOptionPane.OK_CANCEL_OPTION);
							return ;
						}
		 		 }
		 		 else if(e.getButton() == MouseEvent.BUTTON3){
		 			  JPopupMenu pm = new JPopupMenu("�����");
		 			  JMenuItem del = new JMenuItem("ɾ��");
		 			  del.setFont(new Font("�����п�", Font.ITALIC, 20));
		 			  del.setForeground(new Color(255, 0, 0));
		 			  del.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								int choose = JOptionPane.showConfirmDialog(null, "ȷ��ɾ����", "ɾ���Ի���", JOptionPane.YES_NO_OPTION);
		 				  		if(choose == JOptionPane.OK_OPTION){
		 				  			friendPanel.remove(getParent());
		 				  			String NameAndIP = getText();
					 			 	String ip = NameAndIP.substring(NameAndIP.indexOf(':') + 1);
					 			 	String name = NameAndIP.substring(0, NameAndIP.indexOf(':'));
		 				  			QQset.remove(ip);
		 				  			QQFrame fm = QQmap.get(ip);
		 				  			if(fm != null) fm.dispose();
		 				  			nameToIP.remove(name);
		 				  			QQmap.remove(ip);
		 				  		
		 				  			MyPanel QQP = (MyPanel) friendPanel.getParent();
		 				  			QQP.setComponentsHeight(QQP.getComponentsHeight()-60);
		 				  			QQP.Mylayout();
		 				  			String[] sql = {"delete from QQTable where groupName=" + "\'" + groupName + "\' and" + " name=\'" + name + "\' and" + " ip=\'" + ip + "\'"};
		 				  			QQDialog.operateDB(sql);
		 				  		}
							}
					  });
		 			  
		 			  JMenuItem edit = new JMenuItem("�༭");
		 			  edit.addActionListener(new ActionListener() {
						
						  public void actionPerformed(ActionEvent e) {
								//�õ�֮ǰ�����ݣ�
								String content = getText();
								String oldName = content.substring(0, content.indexOf(':'));
								String oldIP = content.substring(content.indexOf(':')+1);
								//�������õ��ı�����
								InputDialog id = new InputDialog(oldIP, oldName, groupName, true);
								if(id.key == InputDialog.CANCELBTN || id.key == 0) return;
								
								String newName = id.getNameText();
								String newIP = id.getIPText();
								String newGroup = id.getGROUPText();
								
								setText(newName + ":" + newIP);
								
								QQFrame fm = QQmap.get(oldIP);
								if(fm != null){
									try{
										fm.setSendIAD(newIP);
										
										QQmap.put(newIP, fm);
										fm.setTitle("�ҵĺ��� " + newName);
										QQmap.remove(oldIP);
										QQset.remove(oldIP);
										QQset.add(newIP);
										nameToIP.remove(oldName);
										nameToIP.put(newName, newIP);
										
									}catch(RuntimeException ex){
										JOptionPane.showMessageDialog(null, ex.getMessage(), "�޸�֮���IP��", JOptionPane.OK_OPTION);
									}
								}
								
								if(!oldName.equals(newName) || !oldIP.equals(newIP) || !groupName.equals(newGroup)){
									String[] sql = {
										"delete from QQTable where groupName=" + "\'" + groupName + "\' and" + " name=\'" + oldName + "\' and" + " ip=\'" + oldIP + "\'",
										"insert into QQTable values(" + "\'" + newGroup + "\'," + "\'" + newName + "\'," + "\'" + newIP + "\')"
									};
									QQDialog.operateDB(sql);
								}
								if(!groupName.equals(newGroup)){
									JPanel TarPan = QQDialog.getBtnToPanel().get(newGroup);
									
									friendPanel.remove(getParent());
									MyPanel QQP = (MyPanel) friendPanel.getParent();
									QQP.setComponentsHeight(QQP.getComponentsHeight()-60);
									QQP.Mylayout();
									
									TarPan.add(getParent());
									if(TarPan.isVisible()){
										QQP.setComponentsHeight(QQP.getComponentsHeight()+60);
										QQP.Mylayout();
									}
								}
							}
					  });
		 			  edit.setFont(new Font("�����п�", Font.ITALIC, 20));
		 			  edit.setForeground(new Color(255, 0, 255));
		 			  pm.setBorderPainted(true);
		 			  pm.setBackground(new Color(125, 0, 125));
		 			  pm.add(del);  pm.add(edit);
		 			  pm.show(MyLabel.this, e.getX(), e.getY());
		 			  
		 			  
		 		 }
		 	}
		 	
	 });
	}
	
	public MyLabel(String arg0, Icon arg1, int arg2, String groupName) {
		super(arg0, arg1, arg2);
		this.groupName = groupName;
		friendPanel = QQDialog.getBtnToPanel().get(groupName);
		myInit();
	}

	public MyLabel(String text, String groupName) {
		super(text);
		this.groupName = groupName;
		friendPanel = QQDialog.getBtnToPanel().get(groupName);
		myInit();
	}

	public String getGroupName() {
		return groupName;
	}

}

class MyButton extends JButton {
	
	private JPanel groupPanel;
	private static MyPanel QQP;//�������
	
	public static void setMyScollPane(MyPanel p){
		 QQP = p;
	} 
	
	public MyButton(JPanel p1) {
		groupPanel = p1;
		addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent e) {//������������������Ч����
				if(e.getButton() == MouseEvent.BUTTON1){//������
						String groupName = getText();
						if(groupName.endsWith(">>")){
							groupPanel.setVisible(true);
							QQP.setComponentsHeight(QQP.getComponentsHeight() + groupPanel.getComponentCount()*60);
							setText(groupName.replace(">>", "<<"));
						}
						else{
							groupPanel.setVisible(false);
							QQP.setComponentsHeight(QQP.getComponentsHeight() - groupPanel.getComponentCount()*60);
							setText(groupName.replace("<<", ">>"));
						}
						QQP.Mylayout();
				} else if(e.getButton() == MouseEvent.BUTTON3){//����Ҽ�, �༭/ɾ������
					  JPopupMenu pm = new JPopupMenu("�����");
		 			  JMenuItem del = new JMenuItem("ɾ��");
		 			  del.setFont(new Font("�����п�", Font.ITALIC, 20));
		 			  del.setForeground(new Color(255, 0, 0));
		 			  del.addActionListener(new ActionListener() {
		 				  	
							public void actionPerformed(ActionEvent e) {
								int choose = JOptionPane.showConfirmDialog(null, "ȷ��ɾ���÷��鼰�����еĺ��ѣ�", "ɾ������", JOptionPane.YES_NO_OPTION);
		 				  		if(choose == JOptionPane.OK_OPTION){
		 				  			String groupName = getText();
		 				  			groupName = groupName.substring(0, groupName.length()-2);
		 				  			JPanel friendPanel = QQDialog.getBtnToPanel().get(groupName);
		 				  			MyPanel QQP = (MyPanel) friendPanel.getParent();
		 				  			Set<String> QQset = QQDialog.getSet();
		 				  			Map<String, QQFrame> QQmap = QQDialog.getMap(); 
		 				  			Map<String, String> nameToIP = QQDialog.getNameToIP();
		 				  			Set<String> groupSet = QQDialog.getGroupSet();
		 				  			for(int i=0; i<friendPanel.getComponentCount(); ++i){
				 				  			JPanel tmp = (JPanel) friendPanel.getComponent(i);
				 				  			String NameAndIP = ((MyLabel)tmp.getComponent(0)).getText();
							 			 	String ip = NameAndIP.substring(NameAndIP.indexOf(':') + 1);
							 			 	String name = NameAndIP.substring(0, NameAndIP.indexOf(':'));
				 				  			QQset.remove(ip);
				 				  			QQFrame fm = QQmap.get(ip);
				 				  			if(fm != null) fm.dispose();
				 				  			nameToIP.remove(name);
				 				  			QQmap.remove(ip);
		 				  			}
		 				  			int h;
		 				  			if(friendPanel.isVisible()) h=30+friendPanel.getComponentCount()*60;
		 				  			else h = 30;
		 				  			//�Ƴ�
		 				  			QQP.remove(MyButton.this.getParent());
		 				  			QQP.remove(friendPanel);
		 				  			groupSet.remove(groupName);
		 				  			QQP.setComponentsHeight(QQP.getComponentsHeight()-h);
		 				  			QQP.Mylayout();
		 				  			String[] sql = {"delete from QQTable where groupName=" + "\'" + groupName + "\'"};
		 				  			QQDialog.operateDB(sql);
		 				  		}
							}
					  });
		 			  
		 			  JMenuItem edit = new JMenuItem("�༭");
		 			  edit.addActionListener(new ActionListener() {
						
						  public void actionPerformed(ActionEvent e) {
								//�õ�֮ǰ�����ݣ�
								String oldGroupName = getText();
								String tail = oldGroupName.substring(oldGroupName.length()-2, oldGroupName.length());
								oldGroupName = oldGroupName.substring(0, oldGroupName.length()-2);
								String newGroupName = (String)JOptionPane.showInputDialog(null, "�·�����:", "�������޸�", JOptionPane.INFORMATION_MESSAGE, null, null, oldGroupName);
								if(newGroupName == null || oldGroupName.equals(newGroupName)) return;
								Set<String> groupSet = QQDialog.getGroupSet();
								groupSet.remove(oldGroupName);
								groupSet.add(newGroupName);
								QQDialog.getBtnToPanel().put(newGroupName, ((JPanel)QQDialog.getBtnToPanel().remove(oldGroupName)));
								setText(newGroupName+tail);
							}
					  });
		 			  edit.setFont(new Font("�����п�", Font.ITALIC, 20));
		 			  edit.setForeground(new Color(255, 0, 255));
		 			  pm.setBorderPainted(true);
		 			  pm.setBackground(new Color(125, 0, 125));
		 			  pm.add(del);  pm.add(edit);
		 			  pm.show(MyButton.this, e.getX(), e.getY());
				}
			}
		});
	}
}

class InputDialog extends Dialog{
	private JLabel ipLabel = new JLabel("IP��ַ:");
	private JLabel nameLabel = new JLabel("����:");
	private JLabel groupLabel = new JLabel("����:");
	private JButton okBtn = new JButton("ȷ��");
	private JButton cleBtn = new JButton("ȡ��");
	private JTextField ipText = new JTextField(20);
	private JTextField nameText = new JTextField(20);
	private Choice groupChoice = new Choice();
	private static final int OKBTN = 1;
	public static final int CANCELBTN = 2;
	public int key = 0;
	private String IP = null, NAME = null, GROUP = null; 
	private boolean flag = false;
	void initDialog(String selectName){
	    
		JPanel p = null;
		setModal(true);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		add(p = new JPanel());
		p.setLayout(new FlowLayout(FlowLayout.CENTER));
		ipLabel.setPreferredSize(new Dimension(50, 12));
		p.add(ipLabel); p.add(ipText);
		
		
		add(p = new JPanel());
		p.setLayout(new FlowLayout(FlowLayout.CENTER));
		nameLabel.setPreferredSize(new Dimension(50, 12));
		p.add(nameLabel); p.add(nameText);
		
		Set groupSet = QQDialog.getGroupSet();
		Iterator it = groupSet.iterator();
		while(it.hasNext())
			groupChoice.add((String)it.next());
		groupChoice.select(selectName);
		add(p = new JPanel());
		p.setLayout(new FlowLayout(FlowLayout.CENTER));
		groupLabel.setPreferredSize(new Dimension(50, 12));
		groupChoice.setPreferredSize(new Dimension(225, 12));
		p.add(groupLabel); p.add(groupChoice); 
		
		add(p = new JPanel());
		p.setLayout(new FlowLayout(FlowLayout.CENTER));
		p.add(okBtn); p.add(cleBtn);
		
		
		addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					 key = 0;
					 dispose();
				}
		});
		
		addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					 requestFocus();
				}
		});
		
		okBtn.addMouseListener(new MouseAdapter() {
				private Set<String> QQset = QQDialog.getSet();

				public void mouseClicked(MouseEvent e) {
					 key = InputDialog.OKBTN;
					 if("".equals(ipText.getText()) || "".equals(nameText.getText()) || !checkIP(ipText.getText()) )
							 JOptionPane.showMessageDialog(null, "��Ϣ��ȫ����IP��д����!");
					 else{
						 String ip = ipText.getText();
						 if(!flag && QQset.contains(ip)){
							 JOptionPane.showMessageDialog(null, "�����Ѵ��ڣ�", "QQ����", JOptionPane.OK_OPTION);
							 return ;
						 }
						 IP = ipText.getText();
						 NAME = nameText.getText();
						 GROUP = groupChoice.getSelectedItem();
						 dispose();//�ͷ�Dialog��Դ
					 }
				}
		});
		
		cleBtn.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					key = InputDialog.CANCELBTN;
					dispose();
				}
		});
		setSize(300, 200);
		setResizable(false);
		setLocation(200, 200);
		setVisible(true);
	}
	
	public InputDialog(String ip, String name, String groupName, boolean flag){
		super(new Frame());
		ipText.setText(ip);
		nameText.setText(name);
		this.flag = flag;
		initDialog(groupName);
	}
	
	public InputDialog(){
		super(new Frame());
		initDialog("�ҵĺ���");
	}
		
	public boolean checkIP(String ip){
		int i, begin = 0, end;
		for(i = 1; i <= 4; ++i){
			end = ip.indexOf('.', begin);
			if(end == -1) return false;
			int p = Integer.valueOf(ip.substring(begin, end));
			if(p < 0 || p > 255)  return false;
		}
		return true;
	}
	
	public String getGROUPText() {
		return GROUP;
	}

	public String getIPText(){
		return IP;
	}
	
	public String getNameText(){
		return NAME;
	}
	
}

public class QQDialog extends Frame{
		private MyPanel QQP = new MyPanel();
		private JPanel funP = new JPanel();
		private JButton add = new JButton("��Ӻ���");
		private JButton newGroup = new JButton("�½�����");
		private JButton serach = new JButton("��ѯ����");
		private JScrollPane jsp = new JScrollPane(QQP);
		private static Set<String>QQset;//IP�ļ���
		private static Map<String, QQFrame>QQmap;//IP����ͨ�Ŵ��ڵ�ӳ��
		private static Map<String, String>nameToIP;//������IP��ӳ��
		private static Set<String>groupSet;//����ļ���

		private static Map<String, JPanel>btnToPanel;//����˵�������ӳ��
		private int groupCnt = 0;
		 
		ImageIcon[] ii = new ImageIcon[3];
		
		public static Map<String, QQFrame> getMap(){
			return QQmap;
		}
		
		public static Set<String> getGroupSet() {
			return groupSet;
		}
		
		public static Set<String> getSet(){
			return QQset;
		}
		
		public static Map<String, String> getNameToIP(){
			return nameToIP;
		}
		public static Map<String, JPanel> getBtnToPanel(){
			return btnToPanel;
		}
		
		private void initPara(){
			QQset = new TreeSet<String>();
			QQmap = new TreeMap<String, QQFrame>();
			nameToIP = new TreeMap<String, String>();
			btnToPanel = new TreeMap<String, JPanel>();
			groupSet = new TreeSet<String>();
			MyButton.setMyScollPane(QQP);
			
			ii[0] = new ImageIcon("ff.jpg");
			ii[1] = new ImageIcon("gg.jpg");
			ii[2] = new ImageIcon("kk.jpg");
		}
		
		private void initDatabase(){
			initPara();
			Connection con = null;
			Statement sta = null;
			ResultSet rt = null;
			Statement stax = null;
			ResultSet rtx = null;
			try {
				Class.forName("org.sqlite.JDBC");
				con = DriverManager.getConnection("jdbc:sqlite:QQDatabase.db3");
				sta = con.createStatement();
				rt = sta.executeQuery("SELECT groupName FROM QQTable GROUP BY groupName");
				 
				while(rt.next()){
					String groupName = rt.getString("groupName");
					JPanel pan = new JPanel();
					pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
					
					MyButton btn = new MyButton(pan);
					JPanel pbtn = new JPanel();
					btn.setText(groupName+">>");
					pbtn.setLayout(new BorderLayout());
					pbtn.add(btn, "Center");
					pbtn.setPreferredSize(new Dimension(300, 30));//��ť�ĸ߶�
					QQP.add(pbtn);
					++groupCnt;//�ڼ�������
					groupSet.add(groupName);
					btnToPanel.put(groupName, pan);//���鰴ť������ӳ��
					stax = con.createStatement();
					rtx = stax.executeQuery("SELECT * FROM QQTable WHERE groupName=" + "\'" + groupName + "\'");//�÷���Ĳ�ѯ
					while(rtx.next()){
						JPanel tmp = new JPanel();
						tmp.setLayout(new FlowLayout(FlowLayout.CENTER));
						tmp.setBackground(new Color(205, 21, 0));
						tmp.setPreferredSize(new Dimension(300, 60));//��ǩ�ĸ߶�
						String name = rtx.getString("name");
						String ip = rtx.getString("ip");
						
						nameToIP.put(name, ip);
						QQset.add(ip);
						
						int k;
						Random rd = new Random();
						while((k=rd.nextInt())<0);
						k %= 3;
						MyLabel lab = new MyLabel(name + ":" + ip, ii[k], MyLabel.LEFT, groupName);
						tmp.add(lab);
						pan.add(tmp);
					}
					pan.setVisible(false);
					QQP.add(pan);
				}
				QQP.add(new JPanel());//ĩβ���
				QQP.setComponentsHeight(groupCnt*30);
				QQP.Mylayout();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try{
					if(rt !=null) rt.close();
					if(rtx !=null) rt.close();
					if(sta != null) sta.close();
					if(stax != null) sta.close();
					if(con != null) con.close();
				} catch(SQLException e){
					e.printStackTrace();
				}
			}
			
		}
		
		public static void operateDB(String[] sql){
			Connection con = null;
			Statement sta = null;
			try {
				Class.forName("org.sqlite.JDBC");
				con = DriverManager.getConnection("jdbc:sqlite:QQDatabase.db3");
				sta = con.createStatement();
				for(int i=0; i<sql.length; ++i)
					sta.executeUpdate(sql[i]);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try{
					if(sta != null) sta.close();
					if(con != null) con.close();
				} catch(SQLException e){
					e.printStackTrace();
				}
			}
			
		}
		
		public QQDialog(){
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
			jsp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			jsp.setPreferredSize(new Dimension(300, 450));
			QQP.setLayout(new BoxLayout(QQP, BoxLayout.Y_AXIS));
			initDatabase();
			add(jsp);
			add(funP);
			funP.setLayout(new FlowLayout(FlowLayout.CENTER));
			funP.add(add);
			funP.add(serach);
			funP.add(newGroup);
			
			add.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						InputDialog dlg = new InputDialog();
						if(dlg.key == InputDialog.CANCELBTN || dlg.key== 0) return;
						Random rd = new Random();
						int index = Math.abs(rd.nextInt()) % 3;
						MyLabel ll = new MyLabel(dlg.getNameText() + ":" + dlg.getIPText(), ii[index], JLabel.LEFT, dlg.getGROUPText());
						QQset.add(dlg.getIPText());//�������ӵĺ���IP��ӵ������У�
						nameToIP.put(dlg.getNameText(), dlg.getIPText());
						JPanel tmp = new JPanel();
						JPanel pan = btnToPanel.get(dlg.getGROUPText());//�õ��ĺ���Ҫ��ӵ������
						tmp.setLayout(new FlowLayout(FlowLayout.CENTER));
						tmp.setPreferredSize(new Dimension(300, 60));
						tmp.setBackground(new Color(205, 21, 0));
					    tmp.add(ll);
						/*
						 *  BoxLayout���ּ�ֱ�ǵ��۵�Ҫ����һ�����X��BoxLayout���֣������������һ�����Y�Ļ�
						 *  ��ôY�ͻ����X��壡��������һ�����Z�� ��ôY, Z�ͻ�һ����X��壡���ǿ�������Y,Z���
						 *  �ı����� ���X��ӵ���һ����ť���߱�ǩʱ�������ܿ������С.....�����ˣ�
						 *  
						 *  ������ҵ���������ǩ��ӵ����tmp�У�Ȼ���ٽ�tmp�����QQP����У������Ϳ��Կ��Ʊ�ǩ�Ĵ�С�ˣ�
						 *  ������µ�����ʱ��Ҫ����һ��֮ǰ����PreferredSize����֤ÿһ����ǩ�ľ������У�
						 *  Ҳ���Ǳ�֤���е���ӵ����ĸ߶�֮�� == QQP.getHeight();
						 * */
						
						pan.add(tmp);
						if(pan.isVisible()){
							QQP.setComponentsHeight(QQP.getComponentsHeight()+60);
							QQP.Mylayout();
							//JScrollBar jsb = jsp.getVerticalScrollBar();
							//���ڸ���ˣ�����ֱ�������Զ����ƶ�����Ͷ�
							//jsp.getViewport().setViewPosition(new Point(0, jsp.getVerticalScrollBar().getMaximum()));
						}
						
						//���ݿ�ĸ��£�
						String[] sql = {"insert into QQTable values(" + "\'" + dlg.getGROUPText() + "\'," + "\'" + dlg.getNameText() + "\'," + "\'" + dlg.getIPText() + "\')"};
						operateDB(sql);
					}
			});
			
			serach.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent e) {
						  String name = JOptionPane.showInputDialog(null, "��������", "���Ѳ�ѯ", JOptionPane.OK_OPTION);
						  if(name == null)  return ;
						  
						  String ip = nameToIP.get(name);
						  
						  if(ip == null)
							  	JOptionPane.showMessageDialog(null, "���Ѳ����ڣ�", "��ѯ���", JOptionPane.OK_OPTION);
						  else{
							  QQFrame fm = QQmap.get(ip);
							  if(fm == null){
								  try{
									     String[] ipStr = ip.split("\\.");//���ַ����е����ַ���
								         byte[] ipBuf = new byte[4];//�洢IP��byte����
								         for(int i = 0; i < 4; i++){
								             ipBuf[i] = (byte)(Integer.parseInt(ipStr[i])&0xff);
								         }
										 fm = new QQFrame(name, ipBuf);
									}catch(RuntimeException ex){
										 JOptionPane.showMessageDialog(null, ex.getMessage(), "Socket����", JOptionPane.OK_CANCEL_OPTION);
										 return ;
									}
								  QQmap.put(ip, fm);
							  }
							  else fm.requestFocus();
						  }
					}
			});
			
			newGroup.addMouseListener(new MouseAdapter() {

				public void mouseClicked(MouseEvent e) {
					String groupName = JOptionPane.showInputDialog(null, "��������", "�½�����", JOptionPane.OK_OPTION);
					if(groupName == null)  return ;
					if(groupSet.contains(groupName)){
						JOptionPane.showMessageDialog(null, "�����Ѵ��ڣ�", "������ʾ", JOptionPane.OK_CANCEL_OPTION);
						return ;
					}
					
					JPanel pan = new JPanel();
					pan.setLayout(new BoxLayout(pan, BoxLayout.Y_AXIS));
					pan.setVisible(false);
					MyButton btn = new MyButton(pan);
					JPanel pbtn = new JPanel();
					btn.setText(groupName+">>");
					pbtn.setLayout(new BorderLayout());
					pbtn.add(btn, "Center");
					pbtn.setPreferredSize(new Dimension(300, 30));//��ť�ĸ߶�
					QQP.add(pbtn, QQP.getComponentCount()-1);
					QQP.add(pan, QQP.getComponentCount()-1);
					++groupCnt;//�ڼ�������
					groupSet.add(groupName);//��ӵ�����ļ���
					QQDialog.getBtnToPanel().put(groupName, pan);
					QQP.setComponentsHeight(QQP.getComponentsHeight()+30);
					QQP.Mylayout();
				}
				
			});
			
			addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						if(qr == null) System.out.println("hehe");
						qr.setFlag();//udp�����߳�ֹͣ
						ss.setFlag();//tcp�����߳�ֹͣ
						System.exit(0);
					}
			});
			
			QQP.setBackground(new Color(255, 0, 255));
			funP.setBackground(new Color(255, 255, 0));
			setResizable(false);
			setSize(300, 500);
			setLocation(500, 200);
			setVisible(true);
		}
		
		private static ServerSocketQQ ss = null;
		private static QQReceive qr = null;
		
		public static void main(String[] args){

 			ss = new ServerSocketQQ();
 			new Thread(ss).start();
 			if(ServerSocketQQ.getPort() < 1) return;
 			
 			qr = new QQReceive();
 			new Thread(qr).start();
 			if(QQReceive.getPort() < 1)  return ;
 			
			new QQDialog();
		}
}
