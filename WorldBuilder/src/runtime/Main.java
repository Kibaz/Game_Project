package runtime;

import rendering.Loader;
import rendering.Window;

public class Main {
	
	public static void main(String args[])
	{
		Loader loader = new Loader();
		Window window = new Window(loader);
	}

}
