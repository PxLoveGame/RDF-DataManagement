package model;


import java.util.Map;
import java.util.TreeMap;

public class Dictionary {

    private static  int cptID = 0;
    private Map<Integer, String> dico;
    private Map<String, Integer> dicoReverse;

    public Dictionary(){
        this.dico = new TreeMap<>();
        this.dicoReverse = new TreeMap<>();
    }


    public int addToDicos(String v){
        cptID++;
        try{
//            System.out.println("Putting " + cptID + " => " + v + " into dico");
//            System.out.println("Putting " + v + " => " + cptID + " into dicoreverse");
            this.dico.put(cptID,v);
            this.dicoReverse.put(v, cptID);
//            System.out.println(dicoReverse.keySet());
//            System.out.println("------------------------------------------");


        }catch(Exception e) {
            System.out.println(e);
        }
        return cptID;
    }

    public Map<Integer, String> getDico() {
        return dico;
    }

    public Map<String, Integer> getDicoReverse() {
        return dicoReverse;
    }
}
