package bg.sofia.uni.fmi.mjt.checker;

public class ArgumentsChecker {

	public static boolean areEnoughArgumentsReceived(String[] tokens, int numberArgs) {
		return tokens.length == numberArgs;
	}  
}
