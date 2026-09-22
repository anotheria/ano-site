# How to enable MCP in AnoSite

AnoSite can expose its CMS content to LLM clients over MCP (Model Context Protocol). The server is built on the
official java sdk (`io.modelcontextprotocol.sdk`) and speaks **stateless streamable http**: one endpoint, one
json response per posted request, no sessions and no long lived connections.

Out of the box it exposes tools for localization bundles and text resources; projects add their own.

The server is **off by default**. It only comes up in a project that does all four steps below.

## 1. Dependencies

The sdk is an *optional* dependency of ano-site, so it is not inherited. Declare it in the webapp's pom:

```xml
<dependency>
    <groupId>io.modelcontextprotocol.sdk</groupId>
    <artifactId>mcp-core</artifactId>
    <version>2.0.1</version>
</dependency>
<dependency>
    <groupId>io.modelcontextprotocol.sdk</groupId>
    <artifactId>mcp-json-jackson2</artifactId>
    <version>2.0.1</version>
</dependency>
```

`mcp-core` brings `reactor-core`, `mcp-json-jackson2` brings `jackson-databind`. Make sure all jackson
artifacts end up on one version — importing `com.fasterxml.jackson:jackson-bom` is the reliable way, mismatched
`jackson-databind` and `jackson-annotations` fail at runtime with a `NoClassDefFoundError`.

## 2. web.xml

```xml
<listener>
    <listener-class>net.anotheria.anosite.cms.mcp.McpBootstrap</listener-class>
</listener>
```

**Declare it after the project's own `ContextInitializer`.** Listeners run in declaration order, and
`McpBootstrap` reads the tool registry and every tool's input schema once, at startup. By then the project must
have called `ContextManager.setFactory(...)`, configured its tiers and registered its tools — the localization
tools, for instance, build the list of supported languages into their schema.

The listener registers the transport servlet and its api key filter itself; there is no `<servlet>` entry to
add. If you previously served MCP through Jersey, drop `net.anotheria.anosite.cms.mcp` from
`jersey.config.server.provider.packages` — it is not a JAX-RS resource anymore.

## 3. Configuration

Add `ano-site-mcp-server-config.json`:

```json
{
  "enabled": true,
  "apiKeys": ["generate-a-long-random-key"],
  "author": "mcp",
  "serverName": "HouseID CMS",
  "serverVersion": "1.0.0",
  "servletPath": "/mcp",
  "maxRequestSizeBytes": 1048576,
  "instructions": "Content of the HouseID CMS. Start with localization_list_bundles."
}
```

| key | default | meaning |
|---|---|---|
| `enabled` | `false` | nothing is registered while this is false |
| `apiKeys` | empty | accepted keys; with none configured the endpoint refuses to start |
| `author` | `mcp` | recorded as the author on everything the tools write |
| `serverName` / `serverVersion` | `AnoSite CMS` / `1.0.0` | what clients show in their server list |
| `servletPath` | `/mcp` | url the endpoint is mapped under |
| `maxRequestSizeBytes` | 1 MB | larger requests are rejected |
| `instructions` | empty | free text describing the CMS, handed to the client on connect |

Keys are compared in constant time and read per request, so they can be rotated without a redeploy.

## 4. Project specific tools (optional)

Implement `net.anotheria.anosite.cms.mcp.McpTool` and register it from the project's `ContextInitializer`,
before `McpBootstrap` runs:

```java
McpToolRegistry.addTool(new MeasuresMcpTools.ListEcoMeasures());
McpToolRegistry.addBundle(MeasuresMcpTools.all());
```

A tool returns its result as text; anything it throws is reported to the caller as a tool error rather than
breaking the request. Arguments are validated against the tool's `inputSchema()` before it runs.

## Smoke test

```bash
curl -s https://cms.example.com/mcp \
  -H 'X-API-Key: generate-a-long-random-key' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json, text/event-stream' \
  -d '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{
        "protocolVersion":"2025-06-18","capabilities":{},
        "clientInfo":{"name":"curl","version":"1.0"}}}'
```

```json
{"jsonrpc":"2.0","id":1,"result":{"protocolVersion":"2025-06-18",
 "capabilities":{"tools":{}},"serverInfo":{"name":"HouseID CMS","version":"1.0.0"}}}
```

Then `{"jsonrpc":"2.0","id":2,"method":"tools/list"}` to see the tools. Without a valid key the same call
answers `401` with a json-rpc error; with `enabled: false` it answers `404`.

## Connecting a client

```bash
claude mcp add --transport http anosite-cms https://cms.example.com/mcp \
  --header "X-API-Key: generate-a-long-random-key"
```

Clients that cannot set headers may pass the key as an `apiKey` request parameter instead.

## Notes

- **The endpoint is api key protected and nothing else.** Its tools write to the CMS, so treat a key like a
  CMS login: one key per client, rotated when a client goes away.
- **Only one url.** Streamable http has no separate SSE stream. If you are migrating from the old endpoint,
  `/asg-api/mcp/sse` becomes `/mcp` and every client config needs updating.
- **Protocol version 2024-11-05 is not served.** That is the version of the retired http+sse transport; a
  client asking for it gets a supported version back and has to be able to speak it.
- Tool handlers run on the request thread, so they see the thread local `CallContext` — the current language
  and the configured author. Don't hand a tool's work to another thread.
