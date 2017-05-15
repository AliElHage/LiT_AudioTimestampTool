package lit.codejava.controller;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Controller implements LineListener {

	private static final int SECONDS_IN_HOUR = 3600;
	private static final int SECONDS_IN_MINUTE = 60;
	
	private boolean playCompleted;
	private boolean isStopped;
	private boolean isPaused;
	private boolean loopEnabled = false;
	
	private Clip audioClip;
	
	public void load(String audioFilePath)
			throws UnsupportedAudioFileException, IOException,
			LineUnavailableException {
		
		File audioFile = new File(audioFilePath);
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		AudioFormat format = audioStream.getFormat();
		
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		
		audioClip = (Clip)AudioSystem.getLine(info);
		audioClip.addLineListener((LineListener) this);
		audioClip.open(audioStream);
		
	}
	
	public long getClipSecondLength(){
		return audioClip.getMicrosecondLength() / 1_000_000;
	}
	
	public String getClipLengthString() {
		String length = "";
		long hour = 0;
		long minute = 0;
		long seconds = audioClip.getMicrosecondLength() / 1_000_000;
		
		System.out.println(seconds);
		
		if (seconds >= SECONDS_IN_HOUR) {
			hour = seconds / SECONDS_IN_HOUR;
			length = String.format("%02d:", hour);
		} else {
			length += "00:";
		}
		
		minute = seconds - hour * SECONDS_IN_HOUR;
		if (minute >= SECONDS_IN_MINUTE) {
			minute = minute / SECONDS_IN_MINUTE;
			length += String.format("%02d:", minute);
			
		} else {
			minute = 0;
			length += "00:";
		}
		
		long second = seconds - hour * SECONDS_IN_HOUR - minute * SECONDS_IN_MINUTE;
		
		length += String.format("%02d", second);
		
		return length;
	}
	
	public void play() throws IOException {
		
		audioClip.start();
		playCompleted = false;
		isStopped = false;
		
		while(!playCompleted){
			try{
				Thread.sleep(1000);
			} catch(InterruptedException e){
				e.printStackTrace();
			}
			if(isStopped){
				audioClip.stop();
				break;
			}
			else if(isPaused){
				audioClip.stop();
			}
			else audioClip.start();
		}
		
		audioClip.close();
		
	}
	
	public void stop(){
		isStopped = true;
		isPaused = false;
	}
	
	public void pause(){
		isPaused = true;
	}
	
	public void resume(){
		isPaused = false;
	}
	
	public void toggleLoop(){
		if(loopEnabled) loopEnabled = false;
		else loopEnabled = true;
	}
	
	public boolean getLoopEnabled(){
		return loopEnabled;
	}
	
	public void setPlayCompleted(boolean value){
		playCompleted = value;
	}
	
	public boolean getPlayCompleted(){
		return playCompleted;
	}
	
	@Override
	public void update(LineEvent event){
		LineEvent.Type type = event.getType();
		if(type == LineEvent.Type.STOP){
			if(isStopped || !isPaused){
				playCompleted = true;
			}
		}
	}
	
	public Clip getAudioClip(){
		return audioClip;
	}
	
}
