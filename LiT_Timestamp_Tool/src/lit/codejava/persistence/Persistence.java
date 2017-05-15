package lit.codejava.persistence;

import java.util.Iterator;

import model.AudioTool;
import model.TimeBlock;

public class Persistence {
	public static void initializeXStream(String filename){
		PersistenceXStream.setFilename(filename);
		PersistenceXStream.setAlias("timeBlock", TimeBlock.class);
		PersistenceXStream.setAlias("AudioTool", AudioTool.class);
	}

	public static void loadModel(String filename){
		AudioTool master = AudioTool.getInstance();
		Persistence.initializeXStream(filename);
		AudioTool master2 = (AudioTool) PersistenceXStream.loadFromXMLwithXStream();
		if(master2 != null){
			// unfortunately this creates a second RegistrationManager object, even though it is a singleton
			// copy loaded model into singleton instance of RegistrationManger, because this will be used throughout the application
			Iterator<TimeBlock> tbIt = master2.getTimeBlocks().iterator();
			while(tbIt.hasNext())
				master.addTimeBlock(tbIt.next());
		}
	}

}
