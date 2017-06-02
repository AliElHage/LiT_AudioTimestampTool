package lit.codejava.controller;

import java.applet.AudioClip;
import java.sql.Time;

import javax.sound.sampled.Clip;

import model.AudioTool;
import model.TimeBlock;
import lit.codejava.persistence.PersistenceXStream;
import lit.codejava.controller.InvalidInputException;

public class TimingController {
	
	private Controller c;
	private Clip clip;
	
	public TimingController(Controller c){
		this.c = c;
		clip = this.c.getAudioClip();
	}
	
	public void createTimeBlock(Long startTime, Long endTime, int type, String description) throws InvalidInputException{
		AudioTool master = AudioTool.getInstance();
		String error = "";
		if (startTime == null)
			error += "Time block start time cannot be empty. ";
		if (endTime == null)
			error += "Time block end time cannot be empty. ";
		if(startTime < 0 || endTime < 0)
			error += "Time must be positive. ";
		if(startTime > clip.getMicrosecondLength() / 1000 || endTime > clip.getMicrosecondLength() / 1000)
			error += "Time must be between the start and the end of the clip. ";
		if (type < 0 || type > 2)
			error += "Time block type is invalid. ";
		if (startTime != null && endTime != null && endTime < startTime)
			error += "Time block end time cannot be before start time. ";
		if (description.length() > 150)
			error += "Input description is too long. Max length: 150 characters. ";
		error = error.trim();
		if(error.length() > 0)
			throw new InvalidInputException(error);
		TimeBlock timeBlock = new TimeBlock();
		timeBlock.setStartTime(startTime);
		timeBlock.setEndTime(endTime);
		timeBlock.setType(type);
		timeBlock.setDescription(description);
		
		master.addTimeBlock(timeBlock);
		PersistenceXStream.saveToXMLwithXStream(master);
	}
	
	public void addTime(Long time) throws InvalidInputException{
		AudioTool master = AudioTool.getInstance();
		String error = "";
		if(time < 0)
			error += "Time must be greater than 0!";
		if(time > clip.getMicrosecondLength() / 1000)
			error += "Time must be within length of audio clip!";
		error = error.trim();
		if(error.length() > 0)
			throw new InvalidInputException(error);
		
		master.addToTimeline(time);
		PersistenceXStream.saveToXMLwithXStream(master);
	}

}
