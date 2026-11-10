# Changelog

All notable changes to ano-site. Add an entry under **Unreleased** with every change; it becomes the section of the next release.

Entries up to 5.1.1 were reconstructed from the git history.

## Unreleased

## 5.1.1 (2026-11-10)
* Content API
* Reworked transfer mechanism (see [HowToTransferDocumentsInAnosite.md](HowToTransferDocumentsInAnosite.md))
* More MCP tools, including `localization_create_bundle`
* Added missing `woff2` content type to resource delivery
* Bump `org.json:json` to 20260719

## 5.1.0 (2026-10-02)
* Auto-transfer, enabled for all built-in documents (see [HowTo Configure autotransfer](https://github.com/anotheria/ano-site/wiki/HowTo-Configure-autotransfer))

## 5.0.7 (2026-09-28)
* New system-to-system transfer implementation
* Switched to a more modern MCP implementation (Jackson based); documented how to enable the MCP server in a project
* `LocalizationTools`: support arbitrary languages, source languages from `CallContext` instead of project-specific `LanguageUtils`
* Fixed `LocalizationTools` corrupting bundle messages via `Properties.store()`
* Removed obsolete templates, dependency updates

## 5.0.6 (2026-06-29)
* Definitions moved from `etc/def` to `src/main/definitions`; copying of anosite resources adjusted accordingly
* Added MoSKito dependencies for AOP generation, so generated code can be monitored too

## 5.0.5 (2026-06-26)
* New MCP tool `localization_set_keys` for bulk bundle updates
* Fixed MCP SSE endpoint exhausting the Tomcat worker pool

## 5.0.4 (2026-06-17)
* New MCP endpoint with tool registry and localization / text-resource tools; `addTool` / `addBundle` exposed for project-level registration
* `ContentPageServlet` uses `getObjectInfo().getLastChangeTimestamp()`
* Removed unused module and its image auto-transfer listener

## 5.0.3 (2026-06-08)
* ano-doc version is now dynamic

## 5.0.1 (2026-06-06)
* Removed an unnecessary exception

## 5.0.0 (2026-06-02)
* **Java 21**
* Migrated to standard Maven layout (sources, tests, static resources, appdata)
* Fixed `TestHttpServletRequest` for Jakarta Servlet 6.1; removed `AnositeTestSuite`
* ano-doc-plugin no longer requires the ano-doc dependency
* Dropped legacy sonatype-oss-release profile

## 4.3.3.1 (2026-06-01)
* Improved `ContentPageServlet` STOP logging ([#57](https://github.com/anotheria/ano-site/issues/57))

## 4.3.3 (2026-05-18)
* Tool to compare two l10n bundles ([#55](https://github.com/anotheria/ano-site/issues/55))
* Removed obsolete dependency on ano-net

## 4.3.2 / 4.3.2.1 (2025-02-07 / 2025-02-10)
* Version alignment with ano-doc, no functional changes

## 4.3.1 (2025-01-22)
* Removed wizard support ([#54](https://github.com/anotheria/ano-site/issues/54))
* [#47](https://github.com/anotheria/ano-site/issues/47), [#48](https://github.com/anotheria/ano-site/issues/48)
* Menu path corrected to relative path

## 4.3.0 (2024-12-10)
* Cloud buckets for CMS content (see [HowTo Setup Bucket Storage](https://github.com/anotheria/ano-site/wiki/HowTo-Setup-Bucket-Storage))
* Replaced ano-web with own files, removed old ano-doc configuration
* `MenuMaf`, name on site bean
* Sorting option for localization bundles
* Usage search for localization bundles: as parents, in mail templates, in action mappings
* Image info via ano-site action method instead of decorators
* Fixed order of stored translated chunk keys

## 4.1.2 (2024-08-19)
* ano-doc upgrade

## 4.1.1 (2024-04-16)
* "Show usages" for brands and guards
* Feature processing of DataObject in guards

## 4.1.0 (2024-03-22)
* Tool to show the difference between two localization bundles
* Resource-delivery login only for resource URLs not starting with `http`

## 4.0.0 (2023-12-24)
* **Javax → Jakarta EE 9** migration (incl. `ws.rs`)
* **Java 11**
* Transfer button on the single-item view in the CMS
* Dependency bumps: httpclient 4.5.13, org.json 20231013, jettison 1.5.4

## 3.2.2 (2023-09-07)
* Auto-translation for localization bundles via OpenAI (bulk and specific translation with preview, MoSKito monitoring)
* PDF templates tab, with localization bundles
* Transfer action for the Content module
* More brand attributes, brand in request scope, brand config with language
* Removed transitive org.json dependency (MoSKito / ano-plass upgrade)

## 3.2.1 (2023-06-19)
* Brands module: BrandFeature, brand guard, logo and media links per brand, brand session attribute
* Localization environment for brands
* `urlsToMap` on page view

## 3.2.0 (2023-01-11)
* Jersey `MultiPartFeature`

## 3.1.0 (2023-01-10)
* Export / import of localization bundles
* Dependency updates

## 3.0.0 (2022-10-28)
* Migration to ano-maf 3.0.0 and MoSKito 3
* `activeInProduction` flag on features (replaces SystemConfigurationAPI usage)
* MJML mail-template support
* Parent bundles for localizations, variable processor in localization bundle texts, ASG tool to generate parent localizations
* Transfer to prod for mail templates, users, picture resources
* Content listeners
* Init parameters for 404 and 500 pages
* Removed blog functionality
* ProducerId is now just the class name; improved source monitoring

## 2.6.11 (2020-05-21)
* ano-doc 2.6.11

## 2.6.10 (2020-04-07)
* Transfer to prod for text resources
* Application name for anosite
* Replaced deprecated `ActionForward` with `ActionCommand`

## 2.6.9 (2019-11-19)
* Push-notification CMS document
* Fixed Jersey client configuration for HTTPS

## 2.6.8 (2019-09-12)
* MoSKito upgrade

## 2.6.5 (2019-02-11)
* Auto transfer for modules and resources

## 2.6.4 (2018-01-23)
* Transfer to multiple domains, with progress display and warning overlay before transfer to prod
* Concept of "System", refactored `SystemIs*` guards, PROD environment icon
* Experiments section
* Localization bundles in mail templates

## 2.6.3 (2017-01-30)
* Transfer to prod (copy-to-prod icon, target URL configuration), including deep dependencies for localization bundles
* Variable processor
* Alt tag for pictures
* Links in localization bundles
* Eliminated render-blocking JS / CSS above the fold
* Deleting CMS content files no longer destroys admin roles

## 2.6.2 (2016-03-30)
* DONTSHOW guard moved to core CMS
* Trace logging (ANOSITE-79)

## 2.6.1 (2016-01-26)
* Feature toggles and FeatureAPI
* Expression templates for meta keywords / description on PageBean and SiteBean
* "Show usages" for localization bundles

## 2.5.4 and earlier (2007-2014)
About 800 commits from the initial import in September 2007 onward. Highlights:
* Pages, boxes, layouts (incl. plain text/HTML layouts), header/footer, navigation items, breadcrumbs
* Page aliases, redirect filter, entry points (redirecting or masking)
* Guards (context language, system environment, Scripts / MediaLinks)
* Text resources, localization bundles, TranslationBox, missing-translation filters
* Resource-delivering servlet for static / CMS resources with versioned URLs and font support
* CMS user manager, roles, password hashing, CMS locking, validators, page preview, "show usages"
* Session distribution via ano-prise
* Migration from Ant/Ivy to Maven, SLF4J
