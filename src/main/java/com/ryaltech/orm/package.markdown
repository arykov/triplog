# WorkFlow Lite ORM

WorkFlow Lite ORM is a very simple object-relational management framework, based
on the WorkFlow Lite SQL utilities and the Spring JDBC helper classes.

The idea is to build a `Mapping` instance for each class you want to
persist. A `Mapping` consists of a series of `Column` objects, each of
which maps a field in the managed class to a column in the database.

The class-to-table and field-to-column mappings are all one-to-one. We
can't do fancy things like mapping inheritance hierarchies or splitting
field values across multiple database columns.

We also don't attempt to map relationships between objects. This is
difficult to do for both the framework developer and the caller (ask any
developer who's worked with Hibernate about
`LazyInitializationExceptions`. Make sure there are no children in the
room!)

Once you've built a `Mapping`, you can use its methods to build DAO-like
functionality.
