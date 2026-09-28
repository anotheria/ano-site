# How to transfer documents in AnoSite

Transfer publishes a cms document to other instances: the editor clicks the transfer icon in a list or the
transfer button in an edit dialog, picks a target group and a scope, and the document is written to every
instance in that group through the generated rest api.

It is **off by default**. An instance only publishes into other systems if it was explicitly set up to.

## 1. Configuration

Everything lives in `anositeconfig.json`. There is no separate transfer config file.

```json
{
  "transferEnabled": true,
  "@transferTargetGroups": [
    {
      "name": "test",
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

Nothing else on the target has to be configured — the endpoint is the generated rest api it already exposes.

## 4. What does not travel

- **Binaries.** `Image` and `FileLink` documents carry the *name* of a file in the cms file storage, not the
  file. The document transfers, the bytes do not. Every transferred document that points at a file is listed in
  the result so the editor knows what still has to be synced; the target's generated `asgimage/upload`
  resource takes the file.
- **Documents of modules the project wrote by hand.** Only generated modules have a transfer support. A deep
  transfer that runs into a link into such a module reports it as skipped and carries on.

## 5. The result

The dialog shows, per target: how many documents were accepted, how many failed and why. Targets are
independent — one unreachable node does not stop the transfer to the others, it shows up as a failed target.
Below that come the warnings: links that could not be followed, modules that could not be transferred, files
left behind.

## Notes

- **The rest api is not authenticated by ano-site.** A target url is a plain url, and the generated resources
  accept writes from whoever can reach them. Guard the path the api is mapped under — `/api/*`, `/asg-api/*`,
  whatever this installation uses — in the project's proxy or with a project filter, particularly on a
  production target.
- **Transfer is not a diff.** Deep mode overwrites every document it reaches, so a linked document that was
  edited on the target is replaced by the source's version.
- **Extending it.** A project with hand written modules can join in by implementing
  `net.anotheria.anosite.transfer.ModuleTransferSupport` (or extending `AbstractModuleTransferSupport`) and
  calling `TransferSupportRegistry.register(...)` from its context listener. The generated modules register
  themselves through `TransferSupportRegistrar`, which the generated `CMSMappingsConfigurator` calls at
  startup.
