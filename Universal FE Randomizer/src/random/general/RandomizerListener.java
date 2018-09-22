package random.general;

import util.recordkeeper.RecordKeeper;

public interface RandomizerListener {
	
	public void onStatusUpdate(String status);
	public void onComplete(RecordKeeper rk);
	public void onError(String errorString);
	public void onProgressUpdate(double progress);

}
