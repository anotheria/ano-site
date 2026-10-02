# How to transfer documents in AnoSite

Transfer publishes a cms document to other instances: the editor clicks the transfer icon in a list or the
transfer button in an edit dialog, picks a target group and a scope, and the document is written to every
instance in that group through the generated rest api.

A group can also be configured to receive **every** change by itself, without anybody clicking anything —
see [6. Auto transfer](#6-auto-transfer-publishing-every-change).

It is **off by default**. An instance only publishes into other systems if it was explicitly set up to.

## 1. Configuration

Everything lives in `anositeconfig.json`. There is no separate transfer config file.

```json
{
  "transferEnabled": true,
  "@transferTargetGroups": [
    {
      "name": "test",
      "autoTransfer": true,
      "@targets": [
        {"name": "test1", "url": "https://test1.example.com/api"},
        {"name": "test2", "url": "https://test2.example.com/api"},
        {"name": "test3", "url": "https://test3.example.com/api"},
        {"name": "test4", "url": "https://test4.example.com/api"}
      ]
    },
    {
      "name": "prod",
      "@targets": [{"name": "prod1", "url": "https://www.example.com/api"}]
    }
  ]
}
```

**Mind the `@`.** It is configureme's marker for an attribute whose value is an object rather than a plain
value, and it is required on both levels here. Without it configureme reads a json object as an *environment*,
not as a `TransferTargetGroup` — the config still loads, no exception is thrown, and the instance simply has no
transfer targets. The java field names carry no `@`; the parser strips it.

| key | default | meaning |
|---|---|---|
| `transferEnabled` | `false` | while false no document can be transferred from this instance |
| `@transferTargetGroups` | empty | the groups the editor picks from; a group without a name or without a single target url is not offered |
| `@transferTargetGroups[].name` | – | what the editor selects, has to be unique |
| `@transferTargetGroups[].autoTransfer` | `false` | while true every created, changed or deleted document is published into this group by itself, see [6](#6-auto-transfer-publishing-every-change) |
| `@transferTargetGroups[].@targets[].name` | – | shown in the dialog and in the result |
| `@transferTargetGroups[].@targets[].url` | – | base url of the **target's rest api**, including the path Jersey is mapped under there; a trailing slash is tolerated |

**The api path is part of the target url, per target.** Nothing in the transfer assumes `/api` — installations
that map the cms endpoints under `/asg-api`, or under a context path, just say so:

```json
{"name": "legacy", "url": "https://old.example.com/asg-api"},
{"name": "hosted", "url": "https://example.com/cms/api"}
```

Two targets in the same group may sit under different paths. Check what the target maps
`jersey.config.server.provider.packages` under in its `web.xml`; that plus its host is the url.

**Being allowed to transfer is a property of the instance, not of its environment.** The old rule was "anywhere
but prod", which broke as soon as the cms became a standalone server: it is not part of the test system
anymore, so "not prod" said nothing useful about it. Now the standalone cms is the one instance with
`transferEnabled: true`, and the systems it publishes to leave it off, even though they run the same war.

**Groups exist because a system is more than one machine.** A transfer to `test` reaches all four test nodes;
reaching three of them would leave the nodes disagreeing about the content.

## 2. Scope: one document or everything it links to

The dialog offers two scopes:

- **This document only** — exactly the selected document. Its links travel as they are, so a link pointing at
  something the target does not have stays dangling until that document is transferred too.
- **This document and everything it links to** — the engine walks the link graph from the selected document,
  single links and link lists alike, across module borders, and transfers the whole reachable subgraph.
  Documents that already exist on the target are overwritten, so afterwards the target mirrors the source for
  everything that was reached.

Deep transfers send a document *after* the documents it links to, so a target never sees a link to something
that isn't there yet. Every document is visited once, which is what makes a box whose sub box links back to it
a piece of content rather than an endless transfer.

## 3. What arrives on the target

Documents are written with `PUT <target url>/<module>/<document>/{id}`, which upserts: the target either
updates the document under that id or creates it under that very id. Ids are therefore the same on every instance, and
that is what makes links survive the trip.

**Files travel with their documents.** An `Image` or a `FileLink` carries the *name* of a file in the cms file
storage, and the bytes are sent to the target's generated `asgimage/upload` resource before the document that
points at them — under the same name, so the document finds its file there. A file that is missing in this
instance's own file storage, or that the target rejects, is reported as a warning; the document still goes out.
Each file is sent once per target, however many documents point at it.

Nothing else on the target has to be configured — both endpoints are part of the generated rest api it already
exposes.

## 4. What does not travel

- **Documents of modules the project wrote by hand.** Only generated modules have a transfer support. A deep
  transfer that runs into a link into such a module reports it as skipped and carries on.

## 5. The result

The dialog shows, per target: how many documents were accepted, how many failed and why. Targets are
independent — one unreachable node does not stop the transfer to the others, it shows up as a failed target.
Below that come the warnings: links that could not be followed, modules that could not be transferred, files
that could not be sent.

A document that reached **every** target of the group gets a transfer timestamp, which the footer of its edit
dialog shows next to its last change:

```
Id: 7, Ts: 1789334489952, Footprint: 66D9CFFD16B6065F8DA0618E832883FD, Author: saenq,
IsoTs: 2026-09-13T21:21:29,952, lastTransferTs: 2026-09-13T21:24:10,004
```

`lastTransferTs: never` means the document was never published from this instance. A document whose `IsoTs` is
younger than its `lastTransferTs` has been edited since it last went out — which is the question the footer
exists to answer. The timestamp is kept on the document but is not content: writing it does not change the
document's author, its last change timestamp or its footprint. A document that failed on one of four nodes
keeps the old timestamp, because it is not on that node.

## 6. Auto transfer: publishing every change

A group with `autoTransfer: true` does not wait for an editor. Every document that is created, changed,
imported or deleted in the cms is published into that group by itself, through the same engine, which is what
the flag on the group means: *this group mirrors the cms*. The usual setup is `autoTransfer: true` on test and
`false` on prod — test should simply look like the cms, while production is something somebody publishes into
deliberately, after looking at the result on test.

Auto transfers always send the one document that changed, never the documents it links to. Every document is
published as it is saved, so the graph catches up by itself; a deep transfer on every save would re-publish
half the cms each time an editor fixes a typo, and overwrite whatever the target was edited with in between.

Deletions are published too: a document deleted in the cms is deleted on every target of the group with
`DELETE <target url>/<module>/<document>/{id}`. A target that does not have the document counts as a success —
what was asked for is that it is not there afterwards.

**It runs in the background.** Saving a document does not wait for the instances to answer, and a target that
is down does not make a save fail. One thread does the work, so documents arrive in the order they were saved.
Nothing is retried: the next save of that document sends it again, and a retry loop against an instance that
is down helps nobody. **The log is the only place an auto transfer failure shows up** — there is no dialog to
report it to. `net.anotheria.anosite.transfer.AutoTransferService` logs every target it could not reach at
`WARN`.

A save that changed nothing is not published, which is also what keeps two instances that point at each other
from bouncing a document back and forth: an incoming transfer writes what the sender already has, so the
document's footprint does not change and the second hop does not happen. Do not rely on that — an instance
that is a transfer target should leave `transferEnabled` off, and then it cannot publish anything at all.

### Wiring it into a module

Auto transfer is driven by a service listener, configured per module in the datadef next to the other
listeners:

```xml
<module name="ASSiteData">
  <listener class="net.anotheria.anosite.cms.listener.CRUDLogListener"/>
  <listener class="net.anotheria.anosite.cms.listener.AutoTransferListener"/>
```

One listener class covers every module — it reads the module and document name off the document itself — so a
project adds that same line to its own modules and is done. The listener is harmless on an instance that does
not publish: without an `autoTransfer` group it does nothing per save, which is why the line can stay in the
datadef of a war that runs as the cms *and* as the system published to.

In ano-site itself the listener is configured for `AnoAccessApplicationData`, `AnoAccessConfiguration`,
`ASBrand`, `ASFeature`, `ASGenericAction`, `ASGenericData`, `ASLayoutData`, `ASResourceData`, `ASSiteConfig`,
`ASSiteData`, `ASUserData` and `ASWebData` — the modules the old mechanism copied. `ASAction`,
`ASCustomAction`, `ASCustomData`, `ASExperiment`, `ASFederatedData` and `ASGeographicData` are not auto
transferred; add the listener to their datadef if they should be.

### What it replaces

The old auto transfer copied a module's `.dat` file and the image files next to it into another installation's
data directory, configured in a separate `auto-transfer-config.json` with `sourceDir` and `copyDir`, through
one `AutoTransferListener` subclass per module. It only ever worked because the cms and the systems it fed
shared a file system. A cms on its own host has nothing to copy into, so all of it is gone:
`auto-transfer-config.json` is not read anymore and can be deleted, and a project that subclassed
`AutoTransferListener` for its own modules replaces the subclass with the listener itself.

## Notes

- **The rest api is not authenticated by ano-site.** A target url is a plain url, and the generated resources
  accept writes from whoever can reach them. Guard the path the api is mapped under — `/api/*`, `/asg-api/*`,
  whatever this installation uses — in the project's proxy or with a project filter, particularly on a
  production target.
- **Transfer is not a diff.** Deep mode overwrites every document it reaches, so a linked document that was
  edited on the target is replaced by the source's version.
- **An instance that is published to should not publish.** `transferEnabled` is what separates the two roles,
  and a target that has it off cannot transfer, auto transfer or forward anything.
- **Extending it.** A project with hand written modules can join in by implementing
  `net.anotheria.anosite.transfer.ModuleTransferSupport` (or extending `AbstractModuleTransferSupport`) and
  calling `TransferSupportRegistry.register(...)` from its context listener. The generated modules register
  themselves through `TransferSupportRegistrar`, which the generated `CMSMappingsConfigurator` calls at
  startup. A hand written support that wants the `lastTransferTs` to be filled in also overrides
  `markTransferred`; the default keeps no timestamp, and the document then reads as never transferred.
