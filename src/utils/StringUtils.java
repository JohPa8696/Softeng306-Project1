package utils;

public final class StringUtils {
	
	/**
	 * Check if the string represents an integer
	 * @param input
	 * @return
	 */
	public static boolean isNumeric(String input){
		try{
			int in= Integer.parseInt(input.trim());
		}catch (NumberFormatException e){
			return false;
		}

		return true;
	}
	
}
