# pii-obfuscator

rewrite sensitive data within flat files and optionally store them in an external persistence service allowing entity linkages across multiple invocations/files

does not currently support more complex field tokenization past simple delimiter splitting (no delimiters within quotes, etc)
...because seriously, if you're storing a bunch of processed files in your data lake, at least run them through a CSV parser if you know its user sourced tabular data ahead of time.

## todo
* refactor public API to friendlier boundary (config objects?)
* figure out this module thing
* build out `RemoteKeyTransformer` implementation involving JDBC