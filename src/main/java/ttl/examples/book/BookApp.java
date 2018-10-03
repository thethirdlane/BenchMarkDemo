package ttl.examples.book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class BookApp {

	public static void main(String[] args) throws IOException {
		// countWordsTheOldWay("PrideAndPrejudice.txt");
		countWords("PrideAndPrejudice.txt");
	}

	public static Map<String, Long> countWordsTheOldWay(String fileName) throws IOException {
		// List<String> lines = Files.readAllLines(Paths.get(fileName));

		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

			Map<String, Long> result = new TreeMap<>();
			String line;

			while ((line = reader.readLine()) != null) {
				String[] words = line.split("\\W");

				for (String word : words) {
					Long count = result.get(word);
					if (count == null) {
						count = new Long(0);
					}
					count++;
					result.put(word, count);
				}
			}
			return result;
		}

		// result.forEach((key, value) -> System.out.println("Word: " + key + ",
		// Count:" + value));
	}

	public static Map<String, Long> countWords(String fileName) throws IOException {
		Map<String, Long> result = Files.lines(Paths.get(fileName)).flatMap(s -> Arrays.stream(s.split("\\W")))
				.collect(Collectors.groupingBy(s -> s, TreeMap::new, Collectors.counting()));

		return result;
		// result.forEach((key, value) -> System.out.println("Word: " + key + ",
		// Count:" + value));
	}

}
