### Cursors in om

It is possible to get access to the root cursor of the application outside of
the typical loop. You can do so by calling `(om/ref-cursor (om/root-cursor state))` where state
is the atom that the root cursor for the app is derived form. Then if you
update that root cursor, it will also update the root cursor of the
application, as well. You can then use `om/ref-cursor` to access specific
sub-cursors of the application state, but this only works if the sub-state is
not a primitive. If it is a primitive, then `transact!` with `update-in` or
something similar must be used instead.

### Routing

There is something I need to consider when designing the routing component. The
first decision I need to make is whether I want routing to trigger a different
component to render via a call to `om/root` or whether I want the router to
simply update the application state. There are some trade-offs here.

The benefits of calling `om/root` for each separate route would be:

* Some routing information would not need to be stored in the application state
* Clear distinction between different "pages" of the application

The drawback to calling `om/root` for each separate route would be:

* The router would also need to update certain pieces of application state
  * For example, if a certain identifier was in the route, the router would
    have to both update the route and update the application state with the
    identifier, which is a little less "clean."

The benefits of simply updating the application state for each route would be:

* Both the page and any page-specific state could be set by the router in one "go"
* The router would not be responsible for any rendering whatsoever

### Secretary Routing

Secretary did not seem suited to my needs because there is no way to pass an arbitrary argument to a route handler. For example, I wanted to write a `navigate!` function that would take a router record and a url as arguments, but secretary's `defroute` does not allow acceptance of arbitrary arguments. I evaluted a couple of other options: [silk](https://github.com/DomKM/silk) and [bidi](https://github.com/juxt/bidi). Both of them looked like they would fit my needs for plenty of flexibility (and I like that they define routes via data instead of macro). I chose bidi because it seemed to be a bit simpler/easier to pick up.
