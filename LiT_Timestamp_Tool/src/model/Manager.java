package model;

public class Manager {

	private static int nextId = 1;
	
	private String permission;
	private int id;
	private AudioTool audioTool;
	
	public Manager(String permission, AudioTool audioTool)
	  {
	    this.permission = permission;
	    id = nextId++;
	    if (!setAudioTool(audioTool))
	    {
	      throw new RuntimeException("Unable to create Manager due to audioTool");
	    }
	  }

	  public boolean setPermission(String aPermission)
	  {
	    boolean wasSet = false;
	    permission = aPermission;
	    wasSet = true;
	    return wasSet;
	  }

	  public String getPermission()
	  {
	    return permission;
	  }

	  public int getId()
	  {
	    return id;
	  }

	  public AudioTool getAudioTool()
	  {
	    return audioTool;
	  }

	  public boolean setAudioTool(AudioTool audioTool)
	  {
	    boolean wasSet = false;
	    if (audioTool != null)
	    {
	    	this.audioTool = audioTool;
	      wasSet = true;
	    }
	    return wasSet;
	  }

	  public void delete()
	  {
		  audioTool = null;
	  }


	  public String toString()
	  {
		  String outputString = "";
	    return super.toString() + "["+
	            "id" + ":" + getId()+ "," +
	            "permission" + ":" + getPermission()+ "]" + System.getProperties().getProperty("line.separator") +
	            "  " + "fTMS = "+(getAudioTool()!=null?Integer.toHexString(System.identityHashCode(getAudioTool())):"null")
	     + outputString;
	  }
	
}
