package parsing;

import java.io.*;
import java.util.TreeMap;
import java.util.TreeSet;


import model.Dictionary;
import model.Index;
import org.openrdf.model.Statement;

import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

public final class RDFRawParser {

	private static final String PATH_TO_RDF_FILE = "./res/dataset/100K.rdfxml";

	private static Dictionary dico = new Dictionary();
    private static Index index = new Index();



	private static class RDFListener extends RDFHandlerBase {

		@Override
		public void handleStatement(Statement st) {
//			System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t "
//					+ st.getObject());


            int subjectID = dico.addToDicos(st.getSubject().toString());
            int predicateID = dico.addToDicos(st.getPredicate().toString());
            int objectID = dico.addToDicos(st.getObject().toString());


		}
	};


	public static void parse() throws  FileNotFoundException {

        Reader reader = new FileReader(PATH_TO_RDF_FILE);

        org.openrdf.rio.RDFParser rdfParser = Rio
                .createParser(RDFFormat.RDFXML);
        rdfParser.setRDFHandler(new RDFListener());
        try {
            rdfParser.parse(reader, "");
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        try {
            reader.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

//    public static void createIndex() throws FileNotFoundException, UnsupportedEncodingException {
//        PrintWriter writerPOS = new PrintWriter("out/POS.txt", "UTF-8");
//        writerPOS.print(pos);
//        writerPOS.close();
//
//        PrintWriter writerOPS = new PrintWriter("out/OPS.txt", "UTF-8");
//        writerOPS.println(ops);
//        writerOPS.close();
//
//    }

	public static void main(String args[]) throws FileNotFoundException, UnsupportedEncodingException {

        parse();
//        createIndex();

	}

}