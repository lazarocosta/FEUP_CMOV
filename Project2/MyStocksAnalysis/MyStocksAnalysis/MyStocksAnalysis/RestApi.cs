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
        readonly private static string responseTemplateStr = "{\"status\":{\"code\":200,\"message\":\"Success.\"},\"results\":[{\"symbol\":\"AMD\",\"timestamp\":\"2018-10-26T00:00:00-04:00\",\"tradingDay\":\"2018-10-26\",\"open\":18.49,\"high\":18.78,\"low\":17.05,\"close\":17.63,\"volume\":119688896,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-10-29T00:00:00-04:00\",\"tradingDay\":\"2018-10-29\",\"open\":18.21,\"high\":18.23,\"low\":16.27,\"close\":16.85,\"volume\":94479504,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-10-30T00:00:00-04:00\",\"tradingDay\":\"2018-10-30\",\"open\":16.38,\"high\":17.24,\"low\":16.17,\"close\":17.2,\"volume\":99049400,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-10-31T00:00:00-04:00\",\"tradingDay\":\"2018-10-31\",\"open\":17.87,\"high\":18.34,\"low\":17.12,\"close\":18.21,\"volume\":110463600,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-01T00:00:00-04:00\",\"tradingDay\":\"2018-11-01\",\"open\":18.41,\"high\":20.33,\"low\":18.08,\"close\":20.22,\"volume\":136896400,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-02T00:00:00-04:00\",\"tradingDay\":\"2018-11-02\",\"open\":20.59,\"high\":21.06,\"low\":19.47,\"close\":20.23,\"volume\":123787904,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-05T00:00:00-05:00\",\"tradingDay\":\"2018-11-05\",\"open\":20.12,\"high\":20.18,\"low\":18.88,\"close\":19.9,\"volume\":108016704,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-06T00:00:00-05:00\",\"tradingDay\":\"2018-11-06\",\"open\":19.5,\"high\":21.65,\"low\":19.48,\"close\":20.68,\"volume\":145083600,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-07T00:00:00-05:00\",\"tradingDay\":\"2018-11-07\",\"open\":21.42,\"high\":22.22,\"low\":21.07,\"close\":21.84,\"volume\":121115696,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-08T00:00:00-05:00\",\"tradingDay\":\"2018-11-08\",\"open\":21.77,\"high\":22.08,\"low\":20.97,\"close\":21.2,\"volume\":92387504,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-09T00:00:00-05:00\",\"tradingDay\":\"2018-11-09\",\"open\":20.77,\"high\":21.19,\"low\":20.11,\"close\":21.03,\"volume\":85900704,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-12T00:00:00-05:00\",\"tradingDay\":\"2018-11-12\",\"open\":20.68,\"high\":20.85,\"low\":18.8,\"close\":19.03,\"volume\":95948096,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-13T00:00:00-05:00\",\"tradingDay\":\"2018-11-13\",\"open\":19.28,\"high\":20.02,\"low\":18.97,\"close\":19.61,\"volume\":76125904,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-14T00:00:00-05:00\",\"tradingDay\":\"2018-11-14\",\"open\":20.18,\"high\":21.11,\"low\":19.76,\"close\":20.81,\"volume\":106344200,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-15T00:00:00-05:00\",\"tradingDay\":\"2018-11-15\",\"open\":20.72,\"high\":21.77,\"low\":20.42,\"close\":21.49,\"volume\":97715400,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-16T00:00:00-05:00\",\"tradingDay\":\"2018-11-16\",\"open\":19.87,\"high\":20.97,\"low\":19.72,\"close\":20.66,\"volume\":112376496,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-19T00:00:00-05:00\",\"tradingDay\":\"2018-11-19\",\"open\":20.4,\"high\":20.59,\"low\":19.09,\"close\":19.11,\"volume\":93578200,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-20T00:00:00-05:00\",\"tradingDay\":\"2018-11-20\",\"open\":17.4,\"high\":19.58,\"low\":17.18,\"close\":19.21,\"volume\":109869400,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-21T00:00:00-05:00\",\"tradingDay\":\"2018-11-21\",\"open\":20.05,\"high\":20.31,\"low\":18.5,\"close\":18.73,\"volume\":81585600,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-23T00:00:00-05:00\",\"tradingDay\":\"2018-11-23\",\"open\":18.61,\"high\":19.83,\"low\":18.56,\"close\":19.38,\"volume\":54611200,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-26T00:00:00-05:00\",\"tradingDay\":\"2018-11-26\",\"open\":19.96,\"high\":20.19,\"low\":19.11,\"close\":20.08,\"volume\":83210896,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-27T00:00:00-05:00\",\"tradingDay\":\"2018-11-27\",\"open\":19.77,\"high\":21.45,\"low\":19.73,\"close\":21.05,\"volume\":119230096,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-28T00:00:00-05:00\",\"tradingDay\":\"2018-11-28\",\"open\":21.82,\"high\":21.88,\"low\":20.18,\"close\":21.34,\"volume\":134425200,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-29T00:00:00-05:00\",\"tradingDay\":\"2018-11-29\",\"open\":21.19,\"high\":21.61,\"low\":20.73,\"close\":21.43,\"volume\":79853696,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-11-30T00:00:00-05:00\",\"tradingDay\":\"2018-11-30\",\"open\":21.3,\"high\":21.36,\"low\":20.52,\"close\":21.3,\"volume\":82370704,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-12-03T00:00:00-05:00\",\"tradingDay\":\"2018-12-03\",\"open\":22.48,\"high\":23.75,\"low\":22.37,\"close\":23.71,\"volume\":139605408,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-12-04T00:00:00-05:00\",\"tradingDay\":\"2018-12-04\",\"open\":23.35,\"high\":23.42,\"low\":21.07,\"close\":21.12,\"volume\":127392896,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-12-06T00:00:00-05:00\",\"tradingDay\":\"2018-12-06\",\"open\":20.22,\"high\":21.41,\"low\":20.06,\"close\":21.3,\"volume\":103434600,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-12-07T00:00:00-05:00\",\"tradingDay\":\"2018-12-07\",\"open\":21.3,\"high\":21.38,\"low\":19.17,\"close\":19.46,\"volume\":105764496,\"openInterest\":null},{\"symbol\":\"AMD\",\"timestamp\":\"2018-12-10T00:00:00-05:00\",\"tradingDay\":\"2018-12-10\",\"open\":19.35,\"high\":20.13,\"low\":19.27,\"close\":19.99,\"volume\":77588850,\"openInterest\":null}]}";
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
