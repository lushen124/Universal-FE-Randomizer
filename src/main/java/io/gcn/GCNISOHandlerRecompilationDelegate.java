package io.gcn;

public interface GCNISOHandlerRecompilationDelegate {
	
	public void onError(String errorMessage);
	public void onProgressUpdate(double progress);
	public void onStatusUpdate(String status);
}
