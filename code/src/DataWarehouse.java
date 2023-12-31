import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataWarehouse implements Runnable{
    private String dbURL = "jdbc:sqlserver://localhost\\SQLSERVER19:1433;databaseName=Hello;encrypt=false;trustServerCertificate=false";
    private String user = "sa";
    private String pass = "JBoussouf";
    private Connection conn;

    private Map<String, List<String>> stringMap;
    private HashMap<String, List<String>> feat_map;
    private String source;
    private String tableName;

    public DataWarehouse(Map<String, List<String>> stringMap, HashMap<String, List<String>> feat_map, String source, String tableName) {
        this.stringMap = stringMap;
        this.feat_map = feat_map;
        this.source = source;
        this.tableName = tableName;
    }

    public void loadPersonData(){
        List<String> columns = feat_map.get("TAR");
        List<String> src = feat_map.get(source);

        String cls="";
        for (int i=0; i<columns.size(); i++){
            if (i==columns.size()-1){
                cls += columns.get(i);
            }else{
                cls += columns.get(i)+",";
            }
        }

        try {
            this.conn = DriverManager.getConnection(dbURL, user, pass);
            for (int i=0; i<stringMap.get(src.get(0)).size(); i++) {
                String Query="INSERT INTO "+tableName+" ("+cls+") values (";
                for (int j=0; j<columns.size(); j++){
                    if (j==columns.size()-1){
                        Query += "?)";
                    }else{
                        Query += "?, ";
                    }
                }
                PreparedStatement st = conn.prepareStatement(Query);

                for (int j=0; j<src.size(); j++){
                    st.setString(j+1,stringMap.get(src.get(j)).get(i));
                }

                //System.out.println(Query);
                st.executeUpdate();


//                System.out.println(personData.getString("LastName"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {
        this.loadPersonData();
    }
}
