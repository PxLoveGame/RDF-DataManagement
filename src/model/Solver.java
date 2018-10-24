package model;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

public class Solver {


    public static void solveQueries(ArrayList<Query> queries, Index index){

        for (Query q : queries){
            q.setResults(solveQuery(q, index));
        }

    }

    private static TreeSet<Integer> solveQuery(Query query, Index index) {

        boolean firstInsert = true;
        TreeSet<Integer> matches = new TreeSet<>();

        for (Triplet t : query.getTriplets()){
            TreeSet<Integer> subMatches = solveTriplet(t, index);

            if (firstInsert){
                firstInsert = false;
                matches.addAll(subMatches);
            }else if(matches.isEmpty()){
                break;
            } else  {
                matches.retainAll(subMatches);
            }
        }

        return matches;

    }

    private static TreeSet<Integer> solveTriplet(Triplet triplet, Index index) {
        TreeMap<Integer, TreeSet<Integer>> os = index.getPos().get(triplet.pId());

        TreeSet<Integer> s = os.get(triplet.oId());

        if (s == null){
            return new TreeSet<>();
        }

        int selectivity = s.size();
        triplet.setSelectivity(selectivity);

        return s;
    }


}
