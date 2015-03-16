

> ## Introduction ##

The job of a controller should be a straightforward one; it mediates a clients application request to an application's [model](Model.md) and passes the resulting data to the [view](View.md).

Most often, when a web developer wants to implement something in a different language, they are obliged to change platforms in order to accommodate that language.  This often means having to change our approach to the model and the view mechanics, not to mention the slew of configuration hassles.

Gossamer controller scripts may be written in a variety of [languages](#Supported_Languages.md).  A controller written in one language may even invoke the controller of another and interact with the resulting data.

A typical Gossamer controller responds to client requests by gathering data from the application model into a [digestible structure](#Result_Values.md) and returning it.  The controller may or may not select a [view](View.md), in this case meaning: an sample output document marked up with additional attributes to format the data by example.  (See [Dexter](Dexter.md)). Precisely how the data is rendered back to the client depends on how the client requested it.

> For more reference information on implementing controllers, see [Scripting](Scripting.md)

> Continue to [The View](View.md)