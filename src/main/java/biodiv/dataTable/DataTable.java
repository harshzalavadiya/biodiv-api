package biodiv.dataTable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "data_table", schema = "public")
public class DataTable {
	
	private long id;
	private String columns;
	
	public DataTable(){
		
	}
	
	
	public DataTable(Long id , String columns){
		this.id = id;
		this.columns = columns;
	}

	@Id
	@GenericGenerator(
	        name = "hibernate_generator",
	        strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
	        parameters = {
	                @Parameter(name = "sequence_name", value = "hibernate_sequence"),
	                @Parameter(name = "increment_size", value = "1"),
                    @Parameter(name = "optimizer", value = "hilo")
	        }
	)
	@GeneratedValue(generator = "hibernate_generator")
	@Column(name = "id", unique = true, nullable = false)
	public long getId() {
		return this.id;
	}


	public void setId(long id) {
		this.id = id;
	}

	@Column(name = "columns",nullable= false)
	public String getColumns() {
		return this.columns;
	}


	public void setColumns(String columns) {
		this.columns = columns;
	}
}
