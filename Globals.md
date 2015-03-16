These variables are visible to controllers in all scripting engines.
  * [args](RequestArgs.md) - the request parameters
  * [vm](ViewManager.md) - the view manager
  * [session](Session.md) - the user session
  * [uploads](Uploads.md) - the upload manager
  * site    - interface to site.properties
  * installBase - a string showing the installation base for this
  * requestInfo - simplified interface to information about the
  * reqp    - the pathinfo, minus the leading slash (deprecated; use requestInfo.pathInfo which includes the leading slash)
  * rp      - the request processor; used to invoke sibling controllers
  * jdbc    - the JDBC connection manager
  * hsf     - Hibernate session factory manager
  * writer  - used to emit raw responses
  * factory - a proxy for the spring bean factory
  * request - the `HttpServletRequest` object
  * response - the `HttpServletResponse` object
  * id      - the runtime identifier; (reserved for future use)