using System;
using System.Collections.Generic;
using System.Text;
using Newtonsoft.Json.Linq;

namespace MyStocksAnalysis {
    public class Response {
        readonly public ResponseStatus status;
        readonly public List<Result> results;
        readonly private string jsonResponse;

        public Response(string jsonResponse) {
            this.jsonResponse = jsonResponse;
            JObject jObject = JObject.Parse(this.jsonResponse);
            status = new ResponseStatus(jObject["status"]);
            results = new List<Result>();
            foreach (JToken result in jObject["results"])
                results.Add(new Result(result));
        }

        override public string ToString() {
            return this.jsonResponse;
        }

        public class ResponseStatus {
            readonly public int code;
            readonly public string message;

            public ResponseStatus(JToken status) {
                this.code = int.Parse(status.SelectToken("code").ToString());
                this.message = status.SelectToken("message").ToString();
            }
        }

        public class Result
        {
            readonly public string symbol;
            readonly public DateTime timestamp;
            readonly public DateTime tradingDay;
            readonly public double open;
            readonly public double high;
            readonly public double low;
            readonly public double close;
            readonly public int volume;
            readonly public string openInterest;

            public Result(JToken result) {
                this.symbol = result.SelectToken("symbol").ToString();
                this.timestamp = DateTime.Parse(result.SelectToken("timestamp").ToString());
                this.tradingDay = DateTime.Parse(result.SelectToken("tradingDay").ToString());
                this.open = double.Parse(result.SelectToken("open").ToString());
                this.high = double.Parse(result.SelectToken("high").ToString());
                this.low = double.Parse(result.SelectToken("low").ToString());
                this.close = double.Parse(result.SelectToken("close").ToString());
                this.volume = int.Parse(result.SelectToken("volume").ToString());
                this.openInterest = result.SelectToken("openInterest").ToString();
            }
        }
    }
}
