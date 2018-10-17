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

        TreeSet<Integer> s = pos.get(pId).get(oId);
        TreeMap<Integer, TreeSet<Integer>> os = pos.get(pId);

        // Si P n'existe pas, crée un nouveau tuple dans POS
        if(!this.pos.containsKey(pId)){

            s = new TreeSet<>();
            os = new TreeMap<>();
        }
        // Sinon ajouter OS dans POS déja existant
        else {
            // Si O n'existe pas, crée un nouveau tuple dans OS
                if(!os.containsKey(oId)){
                    s = new TreeSet<>();
                }
        }

        s.add(sId);
        os.put(oId, s);
        this.pos.put(pId,os);
    }


    private void addToOPS(int sId, int pId, int oId){

        TreeSet<Integer> s = ops.get(oId).get(pId);
        TreeMap<Integer, TreeSet<Integer>> ps = ops.get(oId);

        // Si P n'existe pas, crée un nouveau tuple dans POS
        if(!this.ops.containsKey(oId)){
            s = new TreeSet<>();
            ps = new TreeMap<>();
        }
        // Sinon ajouter OS dans POS déja existant
        else {
            // Si O n'existe pas, crée un nouveau tuple dans OS
            if(!ps.containsKey(pId)){
                s = new TreeSet<>();
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
