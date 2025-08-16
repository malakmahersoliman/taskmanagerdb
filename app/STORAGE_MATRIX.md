# Storage Options Matrix

| Option          | Data type it stores                          | Storage capacity limits                                             | ACID support | Backup difficulty | Example from this app                         |
|-----------------|----------------------------------------------|----------------------------------------------------------------------|--------------|-------------------|-----------------------------------------------|
| Files           | Unstructured (binary/text: images, PDFs)     | Limited by device free space; subject to scoped-storage/SAF rules    | ✗ No         | Medium*           | Store **image/PDF attachments**               |
| DataStore       | Small structured key–value / typed objects   | Intended for small configs (prefs/flags); not for large datasets     | ✓ Yes**      | Easy              | Store **user preferences** (e.g., dark mode)  |
| Room (SQLite)   | Structured relational data (tables/joins)    | Practical GB-scale for mobile; efficient with indexes & queries      | ✓ Yes        | Medium            | Store **Users / Projects / Tasks / relations** |

\* *Backup depends on where files live. Internal app files are covered by Android Auto Backup; shared/external locations usually require custom export/import.*  
\** *DataStore provides atomic read-modify-write (single-writer) transactions, but it’s not a relational DB (no foreign keys/joins).*
