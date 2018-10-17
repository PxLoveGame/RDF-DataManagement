package model;

import java.util.TreeMap;
import java.util.TreeSet;

public class Index {

    private TreeMap<Integer, TreeMap<Integer, TreeSet<Integer>>> ops;
    private TreeMap<Integer, TreeMap<Integer, TreeSet<Integer>>> pos;

    private TreeSet<Integer> s = new TreeSet<>();
    private TreeMap<Integer, TreeSet<Integer>> ps = new TreeMap<>();
    private TreeMap<Integer, TreeSet<Integer>> os = new TreeMap<>();



    public Index(){
        this.ops = new TreeMap<>();
        this.pos = new TreeMap<>();
    }
// Sinon ajouter S dans OS déja existant

//    public void addToIndex(int sId, int pId, int oId){
//
//        if(this.os.containsKey(oId) | this.ps.containsKey(pId)){
//            s.clear();
//        }
//
//
//        this.s.add(sId);
//
//        this.ps.put(pId,s);
//        this.os.put(oId,s);
//        this.ops.put(oId,ps);
//
//    }

    public void addToPOS(int sId, int pId, int oId){

        TreeSet<Integer> s = new TreeSet<>();
        TreeMap<Integer, TreeSet<Integer>> os = new TreeMap<>();

        // Si P n'existe pas, crée un nouveau tuple dans POS
        if(!this.pos.containsKey(pId)){

            s = new TreeSet<>();
            os = new TreeMap<>();

            this.s.add(sId);
            this.os.put(oId, s);
            this.pos.put(pId,os);
        }
        // Sinon ajouter OS dans POS déja existant
        else {
            // Si O n'existe pas, crée un nouveau tuple dans OS
                if(!this.os.containsKey(oId)){
                    s = new TreeSet<>();
                }

            this.s.add(sId);
            this.os.put(oId, s);
            this.pos.put(pId,os);
        }
    }


    public void addToOPS(int sId, int pId, int oId){

    }
}
