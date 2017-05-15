package lit.codejava.controller;

public class GlobalTimer {
	
	private static long counter = 0;
	private static int timeOffset = 0;
	private static int initPosition = 0;
	private static boolean timerUpdate = false;
	private static boolean userUpdate = false;
	private static boolean stateChanged = false;
	
	public static void addMoveDelay(long value){
		counter += value;
	}
	
	public static void setMoveDelay(long value){
		counter = value;
	}
	
	public static long getMoveDelay(){
		return counter;
	}
	
	public static void addOffsetValue(int value){
		timeOffset += value;
	}
	
	public static void setOffsetValue(int value){
		timeOffset = value;
	}
	
	public static int getOffsetValue(){
		return timeOffset;
	}
	
	public static void setInitPosition(int value){
		initPosition = value;
	}
	
	public static int getInitPosition(){
		return initPosition;
	}
	
	public static void addValue(long value){
		counter += value;
	}
	
	public static void multiplyValue(long value){
		counter *= value;
	}
	
	public static void setTimerUpdate(boolean value){
		timerUpdate = value;
	}
	
	public static boolean getTimerUpdate(){
		return timerUpdate;
	}
	
	public static void setUserUpdate(boolean value){
		userUpdate = value;
	}
	
	public static boolean getUserUpdate(){
		return userUpdate;
	}
	
	public static void setStateChanged(boolean value){
		stateChanged = value;
	}
	
	public static boolean getStateChanged(){
		return stateChanged;
	}
	
}
