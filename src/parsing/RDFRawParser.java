package parsing;

import model.Dictionary;
import model.Index;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.RDFHandlerBase;

import java.io.*;

public final class RDFRawParser {

//	private static final String PATH_TO_RDF_FILE = "./res/dataset/100K.rdfxml";

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

            index.addToIndex(subjectID,predicateID,objectID);
//            System.out.println("Adding index " + subjectID);

//            System.out.print(index.getPos().get(predicateID).toString());
//            System.out.println(index.getPos().get(predicateID).get(objectID).toString());

        }
	}


	public static void parse(File dataFile) throws  FileNotFoundException {

        Reader reader = new FileReader(dataFile);

        org.openrdf.rio.RDFParser rdfParser = Rio
                .createParser(RDFFormat.RDFXML);
        rdfParser.setRDFHandler(new RDFListener());
        try {
            rdfParser.parse(reader, "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}