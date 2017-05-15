package lit.codejava.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.sound.sampled.Clip;
import javax.swing.*;

public class PlayingTimer extends Thread 
{
	private DateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
	
	private boolean isRunning = false;
	private boolean isPaused = false;
	private boolean isReset = false;
	
	private long startTime;				// time at which the audio clip starts
	private long currentPauseTime;		// timer to keep track of the time spent on pause
	private double discreteError;
	
	private int sliderPosition;			// keeps track of the current slider position
	
	private JLabel labelTime;
	private JSlider slider;
	private Clip audioClip;
	
	public void setAudioClip(Clip audioClip){
		this.audioClip = audioClip;
	}
	
	public PlayingTimer(JLabel labelTime, JSlider slider){
		this.labelTime = labelTime;
		this.slider = slider;
	}
	
	public void run(){
		
		isRunning = true;
		
		startTime = System.currentTimeMillis();
		
		sliderPosition = 0;
		
		while(isRunning){
			try{
				Thread.sleep(100);
				
				GlobalTimer.setTimerUpdate(true);
				if(!GlobalTimer.getUserUpdate()){
					//GlobalTimer.setTimerUpdate(true);
					if(true){
						if(audioClip != null/* && audioClip.isRunning()*/){
							//labelTime.setText(timeToString());
							//int currentPosition = (int)audioClip.getMicrosecondPosition() / 1000000;
							int currentPosition = (int)audioClip.getMicrosecondPosition() / 100000;
							//discreteError = audioClip.getMicrosecondPosition() / 100000.0 - Math.round(currentPosition);
							//System.out.println(discreteError);
							slider.setValue(currentPosition);
							labelTime.setText(timeAsString(audioClip.getMicrosecondPosition()));
						}
					}
					//else /*currentPauseTime += 100*/;
				}
				else{
					int currentPosition = slider.getValue();
					int offset = currentPosition - GlobalTimer.getInitPosition();
					GlobalTimer.addOffsetValue(offset);
					GlobalTimer.setUserUpdate(false);
					GlobalTimer.setStateChanged(false);
					currentPauseTime = 0;
					sliderPosition = currentPosition;
				}
//				if(!isPaused){
//					if(audioClip != null && audioClip.isRunning()){
//						labelTime.setText(timeToString());
//						//int currentPosition = (int)audioClip.getMicrosecondPosition() / 1000000;
//						int currentPosition = (int)audioClip.getMicrosecondPosition() / 100000;
//						slider.setValue(currentPosition);
//					}
//				}
//				else currentPauseTime += 100;
//				
//				GlobalTimer.setValue(System.currentTimeMillis() - startTime - currentPauseTime);
				
			} catch(InterruptedException e){
				e.printStackTrace();
				if(isReset){
					slider.setValue(0);
					labelTime.setText("00:00:00");
					isRunning = false;
					break;
				}
			} finally{
				GlobalTimer.setTimerUpdate(false);
			}
		}
	}
	
	public void resetTimer() {
		isReset = true;
		isRunning = false;
	}
	
	public void pauseTimer(){
		isPaused = true;
	}
	
	public void resumeTimer(){
		isPaused = false;
	}
	
	private String timeToString(){
		long now = System.currentTimeMillis();
		Date current = new Date(now - startTime - currentPauseTime + GlobalTimer.getOffsetValue() * 100 - GlobalTimer.getMoveDelay() - Math.round(discreteError * 10));
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timeCounter = dateFormatter.format(current);
		return timeCounter;
	}
	
	private String timeAsString(long timeInMicros){
		Date current = new Date(timeInMicros / 1000);
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
		String timeCounter = dateFormatter.format(current);
		return timeCounter;
	}
	
}
