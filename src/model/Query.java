package model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Query {

//    private final String S;
    private ArrayList<String> variables = new ArrayList<>();
    private ArrayList<Triplet> triplets = new ArrayList<>();

    public Query(String... vars) {
        variables.addAll(Arrays.asList(vars));
    }

    public static void bindData(ArrayList<Query> queries, Dictionary dico) {
        Map<String, Integer> dicoReverse = dico.getDicoReverse();
        Integer sId, oId, pId;


        for (Query q : queries){
            for (Triplet t : q.triplets){

                pId = dicoReverse.get(t.p);
                oId = dicoReverse.get(t.o);

                if (pId == null || oId == null){
                    String errMessage = "Le triplet " + t + " fait mention d'éléments non recensés dans le dictionnaire";
                    if (pId == null) errMessage += " : pId";
                    if (oId == null) errMessage += " : oId";
                    throw new NullPointerException(errMessage);
                } else {
                    t.bindIndex(pId, oId);
                }

            }

        }

    }

    private void addTriplet(Triplet triplet) {
        triplets.add(triplet);
    }

    public String toString(){
        StringBuilder res = new StringBuilder();
        res.append("Matching ");
        for (String v : variables){
            res.append(v).append(' ');
        }
        res.append(" on {\n");
        for (Triplet t : triplets){
            res.append('\t').append(t.toString());
        }
        res.append('}');
        return res.toString();
    }

    public static ArrayList<Query> parseQueries(File inputFile) throws IOException {
        ArrayList<Query> queries = new ArrayList<>();

        String source = readFile(inputFile);

        Pattern fullQueryPattern = Pattern.compile("SELECT (\\S+) WHERE \\{((?:\\n\\s\\1 \\S+ \\S+ \\.)+)? ?\\n?}");
//        Pattern fullQueryPattern = Pattern.compile("SELECT (\\S+) WHERE \\{((?:\\n\\s\\1 \\S+ \\S+ \\.)+)\n?}");
        Matcher fullQueryMatcher = fullQueryPattern.matcher(source);

        Pattern subQueryPattern = Pattern.compile("\\s(\\S+) <(\\S+)> <(\\S+)> \\.");
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

    public ArrayList<Triplet> getTriplets() {
        return triplets;
    }

    static class Triplet {

        private String s, p, o;
        private Integer sId, oId, pId;

        Triplet(String subject, String property, String object){
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

        public Integer oId(){
            return oId;
        }

        public Integer pId(){
            return pId;
        }

        public Integer sId(){
            return sId;
        }

        public void bindIndex(/*Integer s, */Integer p, Integer o){
//            sId = s;
            oId = o;
            pId = p;
        }

        public String toString(){
            if (pId != null && oId != null){
                return new StringBuilder().append(s).append("(").append(sId).append(") ").append(p).append("(").append(pId).append(") ").append(o).append("(").append(oId).append(")").toString();
            }else {
                return new StringBuilder().append(s).append(" ").append(p).append(" ").append(o).toString();
            }

        }
    }
}
