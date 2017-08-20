# Usage Analytics Bulk Loading

This project processes Apache Web Server access log files into a comma-separated-value (CSV) file format suitable for
loading into the SCOUT data warehouse.

## Usage

Start by placing access log files you want to process in the inbox directory. You can create arbitrary directory
structures to organize your work as subdirectories in the inbox. These directories will be mirrored to the outbox as a
result of executing the log parser.

Next, run the log parser using SBT.

```
% sbt run
```

As the log parser runs, review the output. Any parsing or validation errors will be displayed on the standard error
stream. Parsed results will be placed in the outbox directory.

Next, upload your files from the outbox directory to an S3 bucket. The cascout-production-access-log-load bucket is
convenient. You can use the aws command-line interface or any S3 browser to accomplish the update.

Last, import your files into the data warehouse. I recommend loading into a temporary table before merging records into
the ```accesses``` table.

```
CREATE TABLE backfill1 (
  id           BIGINT IDENTITY(0, 1),
  hostname     VARCHAR(256)  NOT NULL,
  logname      VARCHAR(256),
  username     VARCHAR(256),
  requested_at TIMESTAMP     NOT NULL,
  first_line   VARCHAR(2000) NOT NULL,
  status       INTEGER       NOT NULL,
  bytes_sent   INTEGER       NOT NULL,
  referer      VARCHAR(2000),
  user_agent   VARCHAR(256),
  user_org_id  VARCHAR(256)
);
```

```
COPY backfill1 (hostname, logname, username, requested_at, first_line, status, bytes_sent, referer, user_agent, user_org_id)
FROM 's3://cascout-production-access-logs-load/backfill/1fcfab6e-bc89-43c2-a478-372a976baa0a/webprd1.int.canics.tabordasolutions.net_access_log/000000.csv.gz'
ACCESS_KEY_ID 'YOUR_ACCESS_KEY'
SECRET_ACCESS_KEY 'YOUR_SECRET_KEY'
GZIP
FORMAT CSV
TIMEFORMAT AS 'auto';
```