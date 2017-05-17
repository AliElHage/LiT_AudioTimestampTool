/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.20.1.4071 modeling language!*/

package model;
import java.util.*;

// line 47 "../model.ump"
// line 85 "../model.ump"
public class TimeBlock
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //MenuItem Attributes
  private String description;
  private long startTime;
  private long endTime;
  private int type;
  
  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TimeBlock()
  {
	description = "";
    startTime = 0;
    endTime = 0;
    type = -1;
  }

  //------------------------
  // INTERFACE
  //------------------------
  
  public boolean setDescription(String aDescription)
  {
    boolean wasSet = false;
    description = aDescription;
    wasSet = true;
    return wasSet;
  }

  public String getDescription()
  {
    return description;
  }
  
  public boolean setStartTime(long aStartTime)
  {
	  boolean wasSet = false;
	  startTime = aStartTime;
	  wasSet = true;
	  return wasSet;
  }
  
  public long getStartTime()
  {
	  return startTime;
  }
  
  public boolean setEndTime(long aEndTime)
  {
	  boolean wasSet = false;
	  endTime = aEndTime;
	  wasSet = true;
	  return wasSet;
  }
  
  public long getEndTime()
  {
	  return endTime;
  }
  
  public boolean setType(int aType)
  {
	  boolean wasSet = false;
	  type = aType;
	  wasSet = true;
	  return wasSet;
  }
  
  public long getType()
  {
	  return type;
  }
  
  public String toString()
  {
	  String outputString = "";
    return super.toString() + "["+
            "description" + ":" + getDescription()+ "]"
     + outputString;
  }
  
}