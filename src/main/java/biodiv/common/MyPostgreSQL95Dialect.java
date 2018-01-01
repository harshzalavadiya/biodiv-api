package biodiv.common;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL95Dialect;

public class MyPostgreSQL95Dialect extends PostgreSQL95Dialect{

	public MyPostgreSQL95Dialect() {
		this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
	}
}
