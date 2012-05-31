
package repositories;

import java.sql.ResultSet;
import play.db.DB;

public class OpenFoosDatabase extends DB {

    public static ResultSet executeQueryToFoosBase(String sql) {

        return executeQuery(sql);
    }
}
