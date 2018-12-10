using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Net;
using System.Text;

namespace MyStocksAnalysis {
    class RestApi {
        readonly private bool DEBUG_MODE = true;
        readonly private string URLtemplate = "https://marketdata.websol.barchart.com/getHistory.json?apikey={0}&symbol={1}&type=daily&startDate={2}&maxRecords={3}";
        readonly private string apiKey = "a60d02d93ff31be9627f14a80906e7bd";
        readonly private string symbol;
        readonly private string date;
        readonly private int maxRecords;
        readonly private string[] acceptedDateFormats = { "yyyyMMdd" };
        readonly private static string responseTemplateStr = "{ \"status\": { \"code\": 200, \"message\": \"Success.\" }, \"results\": [ { \"symbol\": \"IBM\", \"timestamp\": \"2018-12-07T00:00:00-05:00\", \"tradingDay\": \"2018-12-07\", \"open\": 123.9, \"high\": 124.05, \"low\": 118.87, \"close\": 119.34, \"volume\": 6947081, \"openInterest\": null } ] }";
        readonly public static Response responseTemplate = new Response(responseTemplateStr);

        public RestApi(string symbol, int maxRecords) {
            this.symbol = symbol;
            DateTime datenow = DateTime.Now;
            this.date = Convert.ToDateTime(datenow).Subtract(TimeSpan.FromDays(maxRecords * 2)).ToString("yyyyMMdd");
            this.maxRecords = maxRecords;
        }

        public Response POST() {
            if (DEBUG_MODE)
                return responseTemplate;
            WebRequest httpWebRequest = HttpWebRequest.Create(string.Format(URLtemplate, apiKey, symbol, date, maxRecords));
            httpWebRequest.ContentType = "application/json";
            httpWebRequest.Method = "POST";
            HttpWebResponse httpWebResponse = (HttpWebResponse)httpWebRequest.GetResponse();
            using (StreamReader streamReader = new StreamReader(httpWebResponse.GetResponseStream())) {
                return new Response(streamReader.ReadToEnd());
            }
        }
    }
}
