using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Net;
using System.Text;

namespace MyStocksAnalysis {
    class RestApi {
        readonly private static bool DEBUG_MODE = false;
        readonly private static string URLtemplate = "https://marketdata.websol.barchart.com/getHistory.json?apikey={0}&symbol={1}&type=daily&startDate={2}&maxRecords={3}";
        readonly private static string apiKey = "a60d02d93ff31be9627f14a80906e7bd"; // Lázaro's API key
        readonly private static string[] acceptedDateFormats = { "yyyyMMdd" };

        public static Response POST(string symbol, int maxRecords) {
            if (DEBUG_MODE)
                return App.responseTemplates[symbol];
            string date = GenerateDate(maxRecords);
            WebRequest httpWebRequest = HttpWebRequest.Create(string.Format(URLtemplate, apiKey, symbol, date, maxRecords));
            httpWebRequest.ContentType = "application/json";
            httpWebRequest.Method = "POST";
            httpWebRequest.Timeout = 3000;
            HttpWebResponse httpWebResponse = (HttpWebResponse)httpWebRequest.GetResponse();
            using (StreamReader streamReader = new StreamReader(httpWebResponse.GetResponseStream())) {
                return new Response(streamReader.ReadToEnd());
            }
        }

        private static string GenerateDate(int maxRecords) {
            return Convert.ToDateTime(DateTime.Now).Subtract(TimeSpan.FromDays(maxRecords * 2)).ToString("yyyyMMdd");
        }
    }
}
