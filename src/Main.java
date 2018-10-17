import model.Query;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static Boolean WORKLOAD_TIME = false;
    public static Boolean VERBOSE = false;
    public static Boolean EXPORT_RESULTS = false;
    public static Boolean EXPORT_STATS = false;
    public static File OUTPUT_DIRECTORY;
    public static File QUERIES_FILE;
    public static File DATA_SOURCES_FILE;
    public static ArrayList<Query> QUERIES = new ArrayList<>();


    public static void main(String[] args){

        /**
         * java -jar rdfstar
         * -queries "/chemin/vers/requetes"
         * -data "/chemin/vers/donnees"
         * -output "/chemin/vers/dossier/sortie"
         * -verbose
         * -export_results
         * -export_stats
         * -workload_time
         */

        HashMap<String, String> params = readParams(args);
        loadParams(params);

    }

    private static void loadParams(HashMap<String, String> params) {

        HashMap<String, Boolean> bools = new HashMap<>();
        bools.put("verbose", VERBOSE);
        bools.put("export_results", EXPORT_RESULTS);
        bools.put("export_stats", EXPORT_STATS);

        for( String key : params.keySet() ) {
            if (key.equals("queries")){
                QUERIES_FILE = new File( params.get(key) );
            }else if (key.equals("data")){
                DATA_SOURCES_FILE = new File( params.get(key) );
            }else if (key.equals("output")){
                OUTPUT_DIRECTORY = new File( params.get(key) );
            }else if (key.equals("verbose")){
                VERBOSE = Boolean.valueOf(params.get(key));
            }else if (key.equals("export_results")){
                EXPORT_RESULTS = Boolean.valueOf(params.get(key));
            }else if (key.equals("export_stats")){
                EXPORT_STATS = Boolean.valueOf(params.get(key));
            }else if (key.equals("workload_time")){
                WORKLOAD_TIME = Boolean.valueOf(params.get(key));
            }
        }

    }

    private static HashMap<String, String> readParams(String[] args) {
        HashMap<String, String> params = new HashMap<>();


        for (int index = 0; index < args.length; index++){
            String arg = args[index];
            if (arg.startsWith("-")){
                arg = arg.substring(1);

                if ( index+1 < args.length && !args[index+1].startsWith("-") ){
                    params.put(arg, args[index+1]);
                    index++;
                } else {
                    params.put(arg, "true");
                }
            }
        }
        return params;

    }

}
