package parsing;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

public final class RDFRawParser {

	private static final String PATH_TO_RDF_FILE = "./res/dataset/100K.rdfxml";
	private static int  cptID = 0;
	private static Map<Integer, Value> dicoValues = new HashMap<Integer, Value>();
	private static Map<Integer, URI> dicoPredicates = new HashMap<Integer, URI>();


	private static class RDFListener extends RDFHandlerBase {

		@Override
		public void handleStatement(Statement st) {
//			System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t "
//					+ st.getObject());


            // ToDo: Voir avec le prof si il n'est pas plus judicieux de faire une seule HashMap <Integer, String>...
			cptID++;
			dicoValues.put(cptID, st.getSubject());
			cptID++;
            dicoPredicates.put(cptID,st.getPredicate());
			cptID++;
            dicoValues.put(cptID,st.getObject());

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

        }

        try {
            reader.close();
        } catch (IOException e) {
        }
    }

	public static void main(String args[]) throws FileNotFoundException {

        parse();

        System.out.println("Nombre de valeurs (sujets ou objets) : " + dicoValues.keySet().size());
        System.out.println("exemple : " + dicoValues.get(1));
        System.out.println("exemple : " + dicoValues.get(3));
        System.out.println("Nombre de relations : " + dicoPredicates.keySet().size());
        System.out.println("exemple : " + dicoPredicates.get(2));
        System.out.println("exemple : " + dicoPredicates.get(5));
	}

}