package vn.elite.haru;

import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GraphQLDataFetchers {
    //region SAMPLE DATA
    private static List<Map<String, String>> books = Arrays.asList(
        new HashMap<String, String>() {{
            put("id", "book-1");
            put("name", "Harry Potter and the Philosopher's Stone");
            put("pageCount", "223");
            put("authorId", "author-1");
        }},
        new HashMap<String, String>() {{
            put("id", "book-2");
            put("name", "Moby Dick");
            put("pageCount", "635");
            put("authorId", "author-2");
        }},
        new HashMap<String, String>() {{
            put("id", "book-3");
            put("name", "Interview with the vampire");
            put("pageCount", "371");
            put("authorId", "author-3");
        }}
    );

    private static List<Map<String, String>> authors = Arrays.asList(
        new HashMap<String, String>() {{
            put("id", "author-1");
            put("firstName", "Joanne");
            put("lastName", "Rowling");
        }},
        new HashMap<String, String>() {{
            put("id", "author-2");
            put("firstName", "Herman");
            put("lastName", "Melville");
        }},
        new HashMap<String, String>() {{
            put("id", "author-3");
            put("firstName", "Anne");
            put("lastName", "Rice");
        }}
    );
    //endregion Sample data

    public DataFetcher getBookByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return books.stream()
                .filter(book -> book.get("id").equals(bookId))
                .findFirst().orElse(null);
        };
    }

    public DataFetcher getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, String> book = dataFetchingEnvironment.getSource();
            String authorId = book.get("authorId");
            return authors.stream()
                .filter(author -> author.get("id").equals(authorId))
                .findFirst().orElse(null);
        };
    }
}
