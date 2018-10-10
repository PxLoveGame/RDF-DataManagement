package parsing;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.openrdf.model.Statement;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.RDFHandlerBase;

import static org.openrdf.rio.Rio.*;

public final class RDFRawParser {

	private static final String PATH_TO_RDF_FILE = "./res/dataset/100K.rdf";

	private static class RDFListener extends RDFHandlerBase {

		@Override
		public void startRDF() throws RDFHandlerException {
			System.out.println("start");
			super.startRDF();
		}

		@Override
		public void endRDF() throws RDFHandlerException {
			System.out.println("end");
			super.endRDF();
		}

		@Override
		public void handleStatement(Statement st) {
			System.out.println("\n" + st.getSubject() + "\t " + st.getPredicate() + "\t "
					+ st.getObject());
		}

	};

	public static void main(String args[]) throws FileNotFoundException {

		Reader reader = new FileReader(PATH_TO_RDF_FILE);

		RDFParser rdfParser = createParser(RDFFormat.RDFXML);
		rdfParser.setRDFHandler(new RDFListener());

		try {
			System.out.println("je passe par ici");
			rdfParser.parse(reader, "");

		} catch (Exception e) {
			System.err.println(e.getMessage());
		}

		try {
			System.out.println("je passe par ici");
			reader.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

}