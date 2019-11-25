package io.vertx.howtos.graphql;

import graphql.GraphQL;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class GraphQLVerticle extends AbstractVerticle {

    private Map<String, Task> tasks;

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx(); // <1>
        vertx.deployVerticle(new GraphQLVerticle()); // <2>
    }

    @Override
    public void start() {
        tasks = initData();

        GraphQL graphQL = setupGraphQL();
        GraphQLHandler graphQLHandler = GraphQLHandler.create(graphQL); // <1>

        Router router = Router.router(vertx);
        router.route("/graphql").handler(graphQLHandler); // <2>

        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080);
    }

    private Map<String, Task> initData() {
        Stream<Task> stream = Stream.<Task>builder()
            .add(new Task("Learn GraphQL"))
            .add(new Task("Build awesome GraphQL server"))
            .add(new Task("Profit"))
            .build();

        return stream.collect(toMap(task -> task.id, task -> task));
    }

    private GraphQL setupGraphQL() {
        String schema = vertx.fileSystem().readFileBlocking("tasks.graphqls").toString(); // <1>

        SchemaParser schemaParser = new SchemaParser();
        TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema); // <2>

        RuntimeWiring runtimeWiring = newRuntimeWiring() // <3>
            .type("Query", builder -> builder.dataFetcher("allTasks", this::allTasks))
            .type("Mutation", builder -> builder.dataFetcher("complete", this::complete))
            .build();

        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring); // <4>

        return GraphQL.newGraphQL(graphQLSchema).build(); // <5>
    }

    private List<Task> allTasks(DataFetchingEnvironment env) {
        boolean uncompletedOnly = env.getArgument("uncompletedOnly");
        return tasks.values().stream()
            .filter(task -> !uncompletedOnly || !task.completed)
            .collect(toList());
    }

    private boolean complete(DataFetchingEnvironment env) {
        String id = env.getArgument("id");
        Task task = tasks.get(id);
        if (task == null) return false;

        task.completed = true;
        return true;
    }
}
