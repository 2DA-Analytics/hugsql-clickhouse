# Change Log

## [1.0.0][1.0.0] - 2019-07-15
## Changed
- Using [Hikari][hikari] connection pool instead of individual JDBC connection.

## [0.1.6][0.1.6] - 2019-07-15
## Changed
- Using [boot][boot] for builds.

## [0.1.5][0.1.5] - 2019-06-05
## Fixed
- Bug where arrays of numbers weren't working
## Changed
- Using defprotocol instead of defmulti to use type in object->string conversion

## [0.1.4][0.1.4] - 2019-04-15
## Added
- Add ability to have persistent vector strings

## [0.1.3][0.1.3] - 2019-04-12
## Added
- Unpack ClickHouseArrays

## [0.1.2][0.1.2] - 2019-04-05
## Added
- Support for numerics

## [0.1.1][0.1.1] - 2019-03-21
### Added
- Docstrings and updated README.

### Removed
- Some code around turning results into maps.

## [0.1.0][0.1.0] - 2019-03-08
### Added
- Initial release.

[0.1.0]: https://github.com/2DA-Analytics/hugsql-clickhouse/releases/tag/0.1.0
[0.1.1]: https://github.com/2DA-Analytics/hugsql-clickhouse/compare/0.1.0...0.1.1
[0.1.2]: https://github.com/2DA-Analytics/hugsql-clickhouse/compare/0.1.1...0.1.2
[0.1.3]: https://github.com/2DA-Analytics/hugsql-clickhouse/compare/0.1.2...0.1.3
[0.1.4]: https://github.com/2DA-Analytics/hugsql-clickhouse/compare/0.1.3...0.1.4
[0.1.5]: https://github.com/2DA-Analytics/hugsql-clickhouse/compare/0.1.4...0.1.5
[0.1.6]: https://github.com/2DA-Analytics/hugsql-clickhouse/compare/0.1.5...0.1.6
[1.0.0]: https://github.com/2DA-Analytics/hugsql-clickhouse/compare/0.1.6...1.0.0

[boot]: https://github.com/boot-clj
[hikari]: https://github.com/brettwooldridge/HikariCP
