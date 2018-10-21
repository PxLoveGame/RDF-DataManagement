package model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Query {

    private final String S;
    private ArrayList<Triplet> triplets = new ArrayList<>();

    public Query(String s) {
        S = s;
    }

    private void addTriplet(Triplet triplet) {
        triplets.add(triplet);
    }

    public String toString(){
        StringBuilder res = new StringBuilder();
        res.append("Matching " + S + " on {\n");
        for (Triplet t : triplets){
            res.append('\t').append(t.toString());
        }
        res.append('}');
        return res.toString();
    }

    public static ArrayList<Query> parseQueries(File inputFile) throws IOException {
        ArrayList<Query> queries = new ArrayList<>();

        String source = readFile(inputFile);

        Pattern fullQueryPattern = Pattern.compile("SELECT (\\S+) WHERE \\{((?:\\n\\s\\1 \\S+ \\S+ \\.)+)\n?}");
        Matcher fullQueryMatcher = fullQueryPattern.matcher(source);

        Pattern subQueryPattern = Pattern.compile("\\s(\\S+) (\\S+) (\\S+) \\.");
        Matcher subQueryMatcher;

        String subQueries, s, p, o;

        while (fullQueryMatcher.find()){
            s = fullQueryMatcher.group(1); // ?v0
            subQueries = fullQueryMatcher.group(2); // ?v0 <...> <...> .\n X fois
            subQueryMatcher = subQueryPattern.matcher(subQueries);
            Query query = new Query(s);

            while (subQueryMatcher.find()){
                s = subQueryMatcher.group(1);
                p = subQueryMatcher.group(2);
                o = subQueryMatcher.group(3);

                Triplet triplet = new Triplet(s, p, o);
                query.addTriplet(triplet);
            }

            queries.add(query);
        }

        return queries;
    }

    public static String readFile(File inputFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        Stream<String> stream =
                Files.lines( Paths.get(inputFile.toURI()), StandardCharsets.UTF_8);
        stream.forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }

    static class Triplet {

        private String s, p, o;

        public Triplet(String subject, String property, String object){
            s = subject;
            p = property;
            o = object;
        }

        public String s(){
            return s;
        }

        public String p(){
            return p;
        }

        public String o(){
            return o;
        }

        public String toString(){
            return s + " " + p + " " + o;
        }
    }
}
