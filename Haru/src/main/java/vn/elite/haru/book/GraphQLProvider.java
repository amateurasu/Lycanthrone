package vn.elite.haru.book;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

@Component
public class GraphQLProvider {
    private final GraphQLDataFetchers dataFetchers;
    private GraphQL graphQL;

    public GraphQLProvider(GraphQLDataFetchers graphQLDataFetchers) {
        this.dataFetchers = graphQLDataFetchers;
    }

    @PostConstruct
    public void init() {
        File sdl = new File(GraphQLProvider.class.getResource("schema.graphql").getFile());
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    private GraphQLSchema buildSchema(File sdl) {
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = buildWiring();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring().type(
            newTypeWiring("Query").dataFetcher("book", dataFetchers.getBookByIdDataFetcher())
        ).type(
            newTypeWiring("Book").dataFetcher("author", dataFetchers.getAuthorDataFetcher())
        ).build();
    }

    @Bean
    public GraphQL graphQL() {
        return graphQL;
    }
}
