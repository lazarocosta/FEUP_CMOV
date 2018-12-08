using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Net;
using System.Text;
using Newtonsoft.Json;

namespace MyStocksAnalysis {
    class ApiRest {
        readonly private bool DEBUG_MODE = true;
        readonly private string URLtemplate = "https://marketdata.websol.barchart.com/getHistory.json?apikey={0}&symbol={1}&type=daily&startDate={2}";
        readonly private string apiKey = "a60d02d93ff31be9627f14a80906e7bd";
        readonly private string symbol;
        readonly private string date;
        readonly private string[] acceptedDateFormats = { "yyyyMMdd" };
        readonly private static string responseTemplateStr = "{ \"status\": { \"code\": 200, \"message\": \"Success.\" }, \"results\": [ { \"symbol\": \"IBM\", \"timestamp\": \"2018-12-07T00:00:00-05:00\", \"tradingDay\": \"2018-12-07\", \"open\": 123.9, \"high\": 124.05, \"low\": 118.87, \"close\": 119.34, \"volume\": 6947081, \"openInterest\": null } ] }";
        readonly public static object responseTemplate = JsonConvert.DeserializeObject(responseTemplateStr);

        public ApiRest(string symbol, string date) {
            DateTime dateTime = new DateTime();
            if (!DateTime.TryParseExact(date, acceptedDateFormats, new CultureInfo("en-US"), DateTimeStyles.None, out dateTime))
                throw new ArgumentException("Invalid date format.");
            this.symbol = symbol;
            this.date = date;
        }

        public object POST() {
            if (DEBUG_MODE)
                return responseTemplate;
            WebRequest httpWebRequest = HttpWebRequest.Create(string.Format(URLtemplate, apiKey, symbol, date));
            httpWebRequest.ContentType = "application/json";
            httpWebRequest.Method = "POST";
            HttpWebResponse httpWebResponse = (HttpWebResponse)httpWebRequest.GetResponse();
            using (StreamReader streamReader = new StreamReader(httpWebResponse.GetResponseStream())) {
                string response = streamReader.ReadToEnd();
                return JsonConvert.DeserializeObject(response);
            }
        }
    }
}
