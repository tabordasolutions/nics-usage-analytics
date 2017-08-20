var assert = require('assert');
var parser = require('../index');

describe('#handler()', function() {
  it('parses a base64-encoded access log line', function() {
    let csvs = [];
    parser.handler(
      {
        records: [{
          recordId: 'rec1',
          //data: new Buffer('10.30.1.187 - nicsadmin [04/Jul/2017:00:04:37 +0000] "GET /em-api/v1/workspace/system/internal-nicsweb-prd-1235601562.us-west-2.elb.amazonaws.com HTTP/1.1" 200 233 "-" "Jersey/2.17 (HttpUrlConnection 1.7.0_99)" -').toString('base64')
          data: 'H4sIAAAAAAAAAKVQy2rDMBD8FaNrE3f1sB6+GZoECj0ltyQExRZGYMvGUpqWkH/vJmkPOVcLQuyMZnbnQnoXo23d5nt0pCRv1aY6fCzW62q1IDMynIObsC2pZmBYoZgCbHdDu5qG04hITLb1oT3YukahAyIPfJ0mZ3sknN0xppblPqS8tsHXMU/2OEyNjUN3Sn4IMQ8uPQvE0zHWkx9v8NJ3yU2RlFtSdV11p5H93WTx6UK6IRfiG/TiXHCqCm4kU4ppJQ2V2igwvADBQBUMgBaFppJrAYpqIYQpuETH5DGIZHvciQpjKKfSFBLU7C8glKeQc8gh1yKbY21BvL6fulcGVJVAS6bxa/YCePbZjsx3JBOgM/h9P+45ermvNNk6uWbpXdfg/Dj++KSPpMn11ofmHv8/3K7X/fUHsTepVOUBAAA='
        }]
      },
      {},
      function (notUsed, result) {
        result.records.forEach((record) => { csvs.push(record) });
      }
    );
    assert.equal(1, csvs.length);
    let record = csvs[0];
    assert.equal('rec1', record.recordId);
    assert.equal('Ok', record.result);
    let values = new Buffer(record.data, 'base64').toString('utf8').split(',');
    assert.equal('"10.30.1.187"', values[0]);
    assert.equal('-', values[1]);
    assert.equal('nicsadmin', values[2]);
    assert.equal('04/Jul/2017:00:04:37 +0000', values[3]);
    assert.equal('GET /em-api/v1/workspace/system/internal-nicsweb-prd-1235601562.us-west-2.elb.amazonaws.com HTTP/1.1', values[4]);
    assert.equal('200', values[5]);
    assert.equal('233', values[6]);
    assert.equal('-', values[7]);
    assert.equal('Jersey/2.17 (HttpUrlConnection 1.7.0_99)', values[8]);
    assert.equal('-\n', values[9]);
  });
});
