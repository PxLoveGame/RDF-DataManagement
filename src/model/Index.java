package model;

import java.util.TreeMap;
import java.util.TreeSet;

public class Index {

    private TreeMap<Integer, TreeMap<Integer, TreeSet<Integer>>> ops;
    private TreeMap<Integer, TreeMap<Integer, TreeSet<Integer>>> pos;

    public Index(){
        this.ops = new TreeMap<>();
        this.pos = new TreeMap<>();
    }

    public void addToIndex(int sId, int pId, int oId){
        addToPOS(sId, pId, oId);
        addToOPS(sId, pId, oId);
    }

    private void addToPOS(int sId, int pId, int oId){

        TreeSet<Integer> s = new TreeSet<>();
        TreeMap<Integer, TreeSet<Integer>> os = new TreeMap<>();

        // Si P existe, recupérer os et s déjà existant.
        if(this.pos.containsKey(pId)){
            os = pos.get(pId);

            if(os.containsKey(oId)){
                s = os.get(oId);
            }
        }

        s.add(sId);
        os.put(oId, s);
        this.pos.put(pId,os);
    }


    private void addToOPS(int sId, int pId, int oId){

        TreeSet<Integer> s = ops.get(oId).get(pId);
        TreeMap<Integer, TreeSet<Integer>> ps = ops.get(oId);

        // Si P existe, récupérer ps et s déjà existant
        if(this.ops.containsKey(oId)){

            ps = ops.get(oId);

            if(ps.containsKey(pId)){
                s = ps.get(pId);
            }


        }

        s.add(sId);
        ps.put(pId, s);
        this.ops.put(oId,ps);
    }

    public TreeMap<Integer, TreeMap<Integer, TreeSet<Integer>>> getOps() {
        return ops;
    }

    public TreeMap<Integer, TreeMap<Integer, TreeSet<Integer>>> getPos() {
        return pos;
    }
}
