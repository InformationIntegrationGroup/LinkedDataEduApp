package edu.isi.serveletdemo;
import java.util.List;
import org.openrdf.OpenRDFException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.BindingSet;
import org.openrdf.query.QueryLanguage;

public class DemoPageServlet {

	DemoPageServlet(){
		//try {
   			//RepositoryConnection con = repo.getConnection();
  			 //try {
	 		 	//String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
	  			//TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SPARQL, queryString);

	  			//TupleQueryResult result = tupleQuery.evaluate();
	  			//try {
					//BindingSet bindingSet = result.next();
					//Value valueOfX = bindingSet.getValue("x");
					//Value valueOfY = bindingSet.getValue("y");

					// do something interesting with the values here...
	  			//}
	  			//finally {
	      			//result.close();
	  			//}
   			//}
   			//finally {
      			//con.close();
   			//}
		//}
		//catch (OpenRDFException e) {
   			// handle exception
		//}
	}
}
