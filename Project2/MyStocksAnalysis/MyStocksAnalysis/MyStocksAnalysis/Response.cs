using System;
using System.Collections.Generic;
using System.Text;

namespace MyStocksAnalysis
{
    public class Response
    {
        private string jsonResponse;
        public ResponseStatus Status { get; }
        public List<Result> Results { get; }

        public Response(string jsonResponse)
        {
            this.jsonResponse = jsonResponse;
            // TODO: parsing
        }

        override public string ToString()
        {
            return this.jsonResponse;
        }

        public class ResponseStatus
        {
            public int code { get; set; }
            public string message { get; set; }
        }

        public class Result
        {
            public string symbol { get; set; }
            public DateTime timestamp { get; set; }

            public DateTime tradingDay { get; set; }
            public double open { get; set; }
            public double hight { get; set; }
            public double low { get; set; }
            public double close{ get; set; }
            public int volume { get; set; }
            public double openInterest { get; set; }
        }
    }
}
