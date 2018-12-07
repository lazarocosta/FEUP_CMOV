using System;
using System.Collections.Generic;
using System.IO;
using System.Net;
using System.Text;

namespace MyStocksAnalysis
{
    class ApiRest
    {
        readonly private string URL = "https://marketdata.websol.barchart.com/getHistory.json?";
        readonly private string initials;
        readonly string date;
        readonly private string apiKey = "apikey=a60d02d93ff31be9627f14a80906e7bd";

        public ApiRest(string initials, string date)
        {
            this.initials = initials;
            this.date = date;
        }
        public void GetService()
        {
            var httpWebRequest = HttpWebRequest.Create(this.URL + "&" + this.apiKey + "&" + "symbol=" + this.initials + "&type=daily&" + "startDate=" + this.date);
            httpWebRequest.ContentType = "application/json";
            httpWebRequest.Method = "POST";

            var httpResponse = (HttpWebResponse)httpWebRequest.GetResponse();
           // Console.WriteLine(httpResponse);
            using (var streamReader = new StreamReader(httpResponse.GetResponseStream()))
            {
                var result = streamReader.ReadToEnd();
                Console.WriteLine(result);
            }
        }
    }
}
