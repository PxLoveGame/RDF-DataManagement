package model;

import IO.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


public class Query {


    private ArrayList<String> variables = new ArrayList<>();
    private ArrayList<Triplet> triplets = new ArrayList<>();
    private TreeSet<Integer> results = new TreeSet<>();
    private boolean notBound = false;

    private Query(String... vars) {
        variables.addAll(Arrays.asList(vars));
    }

    public static void bindData(ArrayList<Query> queries, Dictionary dico) {
        Map<String, Integer> dicoReverse = dico.getDicoReverse();
        Integer oId, pId;

//        ArrayList<Query> missingBindings = new ArrayList<>();


        for (Query q : queries){
            for (Triplet t : q.triplets){

                pId = dicoReverse.get(t.p());
                oId = dicoReverse.get(t.o());

                if (pId == null || oId == null){
                    if (Logger.logEnabled()){
                        String errMessage = "\n\tLe triplet \"" + t + "\" fait mention d'éléments non recensés dans le dictionnaire : ";
                        if (pId == null) errMessage += "\n\t\tpId <" + t.p() + ">";
                        if (oId == null) errMessage += "\n\t\toId <" + t.o() + ">";
                        Logger.logError("Erreur de correspondance de la requête dans l'index : " + errMessage);
                    }

                    q.flagNotBound();
                    break;

                } else {
                    t.bindIndex(pId, oId);
                }

            }

       }

    }

    private void flagNotBound() {
        notBound = true;
    }

    private void addTriplet(Triplet triplet) {
        triplets.add(triplet);
    }

    public String toString(){

        StringBuilder res = new StringBuilder();
        res.append("SELECT ");
        for (String v : variables){
            res.append(v).append(' ');
        }
        res.append("WHERE {\n");
        for (Triplet t : triplets){
            res.append('\t').append(t.toString()).append('\n');
        }
        res.append('}');
        return res.toString();
    }

    public static ArrayList<Query> parseQueries(File inputFile) throws IOException {
        ArrayList<Query> queries = new ArrayList<>();

        String source = readFile(inputFile);

        Pattern fullQueryPattern = Pattern.compile("SELECT (\\S+) WHERE \\{((?:\\n\\t?\\s\\1 \\S+ \\S+ ?> ?\\.?)+)? ?\\.?\\n*}");

        Matcher fullQueryMatcher = fullQueryPattern.matcher(source);

        Pattern subQueryPattern = Pattern.compile("\\s(\\S+) <(\\S+)> <(\\S+)> ?\\.?");
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

    private static String readFile(File inputFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        Stream<String> stream =
                Files.lines( Paths.get(inputFile.toURI()), StandardCharsets.UTF_8);
        stream.forEach(s -> sb.append(s).append("\n"));
        return sb.toString();
    }

    public ArrayList<Triplet> getTriplets() {
        return triplets;
    }

    void setResults(TreeSet<Integer> res) {
        results = res;
    }

    public TreeSet<Integer> getResults(){
        return results;
    }

    boolean isNotBound() {
        return notBound;
    }
}
