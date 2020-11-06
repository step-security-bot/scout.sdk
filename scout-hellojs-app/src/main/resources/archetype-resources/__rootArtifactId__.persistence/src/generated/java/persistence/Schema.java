#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * This file is generated by jOOQ.
 */
package ${package}.persistence;


import java.util.Arrays;
import java.util.List;

import ${package}.persistence.tables.Person;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Schema extends SchemaImpl {

    private static final long serialVersionUID = 600933592;

    /**
     * The reference instance of <code>Schema</code>
     */
    public static final Schema SCHEMA = new Schema();

    /**
     * The table <code>Schema.person</code>.
     */
    public final Person PERSON = Person.PERSON;

    /**
     * No further instances allowed
     */
    private Schema() {
        super("Schema", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            Person.PERSON);
    }
}
