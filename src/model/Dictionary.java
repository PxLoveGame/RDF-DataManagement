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

        if (!dicoReverse.containsKey(v)){
            cptID++;
            try{
                this.dico.put(cptID,v);
                this.dicoReverse.put(v, cptID);

            }catch(Exception e) {
                e.printStackTrace();
            }
            return cptID;
        } else {
            return dicoReverse.get(v);
        }


    }

    public Map<Integer, String> getDico() {
        return dico;
    }

    public Map<String, Integer> getDicoReverse() {
        return dicoReverse;
    }
}
