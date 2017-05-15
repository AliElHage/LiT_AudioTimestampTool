package lit.codejava.controller;

import java.sql.Time;

import model.AudioTool;
import model.TimeBlock;
import lit.codejava.persistence.PersistenceXStream;
import lit.codejava.controller.InvalidInputException;

public class TimingController {
	
	public TimingController(){}
	
	public void createTimeBlock(Time startTime, Time endTime, int type, String description) throws InvalidInputException{
		AudioTool master = AudioTool.getInstance();
		String error = "";
		if (startTime == null)
			error += "Time block start time cannot be empty! ";
		if (endTime == null)
			error += "Time block end time cannot be empty! ";
		if (type < 0 || type > 2)
			error += "Time block type is invalid. ";
		if (startTime != null && endTime != null && endTime.getTime() < startTime.getTime())
			error += "Time block end time cannot be before start time! ";
		if (description.length() > 150)
			error += "Input description is too long. Max length: 150 characters. ";
		error = error.trim();
		if(error.length() > 0)
			throw new InvalidInputException(error);
		TimeBlock timeBlock = new TimeBlock();
		timeBlock.setStartTime(startTime.getTime());
		timeBlock.setEndTime(endTime.getTime());
		timeBlock.setType(type);
		timeBlock.setDescription(description);
		
		master.addTimeBlock(timeBlock);
		PersistenceXStream.saveToXMLwithXStream(master);
	}

}
