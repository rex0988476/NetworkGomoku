import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class MyClientThread extends Thread {
	private InputStream in;
	private JTextArea chatArea;
	private Socket clientChat;
	public MyClientThread(JTextArea ca,Socket cc) {
		super();
		chatArea = ca;
		//chatArea.append("Chat Room:\n");
		clientChat = cc;
	}
	
	public void run() {
		//chat
		String sbuffer;
		byte[] chatBuffer = new byte[255];
		String endS="leave__________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________________";

		try {
			in = clientChat.getInputStream();
			while (true){
				in.read(chatBuffer);

				sbuffer = new String(chatBuffer);
				if(sbuffer.equals(endS)){

					in.close();
					clientChat.close();

					break;
				}
				chatArea.append("Opponent: " + sbuffer + "\n");

				chatBuffer = new byte[255];
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		System.out.println("client chat thread fin.");
	}
}

class MyClientSoundThread extends Thread {
	private boolean isLoop=false;
	private int waitTime=0;
	private File soundClickFile;
	private AudioInputStream audioInputStream;
	private Clip clickSound;
	public MyClientSoundThread(File soundfile,boolean is_loop,int wait_mill_time) {
		super();
		isLoop = is_loop;
		waitTime = wait_mill_time;
		soundClickFile = soundfile;
	}

	public void run() {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundClickFile);
			AudioFormat audioFormat = audioInputStream.getFormat();
			//int bufferSize = (int) Math.min(audioInputStream.getFrameLength() * audioFormat.getFrameSize(), Integer.MAX_VALUE);
			int bufferSize = Integer.MAX_VALUE;
			DataLine.Info dataLineInfo = new DataLine.Info(Clip.class, audioFormat, bufferSize);
			clickSound = (Clip) AudioSystem.getLine(dataLineInfo);
			clickSound.open(audioInputStream);
			if(isLoop) {
				clickSound.loop(Integer.MAX_VALUE);
			}
			clickSound.start();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

}