package model;

public class Triplet {

    private String s, p, o;
    private Integer oId, pId;
    private int selectivity;

    Triplet(String subject, String property, String object){
        s = subject;
        p = property;
        o = object;
    }


    String p(){
        return p;
    }

    String o(){
        return o;
    }

    Integer oId(){
        return oId;
    }

    Integer pId(){
        return pId;
    }



    public int getSelectivity() {
        return selectivity;
    }

    void setSelectivity(int selectivity) {
        this.selectivity = selectivity;
    }

    void bindIndex(Integer p, Integer o){
        oId = o;
        pId = p;
    }

    public String toString(){
            return s + " " + p + " " + o;
    }
}

