package model;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class Solver {


    public static void solveQueries(ArrayList<Query> queries, Index index){

        for (Query q : queries){
            solveQuery(q, index);
        }
//        solveQuery(queries.get(0), index);


    }

    private static void solveQuery(Query query, Index index) {
        System.out.println("================================\nSolving query \n\t" + query);

        TreeSet<Integer> matches = new TreeSet<>();

        for (Query.Triplet t : query.getTriplets()){
            TreeSet<Integer> subMatches = solveTriplet(t, index);
            System.out.println("SubMatches : " + subMatches);

            if (matches.isEmpty()){
                matches.addAll(subMatches);
            }else {
                matches.retainAll(subMatches);
            }

            System.out.println("Post filter : " + matches);


        }

    }

    private static TreeSet<Integer> solveTriplet(Query.Triplet triplet, Index index) {

//        System.out.println("Solving subQuery " + triplet);

        TreeMap<Integer, TreeSet<Integer>> os = index.getPos().get(triplet.pId());

        TreeSet<Integer> s = os.get(triplet.oId());

        if (s == null){
            return new TreeSet<>();
        }

        return s;
    }


}
