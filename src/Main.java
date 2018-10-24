import model.*;
import parsing.RDFRawParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    private static Boolean WORKLOAD_TIME = false;
    private static Boolean VERBOSE = false;
    private static Boolean EXPORT_RESULTS = false;
    private static Boolean EXPORT_STATS = false;
    private static File OUTPUT_DIRECTORY;
    private static File QUERIES_FILE;
    private static File DATA_FILE;


    public static void main(String[] args) throws IOException {

        /*
         java -jar rdfstar
         -queries "/chemin/vers/requetes" -data "/chemin/vers/donnees" -output "/chemin/vers/dossier/sortie" -verbose -export_results -export_stats -workload_time
         -queries "res/queries/Q_1_likes.queryset" -data "res/dataset/100K.rdfxml" -output "out/parsed" -verbose -export_results -export_stats -workload_time
         */

        readParams(args); // loads and checks the arguments
        StopWatch totalTimer = new StopWatch("Temps total d'execution");
        totalTimer.start();


        log("Input data file : " + DATA_FILE.getPath());
        log("Input queries file : " + QUERIES_FILE.getPath());
        log("Output directory: " + OUTPUT_DIRECTORY.getPath());

        StopWatch queriesTimer = new StopWatch("Lecture des requêtes");
        queriesTimer.start();
        ArrayList<Query> queries = Query.parseQueries(QUERIES_FILE);
        queriesTimer.stop();
        log(queries.size() + " queries found in " + queriesTimer);

        StopWatch dataTimer = new StopWatch("Lecture du jeu de données");
        dataTimer.start();
        RDFRawParser.parse(DATA_FILE);
        dataTimer.stop();
        log("Input data parsed in " + dataTimer);

        Dictionary dico = RDFRawParser.getDico();
        Index index = RDFRawParser.getIndex();

        Query.bindData(queries, dico);

        StopWatch solveTimer = new StopWatch("Traitement des requêtes");
        solveTimer.start();
        Solver.solveQueries(queries, index);
        solveTimer.stop();

        log("Queries processed in " + solveTimer);

        totalTimer.stop();

        log("Total execution time : " + totalTimer);

        if(EXPORT_STATS){
            exportStats(queries,dico);
        }
        if(EXPORT_RESULTS) {
            exportResults(queries, dico);
        }
        if (WORKLOAD_TIME){
            exportWorkloadTime(queriesTimer, dataTimer, solveTimer , totalTimer );
        }



    }

    /**
     * Checks if input files exists
     * Creates the output directories
     */
    private static void checkDirectories() throws IOException {

        if(!OUTPUT_DIRECTORY.exists()){
            log("Creating output directory " + OUTPUT_DIRECTORY);
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

    private static void exportStats(ArrayList<Query> queries, Dictionary dico) throws IOException {
        FileWriter stats = new FileWriter(OUTPUT_DIRECTORY.getPath() + "/" + "stats.csv");
        stats.append("Nom;Correspondances;Selectivité(%)" + "\n");
        for(Query q : queries){
            for(Triplet triplet : q.getTriplets()){
                stats.append(triplet.toString()).append(";");
                stats.append(String.valueOf(triplet.getSelectivity()) + ";");

                float selectivity = ((float) triplet.getSelectivity() / dico.getDico().size()) * 100 ;
                stats.append(String.valueOf(selectivity) + "\n");
            }
        }

        stats.close();
    }

    private static void exportResults(ArrayList<Query> queries, Dictionary dico) throws IOException {

        FileWriter result = new FileWriter(OUTPUT_DIRECTORY.getPath() + "/" + "result.csv");
        result.append("Request;Results" + "\n");

        for(Query q : queries){
            result.append(q.toString()).append(';');
            for ( Integer resId : q.getResults() ){
                result.append( dico.getDico().get(resId) ).append(';');
            }
            result.append('\n');
        }
        result.close();
    }

    private static void exportWorkloadTime(StopWatch... timers) throws IOException {
        FileWriter times = new FileWriter(OUTPUT_DIRECTORY.getPath() + "/" + "workload_time.csv");

        times.append("Tâche;Durée" + "\n");

        for (StopWatch timer : timers){
            System.out.println("timer " + timer.getName());
            times.append(timer.getName());
            times.append(";");
            times.append(timer.toString());
            times.append("\n");

        }
        times.close();

    }
    
    private static void log(String msg){
        if (VERBOSE){
            System.out.println(msg);
        }
    }

}
