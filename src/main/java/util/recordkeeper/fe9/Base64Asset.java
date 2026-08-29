package util.recordkeeper.fe9;

public class Base64Asset {
	String name;
	String base64String;
	int width;
	int height;
	
	public Base64Asset(String name, String base64, int width, int height) {
		this.name = name;
		base64String = base64;
		this.width = width;
		this.height = height;
	}
}