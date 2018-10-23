import model.Query;
import parsing.RDFRawParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    private final static Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static Boolean WORKLOAD_TIME = false;
    public static Boolean VERBOSE = false;
    public static Boolean EXPORT_RESULTS = false;
    public static Boolean EXPORT_STATS = false;
    public static File OUTPUT_DIRECTORY;
    public static File QUERIES_FILE;
    public static File DATA_FILE;
    public static ArrayList<Query> QUERIES = new ArrayList<>();


    public static void main(String[] args) throws IOException {

        /**
         java -jar rdfstar
         -queries "/chemin/vers/requetes" -data "/chemin/vers/donnees" -output "/chemin/vers/dossier/sortie" -verbose -export_results -export_stats -workload_time
         -queries "res/queries/Q_1_likes.queryset" -data "res/dataset/100K.rdfxml" -output "out" -verbose -export_results -export_stats -workload_time
         */
        LOGGER.setLevel(Level.FINE);
        LOGGER.addHandler(new ConsoleHandler());
        LOGGER.addHandler(new FileHandler());

        readParams(args); // loads and checks the arguments


        System.out.println("Input data file : " + DATA_FILE.getPath());
        System.out.println("Input queries file : " + QUERIES_FILE.getPath());
        System.out.println("Output directory: " + OUTPUT_DIRECTORY.getPath());

        StopWatch queriesTimer = new StopWatch();
        queriesTimer.start();
        ArrayList<Query> queries = Query.parseQueries(QUERIES_FILE);
        queriesTimer.stop();
        System.out.println(queries.size() + " queries found in " + queriesTimer);

        StopWatch dataTimer = new StopWatch();
        dataTimer.start();
        RDFRawParser.parse(DATA_FILE);
        dataTimer.stop();
        System.out.println("Input data parsed in " + dataTimer);

    }

    /**
     * Checks if input files exists
     * Creates the output directories
     */
    private static void checkDirectories() throws IOException {

        if(!OUTPUT_DIRECTORY.exists()){
            System.out.println("Creating output directory " + OUTPUT_DIRECTORY);
            if (!OUTPUT_DIRECTORY.mkdirs()){
                throw new IOException("Couldn't create output directory");
            }
        }

        if (!QUERIES_FILE.exists()){
            throw new FileNotFoundException("Input file is missing : " + QUERIES_FILE);
        }else if( !DATA_FILE.exists() ){
            throw new FileNotFoundException("Input file is missing : " + DATA_FILE);
        }
    }

    private static void loadParams(HashMap<String, String> params) {

        for( String key : params.keySet() ) {
            switch (key) {
                case "queries":
                    QUERIES_FILE = new File(params.get(key));
                    break;
                case "data":
                    DATA_FILE = new File(params.get(key));
                    break;
                case "output":
                    OUTPUT_DIRECTORY = new File(params.get(key));
                    break;
                case "verbose":
                    VERBOSE = Boolean.valueOf(params.get(key));
                    break;
                case "export_results":
                    EXPORT_RESULTS = Boolean.valueOf(params.get(key));
                    break;
                case "export_stats":
                    EXPORT_STATS = Boolean.valueOf(params.get(key));
                    break;
                case "workload_time":
                    WORKLOAD_TIME = Boolean.valueOf(params.get(key));
                    break;
            }
        }

        if (OUTPUT_DIRECTORY == null){
            throw new IllegalArgumentException("Missing argument : " + "output");
        }else if (QUERIES_FILE == null){
            throw new IllegalArgumentException("Missing argument : " + "queries");
        } if (DATA_FILE == null){
            throw new IllegalArgumentException("Missing argument : " + "data");
        }

        if (!VERBOSE ){
            LOGGER.setLevel(Level.SEVERE);
        }

    }

    private static void readParams(String[] args) throws IOException {
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

        loadParams(params); // Extrait les arguments du programme
        checkDirectories(); // Vérifie l'existance des fichiers d'entrée, créé le dossier de sortie

    }

}
