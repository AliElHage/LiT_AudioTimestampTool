package model;

import java.util.ArrayList;
import java.util.List;

import lit.codejava.controller.PlayingTimer;

public class AudioTool {
	
	private static AudioTool theInstance = null;
	
	private List<TimeBlock> timeBlocks;
	private String[] previewTableLabels = {"Start Time", "End Time", "Type", "Description"};
	
	private AudioTool(){
		timeBlocks = new ArrayList <TimeBlock>();
	}
	
	public static AudioTool getInstance(){
		if(theInstance == null){
			theInstance = new AudioTool();
			return theInstance;
		}
		else return theInstance;
	}
	
	public String[] getPreviewLabels(){
		  return previewTableLabels;
	}
	
	public TimeBlock getTimeBlock(int index){
		return timeBlocks.get(index);
	}
	
	public List<TimeBlock> getTimeBlocks(){
		return timeBlocks;
	}
	
	public int numberOfTimeBlocks(){
		return timeBlocks.size();
	}
	
	public boolean hasTimeBlocks(){
	    boolean has = timeBlocks.size() > 0;
	    return has;
	}

	  public int indexOfEquipment(TimeBlock timeBlock){
	    int index = timeBlocks.indexOf(timeBlock);
	    return index;
	}
	  
	  public boolean addTimeBlock(TimeBlock timeBlock){
	    boolean wasAdded = false;
	    if (timeBlocks.contains(timeBlock)) { return false; }
	    timeBlocks.add(timeBlock);
	    wasAdded = true;
	    return wasAdded;
	  }

	  public boolean removeTimeBlock(TimeBlock timeBlock){
	    boolean wasRemoved = false;
	    if (timeBlocks.contains(timeBlock))
	    {
	    	timeBlocks.remove(timeBlock);
	      wasRemoved = true;
	    }
	    return wasRemoved;
	  }

	  public boolean addTimeBlockAt(TimeBlock timeBlock, int index){  
	    boolean wasAdded = false;
	    if(addTimeBlock(timeBlock))
	    {
	      if(index < 0 ) { index = 0; }
	      if(index > numberOfTimeBlocks()) { index = numberOfTimeBlocks() - 1; }
	      timeBlocks.remove(timeBlock);
	      timeBlocks.add(index, timeBlock);
	      wasAdded = true;
	    }
	    return wasAdded;
	  }

	  public boolean addOrMoveEquipmentAt(TimeBlock timeBlock, int index){
	    boolean wasAdded = false;
	    if(timeBlocks.contains(timeBlock))
	    {
	      if(index < 0 ) { index = 0; }
	      if(index > numberOfTimeBlocks()) { index = numberOfTimeBlocks() - 1; }
	      timeBlocks.remove(timeBlock);
	      timeBlocks.add(index, timeBlock);
	      wasAdded = true;
	    } 
	    else 
	    {
	      wasAdded = addTimeBlockAt(timeBlock, index);
	    }
	    return wasAdded;
	  }

	
}
