<a name="top"/>
GraphAware Neo4j Framework
==========================

[![Build Status](https://travis-ci.org/graphaware/neo4j-framework.png)](https://travis-ci.org/graphaware/neo4j-framework) | <a href="http://graphaware.com/downloads/" target="_blank">Downloads</a> | <a href="http://graphaware.com/site/framework/latest/apidocs/" target="_blank">Javadoc</a> | Latest Releases: 2.1.2.9

GraphAware Framework speeds up development with <a href="http://neo4j.org" target="_blank">Neo4j</a> by providing a
platform for building useful generic as well as domain-specific functionality, analytical capabilities, (iterative) graph algorithms,
etc.

See the <a href="http://graphaware.com/neo4j/2014/05/28/graph-aware-neo4j-framework.html" target="_blank">announcement on our blog</a>.

Features Overview
-----------------

On a high level, there are two key pieces of functionality:
* [GraphAware Server](#graphaware-server) is a Neo4j server extension that allows developers to rapidly build (REST) APIs
on top of Neo4j using Spring MVC, rather than JAX-RS.
* [GraphAware Runtime](#graphaware-runtime) is a runtime environment for both embedded and server deployments, which
allows the use of pre-built as well as custom modules called [GraphAware Runtime Modules](#graphaware-runtime). These
modules typically extend the core functionality of the database by
    * transparently enriching/modifying/preventing ongoing transactions in real-time
    * performing continuous computations on the graph in the background

Additionally, for [Java developers only](#javadev)(1), the following functionality is provided:

* [GraphAware Test](#graphaware-test)
    * [GraphUnit](#graphunit) - simple graph unit-testing
    * [Integration Testing](#inttest) - support for integration testing
    * [Performance Testing](#perftest) - support for performance testing
* [Improved Neo4j Transaction API](tx-api)
* [Transaction Executor](#tx-executor) and [Batch Transaction Executor](#batch-tx)
* [Miscellaneous Utilities](#utils)

(1) i.e., for embedded mode users, managed/unmanaged extensions developers, [GraphAware Runtime Module](#graphaware-runtime)
 developers and framework-powered Spring MVC controller developers

Framework Usage
---------------

<a name="servermode"/>
### Server Mode

When using Neo4j in the <a href="http://docs.neo4j.org/chunked/stable/server-installation.html" target="_blank">standalone server</a> mode,
deploying the GraphAware Framework (and any code using it) is a matter of [downloading](#download) the appropriate .jar files,
copying them into the _plugins_ directory in your Neo4j installation, and restarting the server. The framework and modules
are then used via calls to their REST APIs, if they provide any.

Note that only **Neo4j 2.0.3 and above** are supported. If you see a `java.lang.IllegalAccessError` when starting up the
server, then you're most likely using a version of Neo4j older than 2.0.3.

### Embedded Mode / Java Development

Java developers that use Neo4j in <a href="http://docs.neo4j.org/chunked/stable/tutorials-java-embedded.html" target="_blank">embedded mode</a>
and those developing Neo4j <a href="http://docs.neo4j.org/chunked/stable/server-plugins.html" target="_blank">server plugins</a>,
<a href="http://docs.neo4j.org/chunked/stable/server-unmanaged-extensions.html" target="_blank">unmanaged extensions</a>,
[GraphAware Runtime Modules](#graphaware-runtime), or Spring MVC controllers can include use the framework as a dependency
for their Java project and use it as a library of useful tested code, in addition to the functionality provided for
[server mode](#servermode).

<a name="download"/>
Getting GraphAware Framework
----------------------------

### Releases

To use the latest release, download the appropriate version and put it
the _plugins_ directory in your Neo4j server installation and restart the server (server mode), or on the classpath (embedded mode).

The following downloads are available:
* [GraphAware Framework for Embedded Mode, version 2.1.2.9](http://graphaware.com/downloads/graphaware-embedded-all-2.1.2.9.jar)
* [GraphAware Framework for Server Mode (Community), version 2.1.2.9](http://graphaware.com/downloads/graphaware-server-community-all-2.1.2.9.jar)
* [GraphAware Framework for Server Mode (Enterprise), version 2.1.2.9](http://graphaware.com/downloads/graphaware-server-enterprise-all-2.1.2.9.jar)

Releases are synced to <a href="http://search.maven.org/#search%7Cga%7C1%7Ccom.graphaware.neo4j" target="_blank">Maven Central repository</a>. When using Maven for dependency management, include one of more of the following dependencies in your pom.xml. Read further
down this page to find out which dependencies you will need. The available ones are:

    <dependencies>
        ...
        <dependency>
            <groupId>com.graphaware.neo4j</groupId>
            <artifactId>api</artifactId>
            <version>2.1.2.9</version>
        </dependency>
        <dependency>
            <groupId>com.graphaware.neo4j</groupId>
            <artifactId>common</artifactId>
            <version>2.1.2.9</version>
        </dependency>
        <dependency>
            <groupId>com.graphaware.neo4j</groupId>
            <artifactId>runtime</artifactId>
            <version>2.1.2.9</version>
        </dependency>
        <dependency>
            <groupId>com.graphaware.neo4j</groupId>
            <artifactId>tests</artifactId>
            <version>2.1.2.9</version>
        </dependency>
        <dependency>
            <groupId>com.graphaware.neo4j</groupId>
            <artifactId>tx-api</artifactId>
            <version>2.1.2.9</version>
        </dependency>
        <dependency>
            <groupId>com.graphaware.neo4j</groupId>
            <artifactId>tx-executor</artifactId>
            <version>2.1.2.9</version>
        </dependency>

        ...
    </dependencies>

### Snapshots

To use the latest development version, just clone this repository and run `mvn clean install`. This will produce 2.1.2.10-SNAPSHOT
 jar files. If you need standalone .jar files with all dependencies, look into the `target` folders in the `build` directory.

### Note on Versioning Scheme

The version number has two parts. The first three numbers indicate compatibility with a Neo4j version.
 The last number is the version of the framework. For example, version 2.1.2.3 is version 3 of the framework
 compatible with Neo4j 2.1.2

<a name="server"/>
GraphAware Server
-----------------

**Example:** An example is provided in `examples/node-counter`.

With GraphAware Framework in the _plugins_ directory of your Neo4j server installation, it is possible to develop Spring
MVC controllers that have the Neo4j database wired in as `GraphDatabaseService`.

For example, to develop an API endpoint that counts all the nodes in the database using Spring MVC, create the following
controller:

```java
/**
 *  Sample REST API for counting all nodes in the database.
 */
@Controller
@RequestMapping("count")
@Transactional
public class NodeCountApi {

    private final GraphDatabaseService database;

    @Autowired
    public NodeCountApi(GraphDatabaseService database) {
        this.database = database;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public long count() {
        return Iterables.count(GlobalGraphOperations.at(database).getAllNodes());
    }
}
```

**WARNING** Your class must reside in a `com`, `net`, or `org` top-level
package and one of the package levels must be called `graphaware`. For example, `com.mycompany.graphaware.NodeCountApi`
 will do. Alternatively, if you do not want the class to reside in the specified package, you need to put the following
 class in a package that follows the specification:

```java
@Configuration
@ComponentScan(basePackages = {"com.yourdomain.**"})
public class GraphAwareIntegration {
}
```

Then your controllers can reside in any subpackage of `com.yourdomain`.
**WARNING END**

Compile this code into a .jar file (with dependencies, see below) and place it into the _plugins_ directory of your
Neo4j server installation. You will then be able to issue a `GET` request to `http://your-neo4j-url:7474/graphaware/count`
and receive the number of nodes in the database in the response body. Note that the `graphaware` part of the URL must be
there and cannot (yet) be configured.

To get started quickly, use the provided Maven archetype by typing:

    mvn archetype:generate -DarchetypeGroupId=com.graphaware.neo4j -DarchetypeArtifactId=graphaware-springmvc-maven-archetype -DarchetypeVersion=2.1.2.9

To get started manually, you will need the following dependencies:

```xml
<dependencies>

    <!-- GraphAware Framework -->
    <dependency>
        <groupId>com.graphaware.neo4j</groupId>
        <artifactId>common</artifactId>
        <version>2.1.2.9</version>
        <scope>provided</scope>
    </dependency>

    <dependency>
        <groupId>com.graphaware.neo4j</groupId>
        <artifactId>api</artifactId>
        <version>2.1.2.9</version>
        <scope>provided</scope>
    </dependency>

    <!-- Spring Framework -->
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-webmvc</artifactId>
        <version>4.0.0.RELEASE</version>
        <scope>provided</scope>
    </dependency>

    <!-- Neo4j -->
    <dependency>
        <groupId>org.neo4j</groupId>
        <artifactId>neo4j</artifactId>
        <version>2.1.2.9</version>
        <scope>provided</scope>
    </dependency>

    <!-- Testing -->
    <dependency>
        <groupId>com.graphaware.neo4j</groupId>
        <artifactId>server-community</artifactId>
        <version>2.1.2.9</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>com.graphaware.neo4j</groupId>
        <version>2.1.2.9</version>
        <artifactId>tests</artifactId>
        <scope>test</scope>
    </dependency>

</dependencies>
```

It is also a good idea to use make sure the resulting .jar file includes all the dependencies, if you use any external
ones that aren't listed above:
<a name="alldependencies"/>
```xml
<build>
    <plugins>
        <plugin>
            <artifactId>maven-assembly-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>attached</goal>
                    </goals>
                </execution>
            </executions>
            <configuration>
                <finalName>${project.name}-all-${project.version}</finalName>
                <descriptorRefs>
                    <descriptorRef>jar-with-dependencies</descriptorRef>
                </descriptorRefs>
                <appendAssemblyId>false</appendAssemblyId>
            </configuration>
        </plugin>
    </plugins>
</build>
```

<a name="runtime"/>
GraphAware Runtime
------------------

GraphAware Runtime is useful when you:
* require functionality that transparently alters transactions or prevents them from happening at all. For example, you might want to:
    * Enforce specific constraints on the graph schema
    * Use optimistic locking to prevent updates of out-of-date data
    * Improve performance by building (and keeping in sync) in-graph indices
    * Improve performance of supernodes
    * Prevent certain parts of the graph from being deleted
    * Timestamp modifications
    * Find out what the latest graph modifications that took place were
    * Write trigger-like functionality (which can actually be unit-tested!)
    * ... and much more
* need to compute something continuously in the background, writing the results back to the graph. For example, you might want to:
    * compute PageRank
    * compute maximum flow between points in the network
    * pre-compute similarities between people
    * pre-compute recommendations to people
    * ... and much more

### Building a Transaction-Driven GraphAware Runtime Module

**Example:** An example is provided in `examples/friendship-strength-counter-module`.

To get started quickly, use the provided Maven archetype by typing:

    mvn archetype:generate -DarchetypeGroupId=com.graphaware.neo4j -DarchetypeArtifactId=graphaware-runtime-module-maven-archetype -DarchetypeVersion=2.1.2.9

To start from scratch, you will need the following dependencies in your pom.xml

```xml
<dependencies>
    ...
    <!-- needed if the module exposes an API -->
    <dependency>
        <groupId>com.graphaware.neo4j</groupId>
        <artifactId>api</artifactId>
        <version>2.1.2.9</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.graphaware.neo4j</groupId>
        <artifactId>common</artifactId>
        <version>2.1.2.9</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.graphaware.neo4j</groupId>
        <artifactId>runtime</artifactId>
        <version>2.1.2.9</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.graphaware.neo4j</groupId>
        <artifactId>tests</artifactId>
        <version>2.1.2.9</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.graphaware.neo4j</groupId>
        <artifactId>tx-api</artifactId>
        <version>2.1.2.9</version>
        <scope>provided</scope>
    </dependency>
    <dependency>
        <groupId>com.graphaware.neo4j</groupId>
        <artifactId>tx-executor</artifactId>
        <version>2.1.2.9</version>
        <scope>provided</scope>
    </dependency>

    ...
</dependencies>
```

Again, if using other dependencies, you need to make sure the resulting .jar file includes all the dependencies. [See above](#alldependencies).

Your module then needs to be built by implementing the <a href="http://graphaware.com/site/framework/latest/apidocs/com/graphaware/runtime/module/TxDrivenModule.html" target="_blank">TxDrivenModule</a> interface.
An example is provided in `examples/friendship-strength-counter-module`. This computes the sum of all `strength` properties
on `FRIEND_OF` relationships and keeps it up to data, written to a special node created for that purpose. It also has
a REST API that can be queried for the total friendship strength value.

### Building a Timer-Driven GraphAware Runtime Module

Similarly, your module can implement the the <a href="http://graphaware.com/site/framework/latest/apidocs/com/graphaware/runtime/module/TimerDrivenModule.html" target="_blank">TimerDrivenModule</a> interface
in order to be able to perform computations on the graph that are automatically scheduled. The framework will detect quiet
periods in your database and increase the rate at which modules perform behind-the-scenes computations. During busy periods, naturally,
the rate is decreased.

Each unit of work, implemented by the `doSomeWork` method on `TimerDrivenModule`, should be a short computation that
writes some results back to the graph. This is very useful for iterative algorithms like PageRank, which are too expensive
to compute in real-time.

<a name="server-usage"/>
### Using GraphAware Runtime (Server Mode)

Using the GraphAware Runtime only makes sense when there is a GraphAware Runtime Module (or more) to go with it.
Assuming we want to use the runtime with the `FriendshipStrengthModule` from examples in server mode, provided that
the GraphAware Framework .jar file is present in the Neo4j `plugins` directory, the following line needs to
be added to `neo4j.properties` in order for the GraphAware Runtime to be enabled:

`com.graphaware.runtime.enabled=true`

GraphAware Runtime Modules can be registered using the following mechanism we will illustrate on the example of
 `FriendshipStrengthModule`. First, a _bootstrapper_ needs to be created like this:

```java
/**
 * {@link GraphAwareRuntimeModuleBootstrapper} for {@link FriendshipStrengthModule}.
 */
public class FriendshipStrengthModuleBootstrapper implements GraphAwareRuntimeModuleBootstrapper {

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphAwareRuntimeModule bootstrapModule(String moduleId, Map<String, String> config, GraphDatabaseService database) {
        return new FriendshipStrengthModule(moduleId, database);
    }
}
```

Then, assuming it lives in `com.graphaware.example.module` package, the boostrapper must be registered
with the runtime using the following line in neo4j.properties:

`com.graphaware.module.FSM.1=com.graphaware.example.module.FriendshipStrengthModuleBootstrapper`

which means that the `FriendshipStrengthModule` will be the first runtime module registered with the runtime with ID
equal to "FSM".

### Using GraphAware Runtime (Embedded Mode)

To use the runtime and modules programmatically, all we need to do is instantiate the runtime and register the module with it:

```java
GraphDatabaseService database = new TestGraphDatabaseFactory().newImpermanentDatabase(); //replace with a real DB
GraphAwareRuntime runtime = GraphAwareRuntimeFactory.createRuntime(database);
runtime.registerModule(new FriendshipStrengthModule("FSM", database));
```

It is, however, also possible to pass a _neo4j.properties_ file to the database. Same rules as in the [server mode](#server-usage)
 apply. For example, if we have a neo4j-friendship.properties file with the following lines

```
# GraphAware Config
com.graphaware.runtime.enabled=true
com.graphaware.module.friendshipcounter.1=com.graphaware.example.module.FriendshipStrengthModuleBootstrapper
```

the runtime and modules will be configured correctly by just doing

```java
database = new TestGraphDatabaseFactory()
              .newImpermanentDatabaseBuilder()
              .loadPropertiesFromFile("neo4j-friendship.properties")
              .newGraphDatabase();
```

**NOTE:** Modules are presented with the about-to-be-committed  transaction data or asked to do work on scheduled basis
in the order in which they've been registered.

<a name="javadev"/>
Features for Java Developers
----------------------------

Whether or not you use the code in this repository as a framework or runtime as described above, you can always add it
as a dependency and take advantage of its useful features.

<a name="graphaware-test"/>
### GraphAware Test

Add the following snippet to your pom.xml:

```xml
 <dependency>
    <groupId>com.graphaware.neo4j</groupId>
    <artifactId>tests</artifactId>
    <version>2.1.2.9</version>
    <scope>test</scope>
</dependency>
```

<a name="graphunit"/>
#### GraphUnit

`GraphUnit` is a single class with two `public static` methods intended for easy unit-testing of code that somehow manipulates
data in the Neo4j graph database. It allows to assert the correct state of the database after the code has been run, using Cypher `CREATE` statements.

The first method `public static void assertSameGraph(GraphDatabaseService database, String sameGraphCypher)` is used to verify
that the graph in the `database` is exactly the same as the graph created by `sameGraphCypher` statement. This means that
the nodes, their properties and labels, relationships, and their properties and labels must be exactly the same. Note that
Neo4j internal node/relationship IDs are ignored. In case the graphs aren't identical, the assertion fails using standard `junit` mechanisms.

The second method `public static void assertSubgraph(GraphDatabaseService database, String subgraphCypher)` is used to
verify that the graph created by `sameGraphCypher` statement is a subgraph of the graph in the `database`.

<a name="inttest"/>
#### Integration Testing
TBD

<a name="perftest"/>
#### Performance Testing

Sometimes it is necessary to run some experiments on the database to check how your code, queries, or the database
itself performs. This is tricky because there are many moving parts:
 * size of transaction (e.g. how often do you commit)?
 * database contents (you want this to be as realistic as possible)
 * data in cache (is data on disk? low level cache? high level cache?)
 * etc...

GraphAware Framework provides a set of classes to simplify performance testing with Neo4j. Start by exploring the JavaDoc
 of `PerformanceTestSuite` and `PerformanceTest`. Then head to `examples/performance-testing` to see an implementation
 of a performance test used for <a href="http://graphaware.com/neo4j/2013/10/24/neo4j-qualifying-relationships.html" target="_blank">this blog post</a>.

In essence, each test can define a list of `Parameters` - these are the moving parts. The Framework will then generate
all permutations and run the performance test with each a specified number of times. Implementations of `PerformanceTest`
can specify, among other things:
* how many times the test should be run and measured
* how many times it should be run before measurements are started to warm up caches (dry runs)
* what parameters to use
* when to throw away and re-build the database

Here's a simple example of a performance test.

```java
/**
 * A {@link com.graphaware.test.performance.PerformanceTest} for documentation. Runs test for each of the scenarios
 * with 3 different {@link CacheConfiguration}s.
 */
public class DummyTestForDocs implements PerformanceTest {

    enum Scenario {
        SCENARIO_1,
        SCENARIO_2
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String shortName() {
        return "test-short-name";
    }

    @Override
    public String longName() {
        return "Test Long Name";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Parameter> parameters() {
        List<Parameter> result = new LinkedList<>();

        result.add(new CacheParameter("cache")); //no cache, low-level cache, high-level cache
        result.add(new EnumParameter("scenario", Scenario.class));

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int dryRuns(Map<String, Object> params) {
        return ((CacheConfiguration) params.get("cache")).needsWarmup() ? 10000 : 100;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int measuredRuns() {
        return 100;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> databaseParameters(Map<String, Object> params) {
        return ((CacheConfiguration) params.get("cache")).addToConfig(Collections.<String, String>emptyMap());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareDatabase(GraphDatabaseService database, final Map<String, Object> params) {
        //create 100 nodes in batches of 100
        new NoInputBatchTransactionExecutor(database, 100, 100, new UnitOfWork<NullItem>() {
            @Override
            public void execute(GraphDatabaseService database, NullItem input, int batchNumber, int stepNumber) {
                database.createNode();
            }
        }).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RebuildDatabase rebuildDatabase() {
        return RebuildDatabase.AFTER_PARAM_CHANGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long run(GraphDatabaseService database, Map<String, Object> params) {
        Scenario scenario = (Scenario) params.get("scenario");
        switch (scenario) {
            case SCENARIO_1:
                //run test for scenario 1
                return 20; //the time it took in microseconds
            case SCENARIO_2:
                //run test for scenario 2
                return 20; //the time it took in microseconds
            default:
                throw new IllegalStateException("Unknown scenario");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean rebuildDatabase(Map<String, Object> params) {
        throw new UnsupportedOperationException("never needed, database rebuilt after every param change");
    }
}
```

You would change the `run` method implementation to do some real work. Then add this test to a test suite and run it:

```java
/**
 * Dummy {@link PerformanceTestSuite} for documentation. Runs {@link DummyTestForDocs}.
 */
public class DummyTestSuiteForDocs extends PerformanceTestSuite {

    /**
     * {@inheritDoc}
     */
    @Override
    protected PerformanceTest[] getPerfTests() {
        return new PerformanceTest[]{
                new DummyTestForDocs()
        };
    }
}
```

This would result in a total of 6 different parameter permutations (3 cache types x 2 scenarios), each executed 100 times.
At the end of the run, you get a file called "test-short-name-xxx.txt" (xxx is a timestamp) in the root of your project.
The contents fo the file are the runtimes of each test, organised by parameter permutations:

```
Test Long Name

cache;scenario;times in microseconds...
nocache;SCENARIO_1;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20
nocache;SCENARIO_2;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20
lowcache;SCENARIO_1;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20
lowcache;SCENARIO_2;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20
highcache;SCENARIO_1;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20
highcache;SCENARIO_2;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20;20
```

You can now have some fun analysing the results - a good starting point could be the python scripts on the `resources`
folder of `examples/performance-testing`.

<a name="tx-executor"/>
### Simplified Transactional Operations

Every mutating operation in Neo4j must run within the context of a transaction. The code dealing with that typically
involves try-catch blocks and looks something like this:

 ```java
 Transaction tx = database.beginTx();
 try {
     //do something useful, can throw a business exception
     tx.success();
 } catch (RuntimeException e) {
     //deal with a business exception
     tx.failure();
 } finally {
     tx.finish(); //can throw a database exception
 }
 ```

 As of Neo4j 2.0, this could be simplified to this:

 ```java
try (Transaction tx = database.beginTx()) {
    //do something useful, can throw a business exception
    tx.success();
}
 ```

GraphAware provides an alternative, callback-based API called `TransactionExecutor` in `com.graphaware.tx.executor`.
`SimpleTransactionExecutor` is a simple implementation thereof and can be used on an instance-per-database basis.
 Since you will typically run a single in-process database instance, you will also only need a single `SimpleTransactionExecutor`.

To create an empty node in a database, you would write something like this.

```java
GraphDatabaseService database = new TestGraphDatabaseFactory().newImpermanentDatabase(); //only for demo, use your own persistent one!
TransactionExecutor executor = new SimpleTransactionExecutor(database);

executor.executeInTransaction(new VoidReturningCallback() {
    @Override
    public void doInTx(GraphDatabaseService database) {
        database.createNode();
    }
});
```

You have the option of selecting an `ExceptionHandlingStrategy`. By default, if an exception occurs, the transaction will be
 rolled back and the exception re-thrown. This is true for both application/business exceptions (i.e. the exception your
 code throws in the `doInTx` method above), and Neo4j exceptions (e.g. constraint violations). This default strategy is
 called `RethrowException`.

The other available implementation of `ExceptionHandlingStrategy` is `KeepCalmAndCarryOn`. It still rolls back the transaction
in case an exception occurs, but it does not re-throw it (only logs it). To use a different `ExceptionHandlingStrategy`, perhaps
  one that you implement yourself, just pass it in to the `executeInTransaction` method:

```java
  executor.executeInTransaction(transactionCallback, KeepCalmAndCarryOn.getInstance());
```

<a name="batch-tx"/>
### Batch Transactional Operations

It is a common requirement to execute operations in batches. For instance, you might want to populate the database with
data from a CSV file, or just some generated dummy data for testing. If there are many such operations (let's say 10,000
or more), doing it all in one transaction isn't the most memory-efficient approach. On the other hand, a new transaction
for each operation results in too much overhead. For some use-cases, `BatchInserters` provided by Neo4j suffice. However,
operations performed using these do not run in transactions and have some other limitations (such as no node/relationship
 delete capabilities).

GraphAware can help here with `BatchTransactionExecutor`s. There are a few of them:

#### Input-Based Batch Operations

If you have some input, such as lines from a CSV file or a result of a Neo4j traversal, and you want to perform an operation
for each item of such input, use `IterableInputBatchTransactionExecutor`. As the name suggests, the input needs to be in the form
of an `Iterable`. Additionally, you need to define a `UnitOfWork`, which will be executed against the database for each
input item. After a specified number of batch operations have been executed, the current transaction is committed and a
new one started, until we run out of input items to process.

For example, if you were to create a number of nodes from a list of node names, you would do something like this:

```java
GraphDatabaseService database = new TestGraphDatabaseFactory().newImpermanentDatabase(); //only for demo, use your own persistent one!

List<String> nodeNames = Arrays.asList("Name1", "Name2", "Name3");  //there will be many more

int batchSize = 10;
BatchTransactionExecutor executor = new IterableInputBatchTransactionExecutor<>(database, batchSize, nodeNames, new UnitOfWork<String>() {
    @Override
    public void execute(GraphDatabaseService database, String nodeName, int batchNumber, int stepNumber) {
        Node node = database.createNode();
        node.setProperty("name", nodeName);
    }
});

executor.execute();
```

#### Batch Operations with Generated Input or No Input

In case you wish to do something input-independent, for example just generate a number of nodes with random names, you
can use the `NoInputBatchTransactionExecutor`.

First, you would create an implementation of `UnitOfWork<NullItem>`, which is a unit of work expecting no input:

```java
/**
 * Unit of work that creates an empty node with random name. Singleton.
 */
public class CreateRandomNode implements UnitOfWork<NullItem> {
    private static final CreateRandomNode INSTANCE = new CreateRandomNode();

    public static CreateRandomNode getInstance() {
        return INSTANCE;
    }

    private CreateRandomNode() {
    }

    @Override
    public void execute(GraphDatabaseService database, NullItem input, int batchNumber, int stepNumber) {
        Node node = database.createNode();
        node.setProperty("name", UUID.randomUUID());
    }
}
```

Then, you would use it in `NoInputBatchTransactionExecutor`:

```java
//create 100,000 nodes in batches of 1,000:
int batchSize = 1000;
int noNodes = 100000;
BatchTransactionExecutor batchExecutor = new NoInputBatchTransactionExecutor(database, batchSize, noNodes, CreateRandomNode.getInstance());
batchExecutor.execute();
```

#### Multi-Threaded Batch Operations

If you wish to execute any batch operation using more than one thread, you can use the `MultiThreadedBatchTransactionExecutor`
 as a decorator of any `BatchTransactionExecutor`. For example, to execute the above example using 4 threads:

```java
int batchSize = 1000;
int noNodes = 100000;
BatchTransactionExecutor batchExecutor = new NoInputBatchTransactionExecutor(database, batchSize, noNodes, CreateRandomNode.getInstance());
BatchTransactionExecutor multiThreadedExecutor = new MultiThreadedBatchTransactionExecutor(batchExecutor, 4);
multiThreadedExecutor.execute();
```

<a name="utils"/>
### Miscellaneous Utilities

The following functionality is also provided:

* Arrays (see `ArrayUtils`)
    * Determine if an object is a primitive array
    * Convert an array to a String representation
    * Check equality of two `Object`s which may or may not be arrays
    * Check equality of two `Map<String, Object>` instances, where the `Object`-typed values may or may not be arrays

* Property Containers (see `PropertyContainerUtils`)
    * Convert a `PropertyContainer` to a Map of properties
    * Delete nodes with all their relationships automatically, avoiding a `org.neo4j.kernel.impl.nioneo.store.ConstraintViolationException: Node record Node[xxx] still has relationships`, using `DeleteUtils.deleteNodeAndRelationships(node);`

* Relationship Directions
    * The need to determine the direction of a relationship is quite common. The `Relationship` object does not provide the
      functionality for the obvious reason that it depends on "who's point of view we're talking about". In order to resolve
      a direction from a specific Node's point of view, use `DirectionUtils.resolveDirection(Relationship relationship, Node pointOfView);`

* Iterables (see `IterableUtils` in tests)
    * Count iterables by iterating over them, unless they're a collection in which case just return `size()`
    * Randomize iterables by iterating over them and shuffling them, unless they're a collection in which case just shuffle
    * Convert iterables to lists
    * Check if iterable contains an object by iterating over the iterable, unless it's a collection in which case just return `contains(..)`

... and more, please see JavaDoc.

License
-------

Copyright (c) 2014 GraphAware

GraphAware is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License
as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program.
If not, see <http://www.gnu.org/licenses/>.
