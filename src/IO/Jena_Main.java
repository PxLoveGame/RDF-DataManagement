package IO;

import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.util.FileManager;
import model.*;
import model.Query;
import parsing.RDFRawParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static IO.Logger.log;

public class Jena_Main {

    private static Boolean WORKLOAD_TIME = false;
    private static Boolean VERBOSE = false;
    private static Boolean EXPORT_RESULTS = false;
    private static Boolean EXPORT_STATS = false;
    private static File OUTPUT_DIRECTORY;
    private static File QUERIES_FILE;
    private static File DATA_FILE;

    private static int completeness;
    private static int soundness;


    public static void main(String[] args) throws IOException {

        if (args.length == 0){
            printUsage();
        }

        /*
         java -jar rdfstar
         -queries "res/queries" -data "res/dataset/500K.rdfxml" -output "out/parsed" -verbose -export_results -export_stats -workload_time
         */

        readParams(args); // loads and checks the arguments


        log("Input data file : " + DATA_FILE.getPath());
        log("Input queries file : " + QUERIES_FILE.getPath());
        log("Output directory: " + OUTPUT_DIRECTORY.getPath());
        log("------------------------------------");


        ArrayList<Query> queries = parseQueries(QUERIES_FILE);

        RDFRawParser.parse(DATA_FILE);

        Dictionary dico = RDFRawParser.getDico();
        Index index = RDFRawParser.getIndex();

        Query.bindData(queries, dico);

        Solver.solveQueries(queries, index);

        jenaExecution(queries, dico);

        if(EXPORT_STATS){
            exportStats(queries,dico);
        }
        if(EXPORT_RESULTS) {
            Main.exportResults(queries, dico);
        }

        float completeness_percent;
        if(completeness != 0){
            completeness_percent = (completeness * 100)/ queries.size();
        }
        else{
            completeness_percent = 0;
        }

        float soundness_percent;
        if(soundness != 0){
            soundness_percent = (soundness * 100) / queries.size();
        }
        else{
            soundness_percent = 0;
        }



    }

    private static int jenaExecution(ArrayList<Query> queries, Dictionary dico){

        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(DATA_FILE.getPath());
        model.read(in, null);


        for (Query q : queries){
            String source = q.getSource();

            ResultSet resultSet = runJenaQuery( source, model );

            List<String> solutions = new ArrayList<>();



            while (resultSet.hasNext()){
                QuerySolution solution = resultSet.next();
                RDFNode soluces = solution.get("?v0");
                solutions.add(soluces.toString());
            }

            compareJenaResults(solutions, q, dico);


        }
        return 0;
    }

    private static void compareJenaResults(List<String> solutions, Query q, Dictionary dico) {
        boolean safe = true;

        for ( int resultId : q.getResults()){
            String resultStr = dico.getDico().get(resultId);
            if( !solutions.contains(resultStr) ){
                // on a une solution que Jena n'a pas
                safe = false;

            }
        }

        if(safe){
            soundness++;
        }

        safe = true;

        for (String solution : solutions){
            Integer resId = dico.getDicoReverse().get(solution);
            if (resId == null){
                // Jena a une solution qu'on n'a pas
                safe = false;
            }
        }

        if(safe){
            completeness++;
        }




    }

    private static com.hp.hpl.jena.query.ResultSet runJenaQuery(String source, Model model) {
        com.hp.hpl.jena.query.Query query = QueryFactory.create(source);
        QueryExecution queryExecution = QueryExecutionFactory.create(query, model);
        com.hp.hpl.jena.query.ResultSet resultSet = queryExecution.execSelect();
//        ResultSetFormatter.out(System.out, resultSet, query);

        return resultSet;
    }

    private static void printUsage() {
        System.out.println("Usage example: ");
        System.out.println("java -jar rdfstar");
        System.out.println("\t -queries \"res/queries\" -data \"res/dataset\" -output \"out\" -verbose -export_results -export_stats -workload_time");
    }

    private static ArrayList<Query> parseQueries(File queriesFile) throws IOException {
        ArrayList<Query> queries = new ArrayList<>();

        ArrayList<File> files = new ArrayList<>();

        if (queriesFile.isDirectory()){
            files.addAll(Arrays.asList(queriesFile.listFiles()));
        }else {
            files.add(queriesFile);
        }


        for (File file : files){

            if( file.isFile() ){
                log("Lecture du fichier " + file.getCanonicalPath());
                queries.addAll(Query.parseQueries(file));
            }else {
                log("Ouverture du sous-dossier " + file.getCanonicalPath());
                for (File subFile : file.listFiles()){
                    queries.addAll(parseQueries(subFile));
                }
            }

        }

        return queries;
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
//                    VERBOSE = Boolean.valueOf(params.get(key));
                    Logger.setLogging( Boolean.valueOf(params.get(key)) );
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

        if (OUTPUT_DIRECTORY == null && (WORKLOAD_TIME || EXPORT_RESULTS || EXPORT_STATS)){
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
        if(OUTPUT_DIRECTORY != null){
            checkDirectories(); // Vérifie l'existance des fichiers d'entrée, créé le dossier de sortie
        }


    }

    private static void exportStats(ArrayList<Query> queries, Dictionary dico) throws IOException {
        FileWriter stats = new FileWriter(OUTPUT_DIRECTORY.getPath() + "/" + "stats.csv");
        stats.append("Nom;Correspondances;Selectivite(%)" + "\n");
        for(Query q : queries){
            for(Triplet triplet : q.getTriplets()){
                stats.append(triplet.toString()).append(";").append(String.valueOf(triplet.getSelectivity())).append(";");

                float selectivity = ((float) triplet.getSelectivity() / dico.getDico().size()) * 100 ;
                stats.append(String.valueOf(selectivity) + "\n");
            }
        }

        stats.close();
    }



}
