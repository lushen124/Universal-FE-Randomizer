package random.general;

import util.recordkeeper.ChangelogBuilder;
import util.recordkeeper.RecordKeeper;

public interface RandomizerListener {
	
	public void onStatusUpdate(String status);
	public void onComplete(RecordKeeper rk, ChangelogBuilder cb);
	public void onError(String errorString);
	public void onProgressUpdate(double progress);

}
