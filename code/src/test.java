import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {

    public static void main(String[] args) {
        HashMap<String, List<String>> columns = new HashMap<>();
        columns.put("SQL", new ArrayList<>());
        columns.get("SQL").add("FirstName");
        columns.get("SQL").add("LastName");

        columns.put("CSV", new ArrayList<>());
        columns.get("CSV").add("first_name");
        columns.get("CSV").add("last_name");

        columns.put("XLS", new ArrayList<>());
        columns.get("XLS").add("first_name");
        columns.get("XLS").add("last_name");

        columns.put("TAR", new ArrayList<>());
        columns.get("TAR").add("FirstName");
        columns.get("TAR").add("LastName");

        String dbURL = "jdbc:sqlserver://localhost\\SQLSERVER19:1433;databaseName=AdventureWorksDW2016;encrypt=false;trustServerCertificate=false";
        String user = "sa";
        String pass = "JBoussouf";
        Connection conn;
        SQLServerExtractor sqlExtractor;
        ArrayList<Thread> threads = new ArrayList<>();
        int sizeSqlServer;
        ArrayList<SQLServerExtractor> sql = new ArrayList<>();
        HashMap<String, ArrayList<Map<String, List<String>>>> data = new HashMap<>();

        
        // Extract data from different sources :
        try {
            // From SQLSERVER
            conn = DriverManager.getConnection(dbURL, user, pass);
            if (conn != null) {
                System.out.println("Connected!");
                sqlExtractor = new SQLServerExtractor("DimEmployee", conn, columns, 1, 2);
                sizeSqlServer = sqlExtractor.serverSize();
                
                for(int i=0; i<sizeSqlServer; i=i+69){
                    sqlExtractor = new SQLServerExtractor("DimEmployee", conn, columns, i, 69);
                    Thread th = new Thread(sqlExtractor);//(i==0)?i:i+1
                    //th.start();
                    sql.add(sqlExtractor);
                    threads.add(th);
                }
                
                CSVFileExtractor csvExtractor = new CSVFileExtractor(
                    "C:\\Users\\Administrateur\\Desktop\\tab1.csv", columns);
                threads.add(new Thread(csvExtractor));

                ExcelFileExtractor xlsExtractor = new ExcelFileExtractor(
                    "C:\\Users\\Administrateur\\Desktop\\tab1.xls", columns);
                threads.add(new Thread(xlsExtractor));




                for(Thread th: threads){
                    th.start();
                }
                for(Thread th: threads){
                    th.join();
                }

                threads.clear();

//                for (SQLServerExtractor s: sql){
//                    System.out.println(s.getStringMap().get("FirstName").size());
//                }

                //System.out.println(sqlExtractor.getStringMap().get("FirstName").size());

                data.put("CSV", new ArrayList<>());
                data.get("CSV").add(csvExtractor.getStringMap());

                data.put("XLS", new ArrayList<>());
                data.get("XLS").add(xlsExtractor.getStringMap());

                data.put("SQL", new ArrayList<>());
                for(SQLServerExtractor s: sql){
                    data.get("SQL").add(s.getStringMap());
                }

                for (String key: data.keySet()){
                    //System.out.println(data.get(key));
                    for(Map<String, List<String>> el: data.get(key)){

                        DataWarehouse dw = new DataWarehouse(el, columns, key, "Person");
                        threads.add(new Thread(dw));
                    }

                }
                long startTime = System.currentTimeMillis();
                for(Thread th:threads){
                    th.start();
                }
                for(Thread th:threads){
                    th.join();
                }

                long endTime = System.currentTimeMillis(); // Capture end time
                long elapsedTime = endTime - startTime;

                System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
            }

            // From CSV FILE
            

            // From XLS FILE
            

            

        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        
    }
}