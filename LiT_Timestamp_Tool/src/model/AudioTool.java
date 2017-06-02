package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import lit.codejava.controller.PlayingTimer;

public class AudioTool {
	
	private static AudioTool theInstance = null;
	
	private List<TimeBlock> timeBlocks;
	private String[] previewTableLabels = {"Start Time", "End Time", "Type", "Description"};
	private ArrayList<Long> times;
	
	private AudioTool(){
		timeBlocks = new ArrayList <TimeBlock>();
		times = new ArrayList<>();
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
	  
	  public boolean addToTimeline(long aType)
	  {
		  boolean wasSet = false;
		  times.add(aType);
		  Collections.sort(times);
		  wasSet = true;
		  return wasSet;
	  }
	  
	  public boolean removeFromTimeline(int index)
	  {
		  boolean wasRemoved = false;
		  times.remove(index);
		  wasRemoved = true;
		  return wasRemoved;
	  }
	  
	  public ArrayList<Long> getTimeline(){
		  return times;
	  }
	  
	  public boolean setTimeline(ArrayList<Long> timeList){
		  boolean wasSet = false;
		  times = timeList;
		  wasSet = true;
		  return wasSet;
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

	  public void sortTimeBlocks(int column){
		  Comparator<TimeBlock> comp = new Comparator<TimeBlock>(){
		       @Override
		       public int compare(TimeBlock t1, TimeBlock t2)
		       {
		    	   int comparison = 0;
		    	   if(column == 0){
		    		   comparison = (int)Long.compare(t1.getStartTime(), t2.getStartTime());
			    	   if(comparison == 0){
			    		   comparison = (int)Long.compare(t1.getEndTime(), t2.getEndTime());
			    		   if(comparison == 0){
				    		   comparison = (int)Long.compare(t1.getType(), t2.getType());
				    	   }
			    	   }
		    	   }
		    	   else if(column == 1){
		    		   comparison = (int)Long.compare(t1.getEndTime(), t2.getEndTime());
			    	   if(comparison == 0){
			    		   comparison = (int)Long.compare(t1.getStartTime(), t2.getStartTime());
			    		   if(comparison == 0){
				    		   comparison = (int)Long.compare(t1.getType(), t2.getType());
				    	   }
			    	   }
		    	   }
		    	   else if(column == 2){
		    		   comparison = (int)Long.compare(t1.getType(), t2.getType());
			    	   if(comparison == 0){
			    		   comparison = (int)Long.compare(t1.getStartTime(), t2.getStartTime());
			    		   if(comparison == 0){
				    		   comparison = (int)Long.compare(t1.getEndTime(), t2.getEndTime());
				    	   }
			    	   }
		    	   }
		           return comparison;
		       }        
		   };
		   timeBlocks.sort(comp);
	  }
	
}
