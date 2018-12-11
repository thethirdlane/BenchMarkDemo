package ttl.examples.book;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BookAppFilled {

    public static void main(String[] args) throws IOException {
        Map<String, Long> cwo = countWordsTheOldWay("PrideAndPrejudice.txt");
        Map<String, Long> cwom = countWordsTheOldWayWithMatcher(
                "PrideAndPrejudice.txt");
        Map<String, Long> cw = countWordsStream("PrideAndPrejudice.txt");
        Map<String, Long> cwm = countWordsStreamMatcher("PrideAndPrejudice.txt");
        Map<String, Long> cwcm = countWordsStreamCustomMatcher("PrideAndPrejudice.txt");
        Map<String, Long> cwm9 = countWordsStreamMatcherJdk9("PrideAndPrejudice.txt");
        Map<String, Long> cwp = countWordsParallel("PrideAndPrejudice.txt");

        System.out.printf("cwo.size = %d, " +
                        "cwom.size = %d " +
                        "cw.size = %d " +
                        "cwm.size = %d " +
                        "cwcm.size = %d " +
                        "cwm9.size = %d " +
                        "cwp.size = %d " +
                        "%n", cwo.size(),
                cwom.size(),
                cw.size(),
                cwm.size(),
                cwcm.size(),
                cwm9.size(),
                cwp.size());
        //cwm.forEach((k, v) -> System.out.printf("%s = %d%n", k, v));
    }

    public static Map<String, Long> countWordsTheOldWay(String fileName) throws IOException {
        // List<String> lines = Files.readAllLines(Paths.get(fileName));

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            Map<String, Long> result = new TreeMap<>();
            String line;

            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\W");

                for (String word : words) {
                    if (!word.matches("\\s*")) {
                        long count = result.computeIfAbsent(word, (s -> 0L));
                        count++;
                        result.put(word, count);
                    }
                }
            }
            return result;
        }

        // result.forEach((key, value) -> System.out.println("Word: " + key + ",
        // Count:" + value));
    }

    public static Map<String, Long> countWordsTheOldWayWithMatcher(String fileName) throws IOException {
        // List<String> lines = Files.readAllLines(Paths.get(fileName));
        Pattern wordRE = Pattern.compile("\\w+");

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {

            Map<String, Long> result = new TreeMap<>();
            String line;

            while ((line = reader.readLine()) != null) {
                Matcher matcher = wordRE.matcher(line);
                matcher.reset();
                while (matcher.find()) {
                    String word = matcher.group();
                    long count = result.computeIfAbsent(word, (s -> 0L));
                    count++;
                    result.put(word, count);
                }
            }
            return result;
        }
    }

    // result.forEach((key, value) -> System.out.println("Word: " + key + ",
    // Count:" + value));

    public static Map<String, Long> countWordsStream(String fileName) throws IOException {
        Map<String, Long> result = Files.lines(Paths.get(fileName))
                .flatMap(s -> Arrays.stream(s.split("\\W")))
                .filter(s -> !s.matches("\\s*"))
                .collect(Collectors.groupingBy(s -> s, TreeMap::new, Collectors.counting()));

        return result;
        // result.forEach((key, value) -> System.out.println("Word: " + key + ",
        // Count:" + value));
    }

    public static Map<String, Long> countWordsStreamMatcher(String fileName) throws IOException {
        Pattern wordRE = Pattern.compile("\\w+");
        Map<String, Long> result = Files.lines(Paths.get(fileName))
                .flatMap(line -> {
                    Matcher matcher = wordRE.matcher(line);
                    List<String> list = new ArrayList();
                    matcher.reset();
                    while (matcher.find()) {
                        String word = matcher.group();
                        list.add(word);
                    }
                    return list.stream();
                })
                .filter(s -> !s.matches("\\s*"))
                .collect(Collectors.groupingBy(s -> s, TreeMap::new, Collectors.counting()));
        return result;
    }

    public static Map<String, Long> countWordsStreamCustomMatcher(String fileName) throws IOException {
        Pattern wordRE = Pattern.compile("\\w+");
        Map<String, Long> result = Files.lines(Paths.get(fileName))
                .flatMap(line -> {
                    Matcher matcher = wordRE.matcher(line);
                    return MatcherSpliterator.stream(matcher);
                })
                .map(mr -> mr.group())
                .filter(s -> !s.matches("\\s*"))
                .collect(Collectors.groupingBy(s -> s, TreeMap::new, Collectors.counting()));
        return result;
    }

    /**
     * Jdk 9+ only - for matcher.results()
     *
     * @param fileName
     * @return
     * @throws IOException
     */
    public static Map<String, Long> countWordsStreamMatcherJdk9(String fileName) throws IOException {
        Pattern wordRE = Pattern.compile("\\w+");
        Map<String, Long> result = Files.lines(Paths.get(fileName))
                //.parallel()
                .flatMap(line -> wordRE.matcher(line).results())
                .map(matchResult -> matchResult.group())
                .filter(s -> !s.matches("\\s*"))
                .collect(Collectors.groupingBy(s -> s, TreeMap::new, Collectors.counting()));
        //.collect(Collectors.groupingByConcurrent(s -> s, ConcurrentHashMap::new, Collectors.counting()));

        return result;
    }

    public static Map<String, Long> countWordsParallel(String fileName) throws IOException {
        Map<String, Long> result = Files.lines(Paths.get(fileName)).parallel()
                .flatMap(s -> Arrays.stream(s.split("\\W"))).filter(s -> !s.matches("\\s*"))
                .collect(Collectors.groupingBy(s -> s, TreeMap::new, Collectors.counting()));

        return result;
        // return new TreeMap<String, Long>(result);
        // result.forEach((key, value) -> System.out.println("Word: " + key + ",
        // Count:" + value));
    }

    public static Map<String, Long> countWordsParallelConcurrent(String fileName) throws IOException {
        Map<String, Long> result = Files.lines(Paths.get(fileName)).parallel()
                .flatMap(s -> Arrays.stream(s.split("\\W"))).filter(s -> !s.matches("\\s*"))
                .collect(Collectors.groupingByConcurrent(s -> s, ConcurrentHashMap::new, Collectors.counting()));

        return result;
        // return new TreeMap<String, Long>(result);
        // result.forEach((key, value) -> System.out.println("Word: " + key + ",
        // Count:" + value));
    }


}
