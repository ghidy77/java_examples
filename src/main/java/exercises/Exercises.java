package exercises;

import java.util.*;

public class Exercises {


	public static void main(String[] args) {
		System.out.println("Try some exercises");
	}

	/**
	 * Determine if a list of strings contain valid Pangrams (if they use all letters from a-z)
	 * @param pangram
	 * @return Codes for pangrams: 0 for invalid, 1 for invalid
	 */
	public static String isPangram(List<String> pangram) {

		String result = "";
		// Iterate through strings
		for (int i = 0; i < pangram.size(); i++) {
			// create list of alphabet letters
			Set<Character> alphabet = new HashSet<>();
			for (char ch = 'a'; ch <= 'z'; ch++) {
				alphabet.add(ch);
			}

			// for each letter of sentence, remove used letters from alphabet
			String sentence = pangram.get(i);
			for (int j = 0; j < sentence.length(); j++) {
				if (alphabet.contains(sentence.charAt(j))) {
					alphabet.remove(sentence.charAt(j));
				}
			}
			// if alphabet is empty, we found a Pangram
			if (alphabet.isEmpty()) {
				result += "1";
			}
			result += "0";
		}

		return result;
	}

	/**
	 * Return first repeated word from a sentence
	 * @param sentence
	 * @return
	 */
	public static String firstRepeatedWord(String sentence) {
		String[] split = sentence.split(" ");
		List<String> word = new ArrayList<>();

		for (int i = 0; i<split.length; i++){
			String usedWord = split[i].toLowerCase();
			// save the word in a list if is not already used
			if (!word.contains(usedWord)){
				word.add(usedWord);
			} else {
				return usedWord;
			}
		}
		return "";
	}

	public static Map<String, Integer> countWordsInASentence(String sentence){
		//testString = "this is a sentence and this is an example have fun with it this this";

		// first solution, using Function Consumer, java 8
		HashMap<String, Integer> mapOfUsedWords = new HashMap<>();
		String[] resultArray = sentence.split(" ");

		List<String> list = Arrays.asList(resultArray);
		list.forEach( word->{
			if (mapOfUsedWords.containsKey(word)) {
				int counter = mapOfUsedWords.get(word) + 1;
				mapOfUsedWords.replace(word, counter);
			} else {
				mapOfUsedWords.put(word, 1);
		}});


		// old-style solution, iterate through array
		HashMap<String, Integer> secondMapOfUsedWords = new HashMap<>();
		for (int i = 0; i < resultArray.length; i++) {
			if (secondMapOfUsedWords.containsKey(resultArray[i])) {
				int oldValue = secondMapOfUsedWords.get(resultArray[i]);
				secondMapOfUsedWords.replace(resultArray[i], ++oldValue);
			} else {
				secondMapOfUsedWords.put(resultArray[i], 1);
			}
		}
		return mapOfUsedWords;
	}

	/**
	 * Determine if an array contain two numbers that, if added, will result into expectedSum
	 * @param numbers
	 * @param expectedSum
	 * @return
	 */
	public static int[] twoSum(int[] numbers, int expectedSum) {

//		int[] example = new int[]{3,3,2,7,11,15};
//		System.out.println(twoSum(example, 9)[0]);
//		System.out.println(twoSum(example, 9)[1]);
//		System.out.println(twoSum(example, 6)[0]);
//		System.out.println(twoSum(example, 6)[1]);

		int[] answer = new int[2];

		//create list of expected numbers
		List<Integer> listOfExpectedNumbers = new ArrayList<>();
		for (int i=0; i< numbers.length; i++){
			listOfExpectedNumbers.add(expectedSum-numbers[i]);
		}

		// check if numbers contain one of the expected numbers
		for (int i=0; i < numbers.length; i++){
			if(listOfExpectedNumbers.contains(numbers[i])){
				System.out.println("Found");
				// check to see if we found two different items. e.g. 3 + 3 = 6, validate we have two different 3
				if (numbers[i] == (expectedSum-numbers[i]) && i == listOfExpectedNumbers.indexOf(expectedSum-numbers[i])){
					System.out.println("Same item, SKIP");
					continue;
				}
				answer[0] = numbers[i];
				answer[1] = expectedSum-numbers[i];
				return answer;
			}
		}
		return answer;
	}

}