package model;

public class Triplet {

    private String s, p, o;
    private Integer sId, oId, pId;
    private int selectivity;

    Triplet(String subject, String property, String object){
        s = subject;
        p = property;
        o = object;
    }

    public String s(){
        return s;
    }

    public String p(){
        return p;
    }

    public String o(){
        return o;
    }

    public Integer oId(){
        return oId;
    }

    public Integer pId(){
        return pId;
    }

    public Integer sId(){
        return sId;
    }


    public int getSelectivity() {
        return selectivity;
    }

    public void setSelectivity(int selectivity) {
        this.selectivity = selectivity;
    }

    public void bindIndex(/*Integer s, */Integer p, Integer o){
//            sId = s;
        oId = o;
        pId = p;
    }

    public String toString(){
        if (pId != null && oId != null){
            return new StringBuilder().append(s).append("(").append(sId).append(") ").append(p).append("(").append(pId).append(") ").append(o).append("(").append(oId).append(")").toString();
        }else {
            return new StringBuilder().append(s).append(" ").append(p).append(" ").append(o).toString();
        }

    }
}

