Gossamer represents a new approach to web development providing a multi-language script facility with a strict MVC philosophy.

What languages can I use with Gossamer? Groovy, ruby, PHP, Javascript, scheme, `BeanShell` and [more](Controller#Supported_Languages.md).  Any language with a JVM implementation and a JSR 223-compliant interface can be integrated into Gossamer.

Conventional frameworks tend to be built up around the facilities of one specific programming language.  If you want to experiment with a different language, a new framework is required and with it a new paradigm. If you think a subset of the problem at hand is better suited to another language and want to integrate, it is usually an ad-hoc procedure and a fragile one at that.

Gossamer provides a unique, unified platform where languages are readily intermixed.  A request may be handled by a single script or several, single language or multi-language. Gossamer tries to stay out of your way and let you program.

Json and XML requests typically share the same scripts as HTML or RSS requests. The task of the script programmer is to compose data, not to worry about rendering details.


It provides a unique take on the classic MVC model, and so is documented in reverse.

  * [Controller](Controller.md)
  * [View](View.md)
  * [Model](Model.md)