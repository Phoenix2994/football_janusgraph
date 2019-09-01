package football.janusgraph.example;

import org.janusgraph.core.attribute.Geoshape;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.core.JanusGraphTransaction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.Multiplicity;

import java.util.Map;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;

public class FootballExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(FootballExample.class);

    /**
     * Creates the vertex labels.
     */
    static protected void createVertexLabels(final JanusGraphManagement management) {
        management.makeVertexLabel("football_player").make();
        management.makeVertexLabel("coach").make();
        management.makeVertexLabel("player_prosecutor").make();
        management.makeVertexLabel("president").make();
        management.makeVertexLabel("team").make();
        management.makeVertexLabel("stadium").make();
    }

    /**
     * Creates the edge labels.
     */
    static protected void createEdgeLabels(final JanusGraphManagement management) {
        management.makeEdgeLabel("plays_for").multiplicity(Multiplicity.MANY2ONE).make();
        management.makeEdgeLabel("plays_in").multiplicity(Multiplicity.MANY2ONE).make();
        management.makeEdgeLabel("trains").make();
        management.makeEdgeLabel("assists").make();
        management.makeEdgeLabel("owns").make();
    }

    /**
     * Creates the properties for vertices, edges, and meta-properties.
     */
    static protected void createProperties(final JanusGraphManagement management) {
        management.makePropertyKey("name").dataType(String.class).make();
        management.makePropertyKey("age").dataType(Integer.class).make();
        management.makePropertyKey("place").dataType(Geoshape.class).make();
    }

    static public void createSchema(final JanusGraphManagement management) {
        LOGGER.info("creating schema");
        createProperties(management);
        createVertexLabels(management);
        createEdgeLabels(management);
        management.commit();
    }

    static public void createElements(GraphTraversalSource g) {
        try {
            LOGGER.info("creating elements");

            final Vertex chiesa = g.addV("football_player").property("name", "federico chiesa").property("age", 21).next();
            final Vertex sottil = g.addV("football_player").property("name", "riccardo sottil").property("age", 20).next();
            final Vertex ribery = g.addV("football_player").property("name", "frank ribery").property("age", 37).next();
            final Vertex montella = g.addV("coach").property("name", "vincenzo montella").property("age", 45).next();
            final Vertex commisso = g.addV("president").property("name", "rocco commisso").property("age", 69).next();
            final Vertex raiola = g.addV("player_prosecutor").property("name", "mino raiola").property("age", 51).next();
            final Vertex viola = g.addV("team").property("name", "fiorentina").next();


            g.V(chiesa).as("a").V(viola).addE("plays_for").from("a").next();
            g.V(sottil).as("a").V(viola).addE("plays_for").from("a").next();
            g.V(ribery).as("a").V(viola).addE("plays_for").from("a").next();
            g.V(montella).as("a").V(viola).addE("trains").from("a").next();
            g.V(commisso).as("a").V(viola).addE("owns").from("a").next();
            g.V(raiola).as("a").V(chiesa).addE("assists").from("a").next();
            g.V(raiola).as("a").V(ribery).addE("assists").from("a").next();
            g.V(raiola).as("a").V(sottil).addE("assists").from("a").next();

            g.tx().commit();

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            g.tx().rollback();
        }
    }

    static public void dropGraph(JanusGraph graph) throws Exception {
        if (graph != null) {
            JanusGraphFactory.drop(graph);
        }
    }

    public static void main(String[] args) throws Exception {
        //to create every time a new graph we drop the old one
        JanusGraph graph_old = JanusGraphFactory.open("conf/janusgraph-cassandra-elasticsearch.properties");
        dropGraph(graph_old);

        JanusGraph graph = JanusGraphFactory.open("conf/janusgraph-cassandra-elasticsearch.properties");
        final JanusGraphManagement management = graph.openManagement();
        createSchema(management);

        GraphTraversalSource g = graph.traversal();
        createElements(g);

        //search ribery vertex
        Map<Object, Object> riberyVertex = g.V().has("name", "frank ribery").valueMap(true).next();
        //search president of ribery's team
        Map<Object, Object> riberyPresident = g.V().has("name", "frank ribery").out("plays_for").in("owns").valueMap(true).next();
        LOGGER.info(riberyVertex.toString());
        LOGGER.info(riberyPresident.toString());

        System.exit(0);
    }
}

