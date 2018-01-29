package biodiv.common;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL93Dialect;
//import org.hibernate.dialect.PostgreSQL95Dialect;

public class MyPostgreSQL93Dialect extends PostgreSQL93Dialect{

	public MyPostgreSQL93Dialect() {
		this.registerColumnType(Types.JAVA_OBJECT, "json");
	}
}
