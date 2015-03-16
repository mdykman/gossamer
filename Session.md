This provides our interface to session-level storage. It is implemented as a Concurrent HashMap and therefore supports the full Map API.

```
ruby:         $session.set("foo","bar")
ruby:         $session.remove('garbage')
javascript:   session.set('foo','bar');
groovy:       session.foo = 'bar'; session.set('foo') = "bar";
python:       mykeys = session.entrySet()
```
> 
---

> return to [Globals](Globals.md).