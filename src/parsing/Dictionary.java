package parsing;

import org.openrdf.model.URI;
import org.openrdf.model.Value;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Dictionary {

    private static  int cptID = 0;
    private Map<Integer, String> dico;
    private Map<String, Integer> dicoReverse;

    Dictionary(){
        this.dico = new TreeMap<Integer, String>();
        this.dicoReverse = new TreeMap<String, Integer>();
    }


    public void addToDicos(String v){
        cptID++;
        try{
            this.dico.put(cptID,v);
            this.dicoReverse.put(v, cptID);
        }catch(Exception e) {
            System.out.println(e);
        }

    }

    public Map<Integer, String> getDico() {
        return dico;
    }

    public Map<String, Integer> getDicoReverse() {
        return dicoReverse;
    }
}
