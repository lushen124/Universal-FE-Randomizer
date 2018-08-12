package random;

public interface RandomizerListener {
	
	public void onStatusUpdate(String status);
	public void onComplete();
	public void onError(String errorString);
	public void onProgressUpdate(double progress);

}
