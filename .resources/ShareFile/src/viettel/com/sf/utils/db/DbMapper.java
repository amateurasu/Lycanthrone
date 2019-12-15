package viettel.com.sf.utils.db;

import java.sql.ResultSet;

@FunctionalInterface
public interface DbMapper<T> {
    T map(ResultSet t);
}
